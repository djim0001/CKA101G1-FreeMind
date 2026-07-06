package com.freemind.course.order.model;

import java.time.LocalDateTime;
import java.util.Set;

import com.freemind.course.coupon.model.MemberCoupon;
import com.freemind.login.member.model.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;

@Entity
@Table(name="course_orders")
public class CourseOrder {

	private Integer courseOrderId;
	private Member member;
	private MemberCoupon memberCoupon;
	private Integer orderTotal;
	private Integer discountAmount;
	private Integer netAmount;
	private Integer paymentMethod;
	private Integer paymentStatus;
	private LocalDateTime orderedAt;
	
	private Set<OrderDetail> orderDetails;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="course_order_id")
	
	public Integer getCourseOrderId() {
		return courseOrderId;
	}
	public void setCourseOrderId(Integer courseOrderId) {
		this.courseOrderId = courseOrderId;
	}
	
	
	@ManyToOne
	@JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false)
	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
	}
	
	@ManyToOne
	@JoinColumn(name ="coupon_serial_no",referencedColumnName="coupon_serial_no",nullable=true)
	
	public MemberCoupon getMemberCoupon() {
		return memberCoupon;
	}
	public void setMemberCoupon(MemberCoupon memberCoupon) {
		this.memberCoupon = memberCoupon;
	}
	
	
	@Column(name ="order_total", nullable = false)
	
	public Integer getOrderTotal() {
		return orderTotal;
	}
	public void setOrderTotal(Integer orderTotal) {
		this.orderTotal = orderTotal;
	}
	
	
	@Column(name="discount_amount",nullable = true)
	public Integer getDiscountAmount() {
		return discountAmount;
	}
	public void setDiscountAmount(Integer discountAmount) {
		this.discountAmount = discountAmount;
	}
	
	
	@Column(name="net_amount",nullable = true)
	
	public Integer getNetAmount() {
		return netAmount;
	}
	public void setNetAmount(Integer netAmount) {
		this.netAmount = netAmount;
	}
	
	
	@Column(name="payment_method",nullable = false)
	public Integer getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(Integer paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	
	
	@Column(name="payment_status")
	public Integer getPaymentStatus() {
		return paymentStatus;
	}
	public void setPaymentStatus(Integer paymentStatus) {
		this.paymentStatus = paymentStatus;
	}
	
	
	@Column(name="ordered_at")
	public LocalDateTime getOrderedAt() {
		return orderedAt;
	}

	public void setOrderedAt(LocalDateTime orderedAt) {
		this.orderedAt = orderedAt;
	}
	
	//訂單明細
	@OneToMany(mappedBy = "courseOrder", cascade = CascadeType.ALL)
	@OrderBy("course_order_id asc")
	public Set<OrderDetail> getOrderDetails() {
		return orderDetails;
	}
	public void setOrderDetails(Set<OrderDetail> orderDetails) {
		this.orderDetails = orderDetails;
	}

	
	
	
	
}
