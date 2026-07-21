package com.freemind.course.dto;

import com.freemind.course.course.model.Course;

import lombok.Getter;

@Getter
public class CourseRecommendDTO {
	private Integer courseId;
	private String courseCategory;
	private String courseName;
	private String psych;
	private String outline;
	private Integer price;
	private Integer saveCount;
	private Integer reviewCount;
	private Integer starCount;

	public CourseRecommendDTO(Course course) {
		this.courseId = course.getCourseId();
		this.courseCategory = course.getCourseCategories().getCourseCatName();
		this.courseName = course.getCourseName();
		this.psych = course.getPsychologist().getName();
		this.outline = course.getOutline();
		this.price = course.getPrice();
		this.saveCount = course.getSaveCount();
		this.reviewCount = course.getReviewCount();
		this.starCount = course.getStarCount();
	}
}
