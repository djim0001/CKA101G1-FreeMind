package com.freemind.course.coupon.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CouponService {

	@Autowired
	CouponRepository repository;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	public void addCoupon(CouponVO couponVO) {
		repository.save(couponVO);
	}
	
	public void updateCoupon(CouponVO couponVO) {
		repository.save(couponVO);
	}
	
	public CouponVO getOneCoupon(Integer couponId) {
		// 代表回傳直可能為Optional.empty() => 不是 null -- Jpa 用法 --
		Optional<CouponVO> optional = repository.findById(couponId);
		return optional.orElse(null);
	}
	
	public List<CouponVO> getAll(){
		return repository.findAll();
	}
	
	public List<CouponVO> getAll(Map<String, String[]> map){
		return repository.findAll();
	}
	
}
