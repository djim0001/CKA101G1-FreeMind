package com.freemind.course.order.model;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.freemind.login.member.model.Member;

public interface OrderDetailRepository
        extends JpaRepository<OrderDetail, OrderDetail.CompositeOrderDetail> {

    Page<OrderDetail> findByCourseOrderMemberMemberId(
            Integer memberId,
            Pageable pageable
    );
    
    // 已付款且已解鎖的課程
    @Query("""
    	    SELECT od
    	    FROM OrderDetail od
    	    WHERE od.courseOrder.member = :member
    	      AND od.courseOrder.paymentStatus = 1
    	      AND od.coursePermission = 1
    	""")
    	Page<OrderDetail> findAccessibleOrderDetailsByMember(
    	        @Param("member") Member member,
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
    
    // 付款後同步解鎖權限
    @Modifying
    @Query("""
        UPDATE OrderDetail od
        SET od.coursePermission = 1
        WHERE od.courseOrder.courseOrderId = :courseOrderId
    """)
    int enableCoursePermission(
            @Param("courseOrderId") Integer courseOrderId
    );

}