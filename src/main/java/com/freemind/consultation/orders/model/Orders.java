package com.freemind.consultation.orders.model;

import java.time.LocalDateTime;
import java.util.List;


import com.freemind.consultation.reports.model.Reports;
import com.freemind.consultation.slots.model.Slots;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "consultation_orders")
public class Orders {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Integer orderId;//not null(PK)(AI)
	
	@OneToOne(fetch = FetchType.LAZY) //一個時段只會有一個訂單
	@JoinColumn(name = "time_id", referencedColumnName = "timeslot_id", nullable = false)
	private Slots slot;//not null(FK)，缺FK>>已補
	
	@Column(name = "cons_start", nullable = false)
//	@NotNull(message = "開始時間：請勿空白")
	private LocalDateTime consStart; //not null
	
	@Column(name = "cons_end", nullable = false)
//	@NotNull(message = "結束時間：請勿空白")
	private LocalDateTime consEnd; //not null
	
	@Column(name = "member_id", nullable = false)
	@NotNull(message = "會員編號：請勿空白")
	private Integer memberId;//not null(FK)，缺FK
	
	@Column(name = "psych_id", nullable = false)
	@NotNull(message = "心理師編號：請勿空白")
	private Integer psychId;//not null(FK)，缺FK
	
	@Column(name = "created_at", nullable = false)
//	@NotNull(message = "建立時間：請勿空白")
	private LocalDateTime createdAt; //not null
	
	@Column(name = "psych_loc", nullable = false, length = 50)
	@NotBlank(message = "心理師工作地點：請勿空白")
	private String psychLoc; //not null
	
	@Column(name = "order_status", nullable = false)
	@NotNull(message = "預約訂單狀態：請勿空白")
	@Min(value = 0, message = "預約訂單狀態：最小值為0")
	@Max(value = 4, message = "預約訂單狀態：最大值為4")
	private Integer orderStatus = 0; //not null，default 0，0：待確認、1：已確認、2：已取消、3：未出席、4：已完成
	
	@Column(name = "has_gov_subsidy", nullable = false)
	@NotNull(message = "是否為政府補助：請勿空白")
	private Boolean govSubsidy = false; //not null，default 0，0：非政府補助、1：政府補助
	
	@Column(name = "psych_fee", nullable = false)
	@NotNull(message = "心理師諮詢費：請勿空白")
	@Min(value = 0, message = "心理師諮詢費：不可為負數")
	private Integer psychFee; //not null
	
	@Column(name = "visit_purpose", nullable = false)
	@NotNull(message = "來訪目的：請勿空白")
	@Pattern(regexp = "(emotion|stress|relationship|family|career|academic|self_growth|sleep|anxiety|other)")
	private String visitPurpose; //not null
	
	@Column(name = "visit_purpose_note", length = 200)
	private String visitPurposeNote;//varchar(200)
	
	@Column(name = "session_type", nullable = false)
	@NotNull(message = "諮商參與形式：請勿空白")
	@Min(value = 0, message = "諮商參與形式：最小值為0")
	@Max(value = 3, message = "諮商參與形式：最大值為3")
	private Integer sessionType; //not null，0：單人諮商、1：伴侶諮商、2：家庭諮商、3：團體諮商
	
	@Column(name = "psych_note", length = 10000)
	private String psychNote; //varchar(10000)
	
	@Column(name = "rating")
	@Min(value = 1, message = "會員評分：最小值為1分")
	@Max(value = 5, message = "會員評分：最大值為5分")
	private Integer rating; //unsigned
	
	@Column(name = "review_content", length = 200)
	private String reviewContent;//varchar(200)
	
	@Column(name = "reviewed_at")
	private LocalDateTime reviewedAt;
	
	@OneToMany(mappedBy = "orders", cascade = CascadeType.ALL) //cascade = CascadeType.ALL大多配置在一方
	private List<Reports> reportsList;

	public Integer getOrderId() {
		return orderId;
	}

	public void setOrderId(Integer orderId) {
		this.orderId = orderId;
	}

	
//	public Integer getTimeId() {
//		return timeId;
//	}
//
//	public void setTimeId(Integer timeId) {
//		this.timeId = timeId;
//	}

	public Slots getSlot() {
		return slot;
	}

	public void setSlot(Slots slot) {
		this.slot = slot;
	}

	public LocalDateTime getConsStart() {
		return consStart;
	}


	public void setConsStart(LocalDateTime consStart) {
		this.consStart = consStart;
	}

	public LocalDateTime getConsEnd() {
		return consEnd;
	}

	public void setConsEnd(LocalDateTime consEnd) {
		this.consEnd = consEnd;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Integer getPsychId() {
		return psychId;
	}

	public void setPsychId(Integer psychId) {
		this.psychId = psychId;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public String getPsychLoc() {
		return psychLoc;
	}

	public void setPsychLoc(String psychLoc) {
		this.psychLoc = psychLoc;
	}

	public Integer getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}

	public Boolean getGovSubsidy() {
		return govSubsidy;
	}

	public void setGovSubsidy(Boolean govSubsidy) {
		this.govSubsidy = govSubsidy;
	}

	public Integer getPsychFee() {
		return psychFee;
	}

	public void setPsychFee(Integer psychFee) {
		this.psychFee = psychFee;
	}

	public String getVisitPurpose() {
		return visitPurpose;
	}

	public void setVisitPurpose(String visitPurpose) {
		this.visitPurpose = visitPurpose;
	}

	public String getVisitPurposeNote() {
		return visitPurposeNote;
	}

	public void setVisitPurposeNote(String visitPurposeNote) {
		this.visitPurposeNote = visitPurposeNote;
	}

	public Integer getSessionType() {
		return sessionType;
	}

	public void setSessionType(Integer sessionType) {
		this.sessionType = sessionType;
	}

	public String getPsychNote() {
		return psychNote;
	}

	public void setPsychNote(String psychNote) {
		this.psychNote = psychNote;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
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

	@Override
	public String toString() {
		return "Orders [orderId=" + orderId + ", consStart=" + consStart + ", consEnd=" + consEnd
				+ ", memberId=" + memberId + ", psychId=" + psychId + ", createdAt=" + createdAt + ", psychLoc="
				+ psychLoc + ", orderStatus=" + orderStatus + ", govSubsidy=" + govSubsidy + ", psychFee=" + psychFee
				+ ", visitPurpose=" + visitPurpose + ", visitPurposeNote=" + visitPurposeNote + ", sessionType="
				+ sessionType + ", psychNote=" + psychNote + ", rating=" + rating + ", reviewContent=" + reviewContent
				+ ", reviewedAt=" + reviewedAt + "]";
	}

	public List<Reports> getReportsList() {
		return reportsList;
	}

	public void setReportsList(List<Reports> reportsList) {
		this.reportsList = reportsList;
	}
	
	
}
