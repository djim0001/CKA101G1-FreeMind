package com.freemind.course.coupon.model;

import java.math.BigDecimal;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "coupons")
public class Coupon {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "coupon_id")
	private Integer couponId;
	@Column(name = "coupon_name")
	@NotEmpty(message="優惠券名稱：請勿空白")
	@Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,10}$", 
		message = "優惠券名稱: 只能是中、英文字母、數字和_ , 且長度必需在2到10之間")
	private String couponName;
	@Column(name = "discount_duration")
	@NotNull(message="優惠券效期：請勿空白")
	@DecimalMin(value = "3.00", message = "優惠券效期: 不能小於{value}")
	@DecimalMax(value = "30.00", message = "優惠券效期: 不能超過{value}")
	private Integer discountDuration;
	@Column(name = "trigger_threshold")
	@DecimalMin(value = "0", message = "消費觸發門檻: 不能小於{value}")
	@DecimalMax(value = "10000", message = "消費觸發門檻: 不能超過{value}")
	private Integer triggerThreshold;
	@Column(name = "discount")
	@NotNull(message="優惠券折扣：請勿空白")
	@DecimalMin(value = "0.01", message = "優惠券折扣: 不能小於{value}")
	@DecimalMax(value = "0.99", message = "優惠券折扣: 不能超過{value}")
	private BigDecimal discount;
	@Column(name = "discount_limit")
	@DecimalMin(value = "0", message = "折扣上限: 不能小於{value}")
	@DecimalMax(value = "10000", message = "折扣上限: 不能超過{value}")
	private Integer discountLimit;
	@OneToMany(mappedBy = "coupon", cascade = CascadeType.ALL)
	@OrderBy("coupon_id asc")
	private Set<MemberCoupon> memberCoupons;
	

	public Integer getCouponId() {
		return couponId;
	}

	public void setCouponId(Integer couponId) {
		this.couponId = couponId;
	}

	public String getCouponName() {
		return couponName;
	}

	public void setCouponName(String couponName) {
		this.couponName = couponName;
	}

	public Integer getDiscountDuration() {
		return discountDuration;
	}

	public void setDiscountDuration(Integer discountDuration) {
		this.discountDuration = discountDuration;
	}

	public Integer getTriggerThreshold() {
		return triggerThreshold;
	}

	public void setTriggerThreshold(Integer triggerThreshold) {
		this.triggerThreshold = triggerThreshold;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public Integer getDiscountLimit() {
		return discountLimit;
	}

	public void setDiscountLimit(Integer discountLimit) {
		this.discountLimit = discountLimit;
	}
	public Set<MemberCoupon> getMemberCoupons() {
		return memberCoupons;
	}

	public void setMemberCoupons(Set<MemberCoupon> memberCoupons) {
		this.memberCoupons = memberCoupons;
	}

}
