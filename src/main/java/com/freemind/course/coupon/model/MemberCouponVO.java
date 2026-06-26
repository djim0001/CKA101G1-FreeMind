package com.freemind.course.coupon.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "member_coupons")
public class MemberCouponVO {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "coupon_serial_no")
	private Integer couponSerialNo;
	@ManyToOne
	@JoinColumn(name = "coupon_id", referencedColumnName = "coupon_id")
	private CouponVO couponVO;
	@Column(name = "member_id")
	private Integer memberId;
	@Column(name = "coupon_status")
	private Byte couponStatus;
	@Column(name = "coupon_start_at")
	private LocalDateTime couponStartAt;
	@Column(name = "coupon_end_at")
	private LocalDateTime couponEndAt;
	
	
	public Integer getCouponSerialNo() {
		return couponSerialNo;
	}
	public void setCouponSerialNo(Integer couponSerialNo) {
		this.couponSerialNo = couponSerialNo;
	}
	public CouponVO getCouponVO() {
		return couponVO;
	}
	public void setCouponVO(CouponVO couponVO) {
		this.couponVO = couponVO;
	}
	public Integer getMemberId() {
		return memberId;
	}
	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}
	public Byte getCouponStatus() {
		return couponStatus;
	}
	public void setCouponStatus(Byte couponStatus) {
		this.couponStatus = couponStatus;
	}
	public LocalDateTime getCouponStartAt() {
		return couponStartAt;
	}
	public void setCouponStartAt(LocalDateTime couponStartAt) {
		this.couponStartAt = couponStartAt;
	}
	public LocalDateTime getCouponEndAt() {
		return couponEndAt;
	}
	public void setCouponEndAt(LocalDateTime couponEndAt) {
		this.couponEndAt = couponEndAt;
	}
}
