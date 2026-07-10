package com.freemind.course.coupon.model;

import java.time.LocalDateTime;

import com.freemind.login.member.model.Member;

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
public class MemberCoupon {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "coupon_serial_no")
	private Integer couponSerialNo;
	@ManyToOne
	@JoinColumn(name = "coupon_id", referencedColumnName = "coupon_id")
	private Coupon coupon;
	
	@ManyToOne
	@JoinColumn(name = "member_id", referencedColumnName = "member_id")
	private Member member;
	
	@Column(name = "coupon_status")
	private Byte couponStatus = 0;
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
	public Coupon getCoupon() {
		return coupon;
	}
	public void setCoupon(Coupon coupon) {
		this.coupon = coupon;
	}
//	public Integer getMemberId() {
//		return memberId;
//	}
//	public void setMemberId(Integer memberId) {
//		this.memberId = memberId;
//	}
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
	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
	}
	// util
	public String getCouponStatusText() {
	    if (couponStatus == null) {
	        return "未知狀態";
	    }
	    byte status = couponStatus;
	    switch (couponStatus) {
	        case 0:
	            return "未使用";
	        case 1:
	            return "已使用";
	        default:
	            return "未知狀態";
	    }
	}
	
	// 會員優惠券是否可用
	public boolean isCouponValid() {
	    LocalDateTime now = LocalDateTime.now();
	    couponStatus = couponStatus == null ? 0 : couponStatus;

	    return couponStatus != 1
	            && couponStartAt != null
	            && couponEndAt != null
	            && !now.isBefore(couponStartAt)
	            && !now.isAfter(couponEndAt);
	}
	
}
