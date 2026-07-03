package com.freemind.course.coupon.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CouponRepository extends JpaRepository<Coupon, Integer>{
	
	@Query("select count(*) from Coupon")
	Integer getCouponCount();
	

}
