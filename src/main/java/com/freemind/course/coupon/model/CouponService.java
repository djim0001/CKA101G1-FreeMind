package com.freemind.course.coupon.model;

import java.time.LocalDateTime;
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
	
	public List<Coupon> getAvailableCoupons(){
		return null;
	}
	public void initCouponStock(Integer couponId, Integer stock) {
	    String stockKey = "coupon:stock:" + couponId;
	    stringRedisTemplate.opsForValue().set(stockKey, String.valueOf(stock));
	}
	
	
}
