package com.freemind.activity.report.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.freemind.activity.activity.model.Activity;
import com.freemind.login.admin.model.Admin;
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
import jakarta.validation.constraints.Size;

@Entity
@Table(name="activity_reports")
public class ActivityReport implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="report_id")
	private Integer reportId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="activity_id", nullable=false)
	private Activity activity;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="admin_id")
	private Admin admin;

	@Column(name="report_content", nullable=false, length=500)
	@Size(max=500, message="回報問題內容：長度不能超過{max}")
	private String reportContent;
	
	@Column(name="report_status", nullable=false, columnDefinition="tinyint")
	private Integer reportStatus;
	
	@Column(name="reply_content", length=500)
	@Size(max=500, message="後台人員回覆內容：長度不能超過{max}")
	private String replyContent;
	
	@Column(name="created_at", nullable=false)
	private LocalDateTime createdAt;
	
	@Column(name="replied_at")
	private LocalDateTime repliedAt;

	public Integer getReportId() {
		return reportId;
	}

	public void setReportId(Integer reportId) {
		this.reportId = reportId;
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

	public Admin getAdmin() {
		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;
	}

	public String getReportContent() {
		return reportContent;
	}

	public void setReportContent(String reportContent) {
		this.reportContent = reportContent;
	}

	public Integer getReportStatus() {
		return reportStatus;
	}

	public void setReportStatus(Integer reportStatus) {
		this.reportStatus = reportStatus;
	}

	public String getReplyContent() {
		return replyContent;
	}

	public void setReplyContent(String replyContent) {
		this.replyContent = replyContent;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getRepliedAt() {
		return repliedAt;
	}

	public void setRepliedAt(LocalDateTime repliedAt) {
		this.repliedAt = repliedAt;
	}
	
	public String getReportStatusText() {
		if (this.reportStatus == null) return "狀態待確認";
		switch (this.reportStatus) {
		case 0: return "待處理";
		case 1: return "處理中";
		case 2: return "已處理";
		default: return "狀態異常(待確認)";
		}
	}
}
