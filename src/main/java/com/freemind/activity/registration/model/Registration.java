package com.freemind.activity.registration.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.freemind.activity.activity.model.Activity;
import com.freemind.login.member.model.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="activity_registrations")
public class Registration implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="regis_id")
	private Integer regisId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="member_id", referencedColumnName="member_id", nullable=false)
	private Member member;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="activity_id", referencedColumnName="activity_id", nullable=false)
	private Activity activity;
	
	@Column(name="regis_status", nullable=false, columnDefinition="tinyint")
	private Integer regisStatus;
	
	@Column(name="regis_at", nullable=false)
	private LocalDateTime regisAt;
	
	@Column(name="motivation", length=200)
	@NotEmpty(message = "報名動機: 請勿空白")
	@Size(max=200, message="報名動機: 長度不能超過{max}")
	private String motivation;
	
	@Column(name="cancelled_at")
	private LocalDateTime cancelledAt;
	
	@Min(value=0, message="取消原因：請選擇有效的選項")
	@Max(value=2, message="取消原因：請選擇有效的選項")
	@Column(name="cancel_reason", columnDefinition="tinyint")
	private Integer cancelReason;
	
	@Column(name="cancel_note", length=200)
	@Size(max=200, message="取消補充說明：長度不能超過{max}")
	private String cancelNote;
	
	@Min(value=0, message="拒絕原因：請選擇有效的選項")
	@Max(value=2, message="拒絕原因：請選擇有效的選項")
	@Column(name="reject_reason", columnDefinition="tinyint")
	private Integer rejectReason;
	
	@Column(name="reject_note", length=200)
	@Size(max=200, message="拒絕補充說明：長度不能超過{max}")
	private String rejectNote;
	
	@Column(name="review_content", length=500)
	@Size(max=500, message="活動心得：長度不能超過{max}")
	private String reviewContent;
	
	@Min(value=1, message="評分：最低為{value}分")
	@Max(value=5, message="評分：最高為{value}分")
	@Column(name="rating", columnDefinition = "tinyint")
	private Integer rating;
	
	@Column(name="reviewed_at")
	private LocalDateTime reviewedAt;

	public Integer getRegisId() {
		return regisId;
	}

	public void setRegisId(Integer regisId) {
		this.regisId = regisId;
	}

	public Member getMember() {
		return member;
	}

	public void setMember(Member member) {
		this.member = member;
	}

	public Activity getActivity() {
		return activity;
	}

	public void setActivity(Activity activity) {
		this.activity = activity;
	}

	public Integer getRegisStatus() {
		return regisStatus;
	}

	public void setRegisStatus(Integer regisStatus) {
		this.regisStatus = regisStatus;
	}

	public LocalDateTime getRegisAt() {
		return regisAt;
	}

	public void setRegisAt(LocalDateTime regisAt) {
		this.regisAt = regisAt;
	}
	
	public String getMotivation() {
		return motivation;
	}

	public void setMotivation(String motivation) {
		this.motivation = motivation;
	}

	public LocalDateTime getCancelledAt() {
		return cancelledAt;
	}

	public void setCancelledAt(LocalDateTime cancelledAt) {
		this.cancelledAt = cancelledAt;
	}

	public Integer getCancelReason() {
		return cancelReason;
	}

	public void setCancelReason(Integer cancelReason) {
		this.cancelReason = cancelReason;
	}

	public String getCancelNote() {
		return cancelNote;
	}

	public void setCancelNote(String cancelNote) {
		this.cancelNote = cancelNote;
	}

	public Integer getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(Integer rejectReason) {
		this.rejectReason = rejectReason;
	}

	public String getRejectNote() {
		return rejectNote;
	}

	public void setRejectNote(String rejectNote) {
		this.rejectNote = rejectNote;
	}

	public String getReviewContent() {
		return reviewContent;
	}

	public void setReviewContent(String reviewContent) {
		this.reviewContent = reviewContent;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public LocalDateTime getReviewedAt() {
		return reviewedAt;
	}

	public void setReviewedAt(LocalDateTime reviewedAt) {
		this.reviewedAt = reviewedAt;
	}

	public String getRegisStatusText() {
		if (this.regisStatus == null) return "狀態待確認";
		switch (this.regisStatus) {
		case 0: return "待審核";
		case 1: return "已報名成功(正取)";
		case 2: return "報名失敗";
		case 3: return "已取消報名";
		case 4: return "已報名成功(備取)";
		default: return "狀態異常(待確認)";
		}
	}

	public String getCancelReasonText() {
		if (this.cancelReason == null) return "無";
		switch (this.cancelReason) {
		case 0: return "行程衝突";
		case 1: return "身體不適";
		case 2: return "其他";
		default: return "未定義的取消原因";
		}
	}
	
	public String getRejectReasonText() {
		if (this.rejectReason == null) return "無";
		switch (this.rejectReason) {
		case 0: return "報名表單填寫不明確";
		case 1: return "不符合活動資格條件";
		case 2: return "其他";
		default: return "未定義的拒絕原因";
		}
	}
	
	public boolean isCancellable() {
	    if (this.regisStatus == null || (this.regisStatus != 0 && this.regisStatus != 1 && this.regisStatus != 4)) {
	        return false;
	    }
	    
	    if (this.activity == null) {
	    	return false;
	    }
	    return !this.activity.isStarted() && this.activity.getActivityStatus() != 4;
	}
}
