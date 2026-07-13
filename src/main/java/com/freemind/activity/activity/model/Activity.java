package com.freemind.activity.activity.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import com.freemind.activity.category.model.ActivityCat;
import com.freemind.login.admin.model.Admin;
import com.freemind.login.member.model.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="activities")
public class Activity implements Serializable{
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="activity_id")
	private Integer activityId;

	@Column(name="activity_name", length=50, nullable=false)
	@NotEmpty(message = "活動名稱: 請勿空白")
	@Size(max = 50, message = "活動名稱: 長度不能超過{max}")
	private String activityName;

	@ManyToOne
	@JoinColumn(name="member_id", referencedColumnName = "member_id", nullable = false)
	private Member member;
	
	@ManyToOne
	@JoinColumn(name="activity_cat_id", referencedColumnName = "activity_cat_id")
	private ActivityCat activityCat;

	@ManyToOne
	@JoinColumn(name="admin_id", referencedColumnName = "admin_id")
	private Admin admin;

	@Column(name="activity_content", length=1000, nullable=false)
	@NotEmpty(message = "活動內容: 請勿空白")
	@Size(max = 1000, message = "活動內容: 長度不能超過{max}")
	private String activityContent;

	@Column(name="activity_city", length=20, nullable=false)
	@NotEmpty(message = "縣市: 請勿空白")
	@Size(max = 20, message = "縣市: 長度不能超過{max}")
	private String activityCity;

	@Column(name="activity_dist", length=20, nullable=false)
	@NotEmpty(message = "區域: 請勿空白")
	@Size(max = 20, message = "區域: 長度不能超過{max}")
	private String activityDist;

	@Column(name="activity_loc", length=50, nullable=false)
	@NotEmpty(message = "詳細地點: 請勿空白")
	@Size(max = 50, message = "詳細地點: 長度不能超過{max}")
	private String activityLoc;

	@Column(name="picture", columnDefinition="longblob")
	private byte[] picture;

	@Column(name = "regis_start", nullable = false)
	@NotNull(message = "報名開始時間: 請勿空白")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime regisStart;

	@Column(name = "regis_end", nullable = false)
	@NotNull(message = "報名結束時間: 請勿空白")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime regisEnd;
	
	@Column(name = "activity_start", nullable = false)
	@NotNull(message = "活動開始時間: 請勿空白")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime activityStart;

	@Column(name = "activity_end", nullable = false)
	@NotNull(message = "活動結束時間: 請勿空白")
	@DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
	private LocalDateTime activityEnd;

	@Column(name="capacity", nullable=false)
	@NotNull(message = "活動名額: 請勿空白")
	@Min(value = 1, message = "活動名額: 不能小於{value}")
	private Integer capacity;

	@Column(name = "regis_count", nullable = false)
	private Integer regisCount;


	@Column(name = "created_at", nullable = false, updatable = false)
	private LocalDateTime createdAt;

	@Column(name = "reviewed_at")
	private LocalDateTime reviewedAt;
	
	@Column(name = "activity_status", nullable = false, columnDefinition = "tinyint")
	private Integer activityStatus;

	@Column(name = "reject_reason", columnDefinition = "tinyint")
	private Integer rejectReason;

	@Column(name = "reject_note", length = 200)
	private String rejectNote;
	
	@Column(name = "cancel_note", length = 200)
	private String cancelNote;
	
	@Column(name = "postpone_note", length = 200)
	private String postponeNote;

	@Column(name = "published_at")
	private LocalDateTime publishedAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;


	public Activity() {
	}
	
	public Integer getActivityId() {
		return activityId;
	}

	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public Member getMember() {
	    return member;
	}

	public void setMember(Member member) {
	    this.member = member;
	}

	public ActivityCat getActivityCat() {
		return activityCat;
	}

	public void setActivityCat(ActivityCat activityCat) {
		this.activityCat = activityCat;
	}

	public Admin getAdmin() {
	    return admin;
	}

	public void setAdmin(Admin admin) {
	    this.admin = admin;
	}

	public String getActivityContent() {
		return activityContent;
	}

	public void setActivityContent(String activityContent) {
		this.activityContent = activityContent;
	}

	public String getActivityCity() {
	    return activityCity;
	}

	public void setActivityCity(String activityCity) {
	    this.activityCity = activityCity;
	}

	public String getActivityDist() {
	    return activityDist;
	}

	public void setActivityDist(String activityDist) {
	    this.activityDist = activityDist;
	}

	public String getActivityLoc() {
	    return activityLoc;
	}

	public void setActivityLoc(String activityLoc) {
	    this.activityLoc = activityLoc;
	}

	public byte[] getPicture() {
		return picture;
	}

	public void setPicture(byte[] picture) {
		this.picture = picture;
	}

	public LocalDateTime getRegisStart() {
		return regisStart;
	}

	public void setRegisStart(LocalDateTime regisStart) {
		this.regisStart = regisStart;
	}

	public LocalDateTime getRegisEnd() {
		return regisEnd;
	}

	public void setRegisEnd(LocalDateTime regisEnd) {
		this.regisEnd = regisEnd;
	}

	public LocalDateTime getActivityStart() {
		return activityStart;
	}

	public void setActivityStart(LocalDateTime activityStart) {
		this.activityStart = activityStart;
	}

	public LocalDateTime getActivityEnd() {
		return activityEnd;
	}

	public void setActivityEnd(LocalDateTime activityEnd) {
		this.activityEnd = activityEnd;
	}

	public Integer getCapacity() {
		return capacity;
	}

	public void setCapacity(Integer capacity) {
		this.capacity = capacity;
	}

	public Integer getRegisCount() {
		return regisCount;
	}

	public void setRegisCount(Integer regisCount) {
		this.regisCount = regisCount;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getReviewedAt() {
		return reviewedAt;
	}

	public void setReviewedAt(LocalDateTime reviewedAt) {
		this.reviewedAt = reviewedAt;
	}

	public Integer getActivityStatus() {
		return activityStatus;
	}

	public void setActivityStatus(Integer activityStatus) {
		this.activityStatus = activityStatus;
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
	
	public String getCancelNote() {
	    return cancelNote;
	}

	public void setCancelNote(String cancelNote) {
	    this.cancelNote = cancelNote;
	}
	
	public String getPostponeNote() {
	    return postponeNote;
	}

	public void setPostponeNote(String postponeNote) {
	    this.postponeNote = postponeNote;
	}

	public LocalDateTime getPublishedAt() {
		return publishedAt;
	}

	public void setPublishedAt(LocalDateTime publishedAt) {
		this.publishedAt = publishedAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getActivityStatusText() {
		if (this.activityStatus == null) return "狀態待確認";
		switch (this.activityStatus) {
	        case 0: return "待審核";
	        case 1: return "已審核";
	        case 2: return "已發布";
	        case 3: return "已退回";
	        case 4: return "取消";
	        case 5: return "延期";
	        default: return "狀態異常 (待確認)";
	    }
	}
	
	public String getRejectReasonText() {
		if (this.rejectReason == null) return "無";
		switch (this.rejectReason) {
			case 0: return "資訊不完整";
			case 1: return "內容不當";
			case 2: return "名額設定異常";
			case 3: return "其他";
			default: return "未定義的退件原因";
		}
	}
	
	public String getRegistrationStatusText() {
		if (this.activityStatus == null || this.activityStatus != 2) {
	        return "—";  // 非已發布狀態，不適用報名狀態判斷
	    }
	    LocalDateTime now = LocalDateTime.now();

	    if (now.isBefore(this.regisStart)) {
	        return "尚未開放報名";
	    } else if (!now.isAfter(this.regisEnd)) {
	        return "可報名中";
	    } else if (now.isBefore(this.activityEnd)) {
	        return "已截止報名";
	    } else {
	        return "已結束";
	    }
	}
	
	public boolean isEnded() {
	    return this.activityEnd != null && LocalDateTime.now().isAfter(this.activityEnd);
	}
}
