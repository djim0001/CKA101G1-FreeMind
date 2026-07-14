package com.freemind.course.coupon.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class CouponService {

	private final CouponRepository repository;
	private final StringRedisTemplate stringRedisTemplate;
	
	@Value("${app.coupon.page-size:5}")
	private int couponPageSize;
	
	public CouponService(CouponRepository repository,
			MemberCouponRepository memberCouponRepository,
			StringRedisTemplate stringRedisTemplate) {
		this.repository = repository;
		this.stringRedisTemplate = stringRedisTemplate;
	}
	
	public void addCoupon(Coupon coupon) {
		repository.save(coupon);
	}
	
	public void updateCoupon(Coupon coupon) {
		repository.save(coupon);
	}
	
	public Coupon getOneCoupon(Integer couponId) {
		// 代表回傳直可能為Optional.empty() => 不是 null -- Jpa 用法 --
		Optional<Coupon> optional = repository.findById(couponId);
		return optional.orElse(null);
	}
	
	public List<Coupon> getAllCoupon(){
		return repository.findAll();
	}
	
	public List<Coupon> getAllCoupon(Map<String, String[]> map){
		return repository.findAll();
	}
	
	public Page<Coupon> getCouponPage(Integer page) {

        Pageable pageable = PageRequest.of(
            page,
            couponPageSize,
            Sort.by("couponId").ascending()
        );

        return repository.findAll(pageable);
    }
	
	public void publishCoupon(Integer couponId, Integer stock, Long ttlHours) {

		if (couponId == null) {
			throw new IllegalArgumentException("優惠券編號不能為空");
		}

		if (stock == null || stock <= 0) {
			throw new IllegalArgumentException("發放數量必須大於 0");
		}

		if (ttlHours == null || ttlHours <= 0) {
			throw new IllegalArgumentException("有效時數必須大於 0");
		}

		boolean exists = repository.existsById(couponId);

		if (!exists) {
			throw new IllegalArgumentException("找不到指定優惠券");
		}

		String stockKey = "coupon:stock:" + couponId;

		String publishedKey = "coupon:published:" + couponId;

		stringRedisTemplate.opsForValue().set(stockKey, stock.toString(), ttlHours, TimeUnit.HOURS);
	}
	 
	
	
}
