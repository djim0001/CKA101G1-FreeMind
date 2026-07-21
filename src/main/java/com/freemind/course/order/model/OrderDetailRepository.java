package com.freemind.course.order.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.freemind.login.member.model.Member;
import com.freemind.login.psychologist.entity.Psychologist;

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
    
    Optional<OrderDetail>
    findFirstByCourseCourseIdAndCourseOrderMemberAndCourseOrderPaymentStatusAndCoursePermissionOrderByCourseOrderOrderedAtDesc(
            Integer courseId,
            Member member,
            Byte paymentStatus,
            Byte coursePermission
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
    
    // 所有可評價的我的課程訂單明細
    List<OrderDetail>
    findByCourseOrderMemberMemberIdAndCourseOrderPaymentStatusAndCoursePermissionAndReviewedAtIsNull(
            Integer memberId,
            Byte paymentStatus,
            Byte coursePermission
    );

    // 所有已評價的我的課程訂單明細
    Page<OrderDetail>
    findByCourseOrderMemberMemberIdAndCourseOrderPaymentStatusAndCoursePermissionAndReviewedAtNotNull(
    		Integer memberId,
    		Byte paymentStatus,
    		Byte coursePermission,
    		Pageable pageable
    		);
    @Query("""
    	    SELECT od
    	    FROM OrderDetail od
    	    WHERE od.courseOrder.member.memberId = :memberId
    	      AND od.courseOrder.courseOrderId = :courseOrderId
    	      AND od.course.courseId = :courseId
    	      AND od.courseOrder.paymentStatus = 1
    	      AND od.coursePermission = 1
    	""")
    	Optional<OrderDetail> findAccessibleOrderDetail(
    	        @Param("memberId") Integer memberId,
    	        @Param("courseOrderId") Integer courseOrderId,
    	        @Param("courseId") Integer courseId
    	);
    // 加總該心理師課程的成交金額
    @Query("""
            SELECT COALESCE(SUM(od.discountedPrice), 0)
            FROM OrderDetail od
            JOIN od.course c
            JOIN c.psychologist p
            JOIN od.courseOrder co
            WHERE p.psychId = :psychId
              AND co.paymentStatus = 1
              AND co.orderedAt >= :startDateTime
              AND co.orderedAt < :endDateTime
        """)
        Long sumMonthlySalesByPsychologist(
                @Param("psychId") Integer psychId,
                @Param("startDateTime") LocalDateTime startDateTime,
                @Param("endDateTime") LocalDateTime endDateTime
        );
    // 一個心理師歷史總售出課程數
    @Query("""
    	    SELECT COUNT(od)
    	    FROM OrderDetail od
    	    WHERE od.course.psychologist.psychId = :psychId
    	      AND od.courseOrder.paymentStatus = 1
    	""")
    	int countAllSoldCoursesByPsychologist(
    	        @Param("psychId") Integer psychId
    	);
    // 心理師營收排平
    @Query("""
            SELECT p
            FROM OrderDetail od
            JOIN od.course c
            JOIN c.psychologist p
            JOIN od.courseOrder co
            WHERE co.paymentStatus = 1
            GROUP BY p
            ORDER BY SUM(od.discountedPrice) DESC
        """)
        List<Psychologist> findTopPsychologistsByRevenue(Pageable pageable);
    
    @Query("""
            SELECT COUNT(DISTINCT co)
            FROM OrderDetail od
            JOIN od.courseOrder co
            JOIN od.course c
            JOIN c.psychologist p
            WHERE p.psychId = :psychId
              AND co.orderedAt >= :startAt
              AND co.orderedAt < :endAt
        """)
        long countMonthlyOrdersByPsychologist(
                @Param("psychId") Integer psychId,
                @Param("startAt") LocalDateTime startAt,
                @Param("endAt") LocalDateTime endAt
        );
    
    @Query("""
            SELECT COALESCE(SUM(od.discountedPrice), 0)
            FROM OrderDetail od
            JOIN od.courseOrder co
            JOIN od.course c
            JOIN c.psychologist p
            WHERE p.psychId = :psychId
              AND co.paymentStatus = 1
              AND co.orderedAt >= :startDate
              AND co.orderedAt < :endDate
            """)
        Long sumMonthlyRevenueByPsychologist(
            @Param("psychId") Integer psychId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
        );
}