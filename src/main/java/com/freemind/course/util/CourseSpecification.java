package com.freemind.course.util;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.freemind.course.course.model.Course;
import com.freemind.login.psychologist.entity.Psychologist;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
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
    
    /**
	 * 搜尋課程名稱或心理師姓名
	 */
    public static Specification<Course> keywordContains(String keyword) {

        return (root, query, cb) -> {

            if (keyword == null || keyword.isBlank()) {
                return cb.conjunction();
            }

            String pattern =
                    "%" + keyword.trim().toLowerCase() + "%";

            Join<Course, Psychologist> psychologist =
                    root.join("psychologist", JoinType.LEFT);

            return cb.or(
                    cb.like(
                            cb.lower(root.get("courseName")),
                            pattern
                    ),
                    cb.like(
                            cb.lower(psychologist.get("name")),
                            pattern
                    )
            );
        };
    }

	/**
	 * 依課程分類查詢
	 */
	public static Specification<Course> categoryEquals(Integer categoryId) {

		return (root, query, cb) -> {

			if (categoryId == null) {
				return cb.conjunction();
			}

			return cb.equal(root.get("courseCategories").get("courseCatId"), categoryId);
		};
	}

	/**
	 * 依課程申請狀態查詢
	 */
	public static Specification<Course> courseStatusEquals(Byte courseStatus) {

		return (root, query, cb) -> {

			if (courseStatus == null) {
				return cb.conjunction();
			}

			return cb.equal(root.get("courseStatus"), courseStatus);
		};
	}
}