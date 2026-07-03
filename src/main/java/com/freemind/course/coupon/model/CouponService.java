package com.freemind.course.coupon.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CouponService {

	@Autowired
	CouponRepository repository;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Value("${app.coupon.page-size:5}")
	private int couponPageSize;
	
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
	
	public Integer getPageTotal() {
		Integer count = repository.getCouponCount();
		Integer total = count % couponPageSize;
		Integer pageTotal = 1;
		if(total != 0) pageTotal = count / couponPageSize + 1;
		else pageTotal = (count / couponPageSize);
		
		return pageTotal;
	}
	
}
