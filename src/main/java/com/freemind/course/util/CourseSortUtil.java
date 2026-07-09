package com.freemind.course.util;

import org.springframework.data.domain.Sort;

public final class CourseSortUtil {

    private CourseSortUtil() {
        // 防止被 new
    }

    public static Sort getCourseSort(String orderBy) {

        if (orderBy == null || orderBy.isBlank()) {
            orderBy = "courseId";
        }

        return switch (orderBy) {
            case "courseCategoriesAsc" -> Sort.by("courseCategories.courseCatId").ascending();
            case "courseCategoriesDesc" -> Sort.by("courseCategories.courseCatId").descending();

            case "psychIdAsc" -> Sort.by("psychologist.psychId").ascending();
            case "psychIdDesc" -> Sort.by("psychologist.psychId").descending();

            case "courseStatusAsc" -> Sort.by("courseStatus").ascending();
            case "courseStatusDesc" -> Sort.by("courseStatus").descending();

            case "saveCountAsc" -> Sort.by("saveCount").ascending();
            case "saveCountDesc" -> Sort.by("saveCount").descending();

            case "starCountAsc" -> Sort.by("starCount").ascending();
            case "starCountDesc" -> Sort.by("starCount").descending();

            case "reviewCountAsc" -> Sort.by("reviewCount").ascending();
            case "reviewCountDesc" -> Sort.by("reviewCount").descending();

            case "commentCountAsc" -> Sort.by("commentCount").ascending();
            case "commentCountDesc" -> Sort.by("commentCount").descending();

            case "priceAsc" -> Sort.by("price").ascending();
            case "priceDesc" -> Sort.by("price").descending();

            default -> Sort.by("courseId").descending();
        };
    }
}