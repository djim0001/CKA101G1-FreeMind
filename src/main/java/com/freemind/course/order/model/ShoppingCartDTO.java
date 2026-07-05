package com.freemind.course.order.model;

import java.time.LocalDateTime;

public class ShoppingCartDTO {
	private Integer memberId;
	private Integer courseId;
	private LocalDateTime createdAt;
	
	public ShoppingCartDTO(
			Integer memberId,
			Integer courseId,
			LocalDateTime createdAt) {
		this.memberId = memberId;
		this.courseId = courseId;
		this.createdAt = createdAt;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Integer getCourseId() {
		return courseId;
	}

	public void setCourseId(Integer courseId) {
		this.courseId = courseId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	
}
