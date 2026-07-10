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

import com.freemind.login.member.model.Member;

import jakarta.transaction.Transactional;

@Service
public class MemberCouponService {

	private final MemberCouponRepository repository;
	private final CouponRepository couponRepository;
	private final StringRedisTemplate stringRedisTemplate;

	@Value("${app.coupon.page-size:5}")
	private int couponPageSize;

	public MemberCouponService(
			MemberCouponRepository repository,
			CouponRepository couponRepository,
			StringRedisTemplate stringRedisTemplate) {
		this.repository = repository;
		this.couponRepository = couponRepository;
		this.stringRedisTemplate = stringRedisTemplate;
	}
	
	public void addCoupon(MemberCoupon memCoupon) {
		repository.save(memCoupon);
	}
	
	public void updateCoupon(MemberCoupon memCoupon) {
		repository.save(memCoupon);
	}
	
	public MemberCoupon getOneByPK(Integer couponSerialNo) {
		// 代表回傳直可能為Optional.empty() => 不是 null -- Jpa 用法 --
		Optional<MemberCoupon> optional = repository.findById(couponSerialNo);
		return optional.orElse(null);
	}
	
	public List<MemberCoupon> getAllMemberCoupon(){
		return repository.findAll();
	}
	
	public List<MemberCoupon> getAllMemberCoupon(Map<String, String[]> map){
		return repository.findAll();
	}
	
	public List<MemberCoupon> getAllMyValidCoupon(Member member) {
		
		List<MemberCoupon> coupons = repository.findByMember(member);
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
		return repository.findByMember(member);
	}
	public Page<MemberCoupon> getMemberCouponPage(Integer page) {

        Pageable pageable = PageRequest.of(
            page,
            couponPageSize,
            Sort.by("couponSerialNo").ascending()
        );

        return repository.findAll(pageable);
    }
	public Page<MemberCoupon> getMyCoupons(Member member, Integer page) {
		
		Pageable pageable = PageRequest.of(
				page,
				couponPageSize,
				Sort.by("couponSerialNo").ascending()
				);
		
		return repository.findByMember(member, pageable);
	}
	
	@Transactional
    public String claimCoupon(Integer memberId, Integer couponId) {

        String stockKey = "coupon:stock:" + couponId;
        String claimedKey = "coupon:claimed:" + couponId;

        String luaScript = """
                if redis.call('SISMEMBER', KEYS[2], ARGV[1]) == 1 then
                    return -1
                end

                local stock = tonumber(redis.call('GET', KEYS[1]))

                if stock == nil or stock <= 0 then
                    return 0
                end

                redis.call('DECR', KEYS[1])
                redis.call('SADD', KEYS[2], ARGV[1])

                return 1
                """;

        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(luaScript);
        redisScript.setResultType(Long.class);

        Long result = stringRedisTemplate.execute(
                redisScript,
                List.of(stockKey, claimedKey),
                String.valueOf(memberId)
        );

        if (result == null) {
            return "系統錯誤，請稍後再試";
        }

        if (result == -1) {
            return "你已經領取過此優惠券";
        }

        if (result == 0) {
            return "優惠券已被領完";
        }

        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new RuntimeException("查無此優惠券"));

        Member member = new Member();
        member.setMemberId(memberId);

        MemberCoupon memberCoupon = new MemberCoupon();
        memberCoupon.setCoupon(coupon);
        memberCoupon.setMember(member);
        memberCoupon.setCouponStartAt(LocalDateTime.now());
        memberCoupon.setCouponEndAt(
        	    LocalDateTime.now().plusDays(coupon.getDiscountDuration())
        	);

        repository.save(memberCoupon);

        return "領取成功";
    }
	
}
