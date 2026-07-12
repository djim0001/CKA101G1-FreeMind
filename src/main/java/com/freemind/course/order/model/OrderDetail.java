package com.freemind.course.order.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.freemind.course.course.model.Course;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "order_details")
public class OrderDetail {

	public OrderDetail() {
	}

	@EmbeddedId
	private CompositeOrderDetail compositeOrderDetail;

	@ManyToOne
	@MapsId("courseOrderId")
	@JoinColumn(name = "course_order_id")
	private CourseOrder courseOrder;
	@ManyToOne
	@MapsId("courseId")
	@JoinColumn(name = "course_id")
	private Course course;

	@Column(name = "price")
	private Integer price;
	@Column(name = "discounted_price")
	private Integer discountedPrice;
	@Column(name = "course_permission")
	private Byte coursePermission = 0;
	@Column(name = "rating")
	private Byte rating;
	@Column(name = "review_content")
	private String reviewContent;
	@Column(name = "reviewed_at")
	private LocalDateTime reviewedAt;
	@Column(name = "course_progress")
	private BigDecimal courseProgress;
	@Column(name = "playback_position")
	private LocalTime playbackPosition;

	public CompositeOrderDetail getCompositeOrderDetail() {
		return compositeOrderDetail;
	}

	public void setCompositeOrderDetail(CompositeOrderDetail compositeOrderDetail) {
		this.compositeOrderDetail = compositeOrderDetail;
	}

	public CourseOrder getCourseOrder() {
		return courseOrder;
	}

	public void setCourseOrder(CourseOrder courseOrder) {
		this.courseOrder = courseOrder;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public Integer getPrice() {
		return price;
	}

	public void setPrice(Integer price) {
		this.price = price;
	}
	public void setPrice(Course course) {
		price = course.getPrice();
	}

	public Integer getDiscountedPrice() {
		return discountedPrice;
	}

	public void setDiscountedPrice(Integer discountedPrice) {
		this.discountedPrice = discountedPrice;
	}

	public Byte getCoursePermission() {
		return coursePermission;
	}

	public void setCoursePermission(Byte coursePermission) {
		this.coursePermission = coursePermission;
	}

	public Byte getRating() {
		return rating;
	}

	public void setRating(Byte rating) {
		this.rating = rating;
	}

	public String getReviewContent() {
		return reviewContent;
	}

	public void setReviewContent(String reviewContent) {
		this.reviewContent = reviewContent;
	}

	public LocalDateTime getReviewedAt() {
		return reviewedAt;
	}

	public void setReviewedAt(LocalDateTime reviewedAt) {
		this.reviewedAt = reviewedAt;
	}

	public BigDecimal getCourseProgress() {
		return courseProgress;
	}

	public void setCourseProgress(BigDecimal courseProgress) {
		this.courseProgress = courseProgress;
	}

	public LocalTime getPlaybackPosition() {
		return playbackPosition;
	}

	public void setPlaybackPosition(LocalTime playbackPosition) {
		this.playbackPosition = playbackPosition;
	}
	public String getCoursePermissionText() {
	    if (coursePermission == null) {
	        return "未知狀態";
	    }
	    byte status = coursePermission;
	    switch (coursePermission) {
	        case 0:
	            return "尚未解鎖權限";
	        case 1:
	            return "已解鎖";
	        case 2:
	            return "審核成功";
	        default:
	            return "未知狀態";
	    }
	}
	
	// =========複合主鍵=====================
	@Embeddable
	public static class CompositeOrderDetail implements Serializable {

		@Column(name = "course_order_id")
		private Integer courseOrderId;
		@Column(name = "course_id")
		private Integer courseId;

		public CompositeOrderDetail() {
			super();
		}

		public CompositeOrderDetail(Integer courseOrderId, Integer courseId) {
			super();
			this.courseId = courseId;
			this.courseOrderId = courseOrderId;
		}

		public Integer getCourseOrderId() {
			return courseOrderId;
		}

		public void setCourseOrderId(Integer courseOrderId) {
			this.courseOrderId = courseOrderId;
		}

		public Integer getCourseId() {
			return courseId;
		}

		public void setCourseId(Integer courseId) {
			this.courseId = courseId;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((courseId == null) ? 0 : courseId.hashCode());
			result = prime * result + ((courseOrderId == null) ? 0 : courseOrderId.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;

			if (obj != null && getClass() == obj.getClass()) {
				CompositeOrderDetail compositeOrderDetail = (CompositeOrderDetail) obj;
				if (courseOrderId.equals(compositeOrderDetail.courseOrderId) 
						&& courseId.equals(compositeOrderDetail.courseId)) {
					return true;
				}
			}
			return false;
		}

	}

}
