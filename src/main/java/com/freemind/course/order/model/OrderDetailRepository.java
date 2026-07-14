package com.freemind.course.order.model;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderDetailRepository
        extends JpaRepository<OrderDetail, OrderDetail.CompositeOrderDetail> {

    Page<OrderDetail> findByCourseOrderMemberMemberId(
            Integer memberId,
            Pageable pageable
    );
    
    // 課程已付款且已解鎖
    @Query("""
    	    SELECT COUNT(od) > 0
    	    FROM OrderDetail od
    	    JOIN od.courseOrder co
    	    JOIN od.course c
    	    JOIN co.member m
    	    WHERE m.memberId = :memberId
    	      AND c.courseId = :courseId
    	      AND co.paymentStatus = 1
    	      AND od.coursePermission = 1
    	""")
    	boolean existsPermission(
    	    @Param("memberId") Integer memberId,
    	    @Param("courseId") Integer courseId
    	);
    
    // 課程已付款
    @Query("""
    	    SELECT COUNT(od) > 0
    	    FROM OrderDetail od
    	    WHERE od.courseOrder.member.memberId = :memberId
    	      AND od.course.courseId = :courseId
    	      AND od.courseOrder.paymentStatus = 1
    	""")
    	boolean existsPaidCourse(
    	        @Param("memberId") Integer memberId,
    	        @Param("courseId") Integer courseId
    	);
    
    // 課程未付款
    @Query("""
    	    SELECT COUNT(od) > 0
    	    FROM OrderDetail od
    	    WHERE od.courseOrder.member.memberId = :memberId
    	      AND od.course.courseId = :courseId
    	      AND od.courseOrder.paymentStatus = 0
    	""")
    	boolean existsPendingCourse(
    	        @Param("memberId") Integer memberId,
    	        @Param("courseId") Integer courseId
    	);
    
    List<OrderDetail> findByCourseOrderCourseOrderId(Integer courseOrderId);

}