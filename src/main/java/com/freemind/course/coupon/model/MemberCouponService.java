package com.freemind.course.coupon.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import com.freemind.course.coupon.dto.CouponClaimResult;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberRepository;

import jakarta.transaction.Transactional;

@Service
public class MemberCouponService {

	private static final String CLAIM_COUPON_SCRIPT = """
			local stockKey = KEYS[1]
			local claimedKey = KEYS[2]

			local memberId = ARGV[1]

			if redis.call('EXISTS', stockKey) == 0 then
			    return -1
			end

			if redis.call('SISMEMBER', claimedKey, memberId) == 1 then
			    return -2
			end

			local stock = tonumber(redis.call('GET', stockKey))

			if stock == nil or stock <= 0 then
			    return 0
			end

			redis.call('DECR', stockKey)
			redis.call('SADD', claimedKey, memberId)

			local ttl = redis.call('TTL', stockKey)

			if ttl > 0 then
			    redis.call('EXPIRE', claimedKey, ttl)
			end

			return 1
			""";
	@Value("${app.coupon.page-size:5}")
	private int couponPageSize;
	private final CouponRepository couponRepository;
	private final MemberRepository memberRepository;
	private final MemberCouponRepository memberCouponRepository;
	private final StringRedisTemplate redisTemplate;
	private final DefaultRedisScript<Long> claimCouponScript;

	public MemberCouponService(CouponRepository couponRepository, MemberRepository memberRepository,
			MemberCouponRepository memberCouponRepository, StringRedisTemplate redisTemplate) {

		this.couponRepository = couponRepository;
		this.memberRepository = memberRepository;
		this.memberCouponRepository = memberCouponRepository;
		this.redisTemplate = redisTemplate;

		this.claimCouponScript = new DefaultRedisScript<>();

		this.claimCouponScript.setScriptText(CLAIM_COUPON_SCRIPT);

		this.claimCouponScript.setResultType(Long.class);
	}

	public void addCoupon(MemberCoupon memCoupon) {
		memberCouponRepository.save(memCoupon);
	}

	public void updateCoupon(MemberCoupon memCoupon) {
		memberCouponRepository.save(memCoupon);
	}

	public MemberCoupon getOneByPK(Integer couponSerialNo) {
		// 代表回傳直可能為Optional.empty() => 不是 null -- Jpa 用法 --
		Optional<MemberCoupon> optional = memberCouponRepository.findById(couponSerialNo);
		return optional.orElse(null);
	}

	public List<MemberCoupon> getAllMemberCoupon() {
		return memberCouponRepository.findAll();
	}

	public List<MemberCoupon> getAllMemberCoupon(Map<String, String[]> map) {
		return memberCouponRepository.findAll();
	}

	public List<MemberCoupon> getAllMyValidCoupon(Member member) {

		List<MemberCoupon> coupons = memberCouponRepository.findByMember(member);
		List<MemberCoupon> couponsValid = new ArrayList<>();

		if (coupons == null || coupons.isEmpty()) {
			return couponsValid;
		}

		for (MemberCoupon coupon : coupons) {
			if (coupon.isCouponValid()) {
				couponsValid.add(coupon);
			}
		}

		return couponsValid;
	}

	public List<MemberCoupon> getAllMyCoupon(Member member) {
		return memberCouponRepository.findByMember(member);
	}

	public Page<MemberCoupon> getMemberCouponPage(Integer page) {

		Pageable pageable = PageRequest.of(page, couponPageSize, Sort.by("couponSerialNo").ascending());

		return memberCouponRepository.findAll(pageable);
	}

	public Page<MemberCoupon> getMyCoupons(Member member, Integer page) {

		Pageable pageable = PageRequest.of(page, couponPageSize, Sort.by("couponSerialNo").ascending());

		return memberCouponRepository.findByMember(member, pageable);
	}

	 public List<Coupon> getAvailableCoupons() {

        List<Coupon> allCoupons =
            couponRepository.findAll();

        return allCoupons.stream()
            .filter(coupon -> {

                String stockKey =
                    "coupon:stock:"
                    + coupon.getCouponId();

                String stock =
                    redisTemplate.opsForValue()
                        .get(stockKey);

                if (stock == null) {
                    return false;
                }

                try {
                    return Long.parseLong(stock) > 0;
                } catch (NumberFormatException e) {
                    return false;
                }
            })
            .toList();
    }

    @Transactional
    public CouponClaimResult claimCoupon(
            Integer memberId,
            Integer couponId) {

        Coupon coupon =
            couponRepository.findById(couponId)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "優惠券不存在"
                    )
                );

        Member member =
            memberRepository.findById(memberId)
                .orElseThrow(() ->
                    new IllegalArgumentException(
                        "會員不存在"
                    )
                );

        /*
         * 資料庫先檢查。
         * 即使 Redis 資料遺失，也可避免再次新增。
         */
        boolean alreadyClaimed =
            memberCouponRepository
                .existsByMemberMemberIdAndCouponCouponId(
                    memberId,
                    couponId
                );

        if (alreadyClaimed) {
            return CouponClaimResult.ALREADY_CLAIMED;
        }

        String stockKey =
            "coupon:stock:" + couponId;

        String claimedKey =
            "coupon:claimed:" + couponId;

        Long result = redisTemplate.execute(
            claimCouponScript,
            List.of(stockKey, claimedKey),
            memberId.toString()
        );

        if (result == null || result == -1L) {
            return CouponClaimResult.NOT_PUBLISHED;
        }

        if (result == -2L) {
            return CouponClaimResult.ALREADY_CLAIMED;
        }

        if (result == 0L) {
            return CouponClaimResult.SOLD_OUT;
        }

        try {
            LocalDateTime now =
                LocalDateTime.now();

            MemberCoupon memberCoupon =
                new MemberCoupon();

            memberCoupon.setCoupon(coupon);
            memberCoupon.setMember(member);
            memberCoupon.setCouponStatus((byte) 0);
            memberCoupon.setCouponStartAt(now);

            memberCoupon.setCouponEndAt(
                now.plusDays(
                    coupon.getDiscountDuration()
                )
            );

            memberCouponRepository.save(
                memberCoupon
            );

            return CouponClaimResult.SUCCESS;

        } catch (RuntimeException e) {

            /*
             * MySQL 新增失敗，補回 Redis。
             */
            redisTemplate.opsForValue()
                .increment(stockKey);

            redisTemplate.opsForSet()
                .remove(
                    claimedKey,
                    memberId.toString()
                );

            throw e;
        }
    }

}
