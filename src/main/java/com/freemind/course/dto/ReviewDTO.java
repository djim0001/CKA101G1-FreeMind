package com.freemind.course.dto;

import java.time.LocalDateTime;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReviewDTO {
	@NotNull(message = "請選擇課程")
	private Integer courseId;
	@NotBlank(message="請輸入評價內容")
	@Size(max = 200, message = "評價內容不能超過 200 個字")
	private String reviewContent;
	@NotNull
	@DecimalMin(value = "1")
	@DecimalMax(value = "5")
	private Byte rating;
	private LocalDateTime reviewed_at;
	public Integer getCourseId() {
		return courseId;
	}
	public void setCourseId(Integer courseId) {
		this.courseId = courseId;
	}
	public String getReviewContent() {
		return reviewContent;
	}
	public void setReviewContent(String reviewContent) {
		this.reviewContent = reviewContent;
	}
	public Byte getRating() {
		return rating;
	}
	public void setRating(Byte rating) {
		this.rating = rating;
	}
	public LocalDateTime getReviewed_at() {
		return reviewed_at;
	}
	public void setReviewed_at(LocalDateTime reviewed_at) {
		this.reviewed_at = reviewed_at;
	}
	
}
