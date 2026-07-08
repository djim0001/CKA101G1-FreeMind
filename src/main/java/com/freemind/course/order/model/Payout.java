package com.freemind.course.order.model;

import java.time.LocalDateTime;

import com.freemind.login.admin.model.Admin;
import com.freemind.login.psychologist.entity.Psychologist;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "payouts")
public class Payout {

	private Integer payoutId;
	private String billingMonth;
	private Psychologist psychologist;
	private Admin admin;
	private Integer grossPayoutAmount;
	private Integer platformCommission;
	private Integer billingOffset;
	private Integer netPayoutAmount;
	private LocalDateTime paidAt;
	private Integer payoutStatus;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "payout_id")

	public Integer getPayoutId() {
		return payoutId;
	}

	public void setPayoutId(Integer payoutId) {
		this.payoutId = payoutId;
	}

	@Column(name = "billing_month", nullable = false)
	public String getBillingMonth() {
		return billingMonth;
	}

	public void setBillingMonth(String billingMonth) {
		this.billingMonth = billingMonth;
	}

	@ManyToOne
	@JoinColumn(name = "psych_id", referencedColumnName = "psych_id", nullable = false)
	public Psychologist getPsychologist() {
		return psychologist;
	}

	public void setPsychologist(Psychologist psychologist) {
		this.psychologist = psychologist;
	}

	@ManyToOne
	@JoinColumn(name = "admin_id", referencedColumnName = "admin_id", nullable = true)
	public Admin getAdmin() {
		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;
	}

	@Column(name = "gross_payout_amount ", nullable = false)
	public Integer getGrossPayoutAmount() {
		return grossPayoutAmount;
	}

	public void setGrossPayoutAmount(Integer grossPayoutAmount) {
		this.grossPayoutAmount = grossPayoutAmount;
	}

	@Column(name = "platform_commission ", nullable = false)
	public Integer getPlatformCommission() {
		return platformCommission;
	}

	public void setPlatformCommission(Integer platformCommission) {
		this.platformCommission = platformCommission;
	}

	@Column(name = "billing_offset ", nullable = false)
	public Integer getBillingOffset() {
		return billingOffset;
	}

	public void setBillingOffset(Integer billingOffset) {
		this.billingOffset = billingOffset;
	}

	@Column(name = "net_payout_amount ", nullable = false)
	public Integer getNetPayoutAmount() {
		return netPayoutAmount;
	}

	public void setNetPayoutAmount(Integer netPayoutAmount) {
		this.netPayoutAmount = netPayoutAmount;
	}

	@Column(name = "paid_at")
	public LocalDateTime getPaidAt() {
		return paidAt;
	}

	public void setPaidAt(LocalDateTime paidAt) {
		this.paidAt = paidAt;
	}

	@Column(name = "payout_status")
	public Integer getPayoutStatus() {
		return payoutStatus;
	}

	public void setPayoutStatus(Integer payoutStatus) {
		this.payoutStatus = payoutStatus;
	}
}
