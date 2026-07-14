package com.freemind.course.order.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.freemind.course.coupon.model.MemberCoupon;
import com.freemind.login.member.model.Member;

public interface CourseOrderRepository extends JpaRepository<CourseOrder, Integer> {

	List<CourseOrder> findByMember(Member member);

	Page<CourseOrder> findByMember(Member member, Pageable pageable);

	@Query("""
			    SELECT co.memberCoupon
			    FROM CourseOrder co
			    WHERE co.courseOrderId = :courseOrderId
			""")
	Optional<MemberCoupon> findMemberCouponByCourseOrderId(@Param("courseOrderId") Integer courseOrderId);

}
