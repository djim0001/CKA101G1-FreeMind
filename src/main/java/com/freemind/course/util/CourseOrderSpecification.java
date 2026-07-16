package com.freemind.course.util;


import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.jpa.domain.Specification;

import com.freemind.course.order.model.CourseOrder;
import com.freemind.login.member.model.Member;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

public final class CourseOrderSpecification {

    private CourseOrderSpecification() {
    }

    /**
     * 搜尋訂單編號或會員姓名。
     *
     * keyword 是純數字時：
     * - 搜尋訂單編號
     * - 同時搜尋會員姓名
     *
     * keyword 不是數字時：
     * - 只搜尋會員姓名
     */
    public static Specification<CourseOrder> keywordContains(
            String keyword) {

        return (root, query, cb) -> {

            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }

            String value = keyword.trim();

            Join<CourseOrder, Member> memberJoin =
                    root.join("member", JoinType.INNER);

            var memberNamePredicate = cb.like(
                    cb.lower(memberJoin.get("name")),
                    "%" + value.toLowerCase() + "%"
            );

            try {
                Integer orderId = Integer.valueOf(value);

                return cb.or(
                        cb.equal(root.get("courseOrderId"), orderId),
                        memberNamePredicate
                );

            } catch (NumberFormatException e) {
                return memberNamePredicate;
            }
        };
    }

    /**
     * 依付款狀態查詢。
     */
    public static Specification<CourseOrder> paymentStatusEquals(
            Byte paymentStatus) {

        return (root, query, cb) -> {

            if (paymentStatus == null) {
                return cb.conjunction();
            }

            return cb.equal(
                    root.get("paymentStatus"),
                    paymentStatus
            );
        };
    }

    /**
     * 查詢指定日期 00:00 到隔日 00:00 之前的訂單。
     */
    public static Specification<CourseOrder> orderedDateEquals(
            LocalDate orderedDate) {

        return (root, query, cb) -> {

            if (orderedDate == null) {
                return cb.conjunction();
            }

            LocalDateTime startTime =
                    orderedDate.atStartOfDay();

            LocalDateTime endTime =
                    orderedDate.plusDays(1).atStartOfDay();

            return cb.and(
                    cb.greaterThanOrEqualTo(
                            root.get("orderedAt"),
                            startTime
                    ),
                    cb.lessThan(
                            root.get("orderedAt"),
                            endTime
                    )
            );
        };
    }
}