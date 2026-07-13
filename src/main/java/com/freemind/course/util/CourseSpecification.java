package com.freemind.course.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.freemind.course.course.model.Course;

import jakarta.persistence.criteria.Predicate;

public class CourseSpecification {
	private CourseSpecification() {
		
	}

    public static Specification<Course> searchByCounts(
            Byte courseStatus,
            Integer minSaveCount,
            Integer minStarCount,
            Integer minReviewCount,
            Integer minCommentCount) {

        return (root, query, criteriaBuilder) -> {

            List<Predicate> predicates = new ArrayList<>();

            // 課程狀態
            if (courseStatus != null) {
                predicates.add(
                    criteriaBuilder.equal(
                        root.get("courseStatus"),
                        courseStatus
                    )
                );
            }

            // 收藏次數大於等於
            if (minSaveCount != null) {
                predicates.add(
                    criteriaBuilder.greaterThanOrEqualTo(
                        root.get("saveCount"),
                        minSaveCount
                    )
                );
            }

            // 總星數大於等於
            if (minStarCount != null) {
                predicates.add(
                    criteriaBuilder.greaterThanOrEqualTo(
                        root.get("starCount"),
                        minStarCount
                    )
                );
            }

            // 評價次數大於等於
            if (minReviewCount != null) {
                predicates.add(
                    criteriaBuilder.greaterThanOrEqualTo(
                        root.get("reviewCount"),
                        minReviewCount
                    )
                );
            }

            // 提問次數大於等於
            if (minCommentCount != null) {
                predicates.add(
                    criteriaBuilder.greaterThanOrEqualTo(
                        root.get("commentCount"),
                        minCommentCount
                    )
                );
            }

            return criteriaBuilder.and(
                predicates.toArray(new Predicate[0])
            );
        };
    }
}