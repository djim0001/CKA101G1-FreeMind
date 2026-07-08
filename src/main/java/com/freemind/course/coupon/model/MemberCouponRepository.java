package com.freemind.course.coupon.model;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.freemind.login.member.model.Member;

public interface MemberCouponRepository extends JpaRepository<MemberCoupon, Integer> {

//	Page<MemberCoupon> findByMemberMemberId(Integer memberId, Pageable pageable);
	Page<MemberCoupon> findByMember(Member member, Pageable pageable);
	List<MemberCoupon> findByMember(Member member);
	
	List<MemberCoupon> findByCouponStatus(Byte couponStatus);
}
