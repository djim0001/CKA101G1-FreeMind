package com.freemind.course.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.freemind.course.course.model.Course;
import com.freemind.course.course.model.CourseQaComment;
import com.freemind.course.dto.CourseQaSearchCondition;
import com.freemind.login.member.model.Member;
import com.freemind.login.psychologist.entity.Psychologist;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

public class CourseQaSpecification {

    public static Specification<CourseQaComment> search(
            Integer psychId,
            CourseQaSearchCondition condition
    ) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            /*
             * 關聯：
             * CourseQaComment -> Course -> Psychologist
             * CourseQaComment -> Member
             */
            Join<CourseQaComment, Course> courseJoin =
                    root.join("course", JoinType.INNER);

            Join<Course, Psychologist> psychologistJoin =
                    courseJoin.join("psychologist", JoinType.INNER);

            Join<CourseQaComment, Member> memberJoin =
                    root.join("member", JoinType.INNER);

            // 限制只能查指定心理師底下的課程提問
            if (psychId != null) {
                predicates.add(
                        cb.equal(
                                psychologistJoin.get("psychId"),
                                psychId
                        )
                );
            }

            if (condition == null) {
                return cb.and(predicates.toArray(new Predicate[0]));
            }

            // 關鍵字搜尋
            if (condition.getKeyword() != null
                    && !condition.getKeyword().isBlank()) {

                String pattern =
                        "%" + condition.getKeyword()
                                       .trim()
                                       .toLowerCase()
                                + "%";

                Predicate keywordPredicate = cb.or(
                        cb.like(
                                cb.lower(courseJoin.get("courseName")),
                                pattern
                        ),
                        cb.like(
                                cb.lower(memberJoin.get("name")),
                                pattern
                        ),
                        cb.like(
                                cb.lower(root.get("courseQuestion")),
                                pattern
                        )
                );

                predicates.add(keywordPredicate);
            }

            // 提問時間起始
            if (condition.getAskedAtStart() != null) {
                predicates.add(
                        cb.greaterThanOrEqualTo(
                                root.get("askedAt"),
                                condition.getAskedAtStart()
                        )
                );
            }

            // 提問時間結束
            if (condition.getAskedAtEnd() != null) {
                predicates.add(
                        cb.lessThanOrEqualTo(
                                root.get("askedAt"),
                                condition.getAskedAtEnd()
                        )
                );
            }

            // 回覆狀態
            if (condition.getAnswerStatus() != null) {

                switch (condition.getAnswerStatus()) {

                    case ANSWERED -> predicates.add(
                            cb.isNotNull(root.get("answeredAt"))
                    );

                    case UNANSWERED -> predicates.add(
                            cb.isNull(root.get("answeredAt"))
                    );

                    case ALL -> {
                        // 不增加條件
                    }
                }
            }

            query.distinct(true);

            return cb.and(
                    predicates.toArray(new Predicate[0])
            );
        };
    }
}
