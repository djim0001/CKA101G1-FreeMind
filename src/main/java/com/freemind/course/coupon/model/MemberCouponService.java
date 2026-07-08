package com.freemind.course.coupon.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.freemind.login.member.model.Member;

@Service
public class MemberCouponService {

	private final MemberCouponRepository repository;

	@Value("${app.coupon.page-size:5}")
	private int couponPageSize;

	public MemberCouponService(MemberCouponRepository repository) {
		this.repository = repository;
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
	
}
