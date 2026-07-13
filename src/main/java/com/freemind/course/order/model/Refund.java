package com.freemind.course.order.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.freemind.login.admin.model.Admin;
import com.freemind.login.member.model.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "refunds")
public class Refund {

	@EmbeddedId
	private CompositeRefund compositeRefund;

	@Column(name = "refund_reason", nullable = false)
	private String refundReason;

	@Column(name = "refund_amount", nullable = false)
	private Integer refundAmount;

	@Column(name = "created_at")
	private LocalDateTime createdAt;

	@Column(name = "refunded_at")
	private LocalDateTime refundedAt;

	@Column(name = "refund_status", nullable = false)
	private Integer refundStatus;

	@ManyToOne
	@MapsId("courseOrderId")
	@JoinColumn(name = "course_order_id", referencedColumnName = "course_order_id", nullable = false)
	private CourseOrder courseOrder;

	public CourseOrder getCourseOrder() {
		return courseOrder;
	}

	public void setCourseOrder(CourseOrder courseOrder) {
		this.courseOrder = courseOrder;
	}

	@ManyToOne
	@MapsId("memberId")
	@JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false)
	private Member member;

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	@ManyToOne
	@JoinColumn(name = "admin_id", referencedColumnName = "admin_id")
	private Admin admin;

	public Admin getAdmin() {
		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;
	}

	public String getRefundReason() {
		return refundReason;
	}

	public void setRefundReason(String refundReason) {
		this.refundReason = refundReason;
	}

	public Integer getRefundAmount() {
		return refundAmount;
	}

	public void setRefundAmount(Integer refundAmount) {
		this.refundAmount = refundAmount;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getRefundedAt() {
		return refundedAt;
	}

	public void setRefundedAt(LocalDateTime refundedAt) {
		this.refundedAt = refundedAt;
	}

	public Integer getRefundStatus() {
		return refundStatus;
	}

	public void setRefundStatus(Integer refundStatus) {
		this.refundStatus = refundStatus;
	}


	@Embeddable
	public static class CompositeRefund implements Serializable {

	    private static final long serialVersionUID = 1L;

	    @Column(name = "course_order_id")
	    private Integer courseOrderId;

	    @Column(name = "member_id")
	    private Integer memberId;

	    public CompositeRefund() {
	    }

	    public CompositeRefund(Integer courseOrderId, Integer memberId) {
	        this.courseOrderId = courseOrderId;
	        this.memberId = memberId;
	    }

	    public Integer getCourseOrderId() {
	        return courseOrderId;
	    }

	    public void setCourseOrderId(Integer courseOrderId) {
	        this.courseOrderId = courseOrderId;
	    }

	    public Integer getMemberId() {
	        return memberId;
	    }

	    public void setMemberId(Integer memberId) {
	        this.memberId = memberId;
	    }

	    @Override
	    public int hashCode() {
	        final int prime = 31;
	        int result = 1;
	        result = prime * result + ((courseOrderId == null) ? 0 : courseOrderId.hashCode());
	        result = prime * result + ((memberId == null) ? 0 : memberId.hashCode());
	        return result;
	    }

	    @Override
	    public boolean equals(Object obj) {
	        if (this == obj)
	            return true;

	        if (obj != null && getClass() == obj.getClass()) {
	            CompositeRefund other = (CompositeRefund) obj;
	            return courseOrderId.equals(other.courseOrderId)
	                    && memberId.equals(other.memberId);
	        }
	        return false;
	    }
	}
		
		
	

	
	
}
