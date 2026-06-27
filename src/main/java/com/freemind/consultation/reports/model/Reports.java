package com.freemind.consultation.reports.model;

import java.time.LocalDateTime;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "consultation_reports")
public class Reports {

		@Id
		@GeneratedValue(strategy = GenerationType.IDENTITY)
		@Column(name = "report_id")
		private Integer reportId; //not null(PK)(AI)
		
		@Column(name = "member_id", nullable = false)
		@NotNull(message = "會員編號：請勿空白") 
		private Integer memberId; //not null(FK), 缺FK
		
		@Column(name = "order_id", nullable = false)
		@NotNull(message = "諮詢訂單編號：請勿空白")
		private Integer orderId; //not null(FK), 缺FK
		
		@Column(name = "admin_id")
		private Integer adminId; //(FK), 缺FK
		
		@Column(name = "issue_desc", nullable = false, length = 200)
		@NotEmpty(message = "問題描述：請勿空白")
		private String issueDesc; //not null,varchar(200)
		
		@Column(name = "report_date", nullable = false)
		@NotNull(message = "回報日期：請勿空白")
		@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
		private LocalDateTime reportDate; //not null
		
		@Column(name = "report_status", nullable = false) 
		@NotNull(message = "處理狀態：請勿空白")
		private Integer reportStatus = 0;//not null, default 0
		
		@Column(name = "report_note", length = 200)
		private String reportNote; //varchar(200)
		
		public Integer getReportId() {
			return reportId;
		}
		public void setReportId(Integer reportId) {
			this.reportId = reportId;
		}
		public Integer getMemberId() {
			return memberId;
		}
		public void setMemberId(Integer memberId) {
			this.memberId = memberId;
		}
		public Integer getOrderId() {
			return orderId;
		}
		public void setOrderId(Integer orderId) {
			this.orderId = orderId;
		}
		public Integer getAdminId() {
			return adminId;
		}
		public void setAdminId(Integer adminId) {
			this.adminId = adminId;
		}
		public String getIssueDesc() {
			return issueDesc;
		}
		public void setIssueDesc(String issueDesc) {
			this.issueDesc = issueDesc;
		}
		public LocalDateTime getReportDate() {
			return reportDate;
		}
		public void setReportDate(LocalDateTime reportDate) {
			this.reportDate = reportDate;
		}
		public Integer getReportStatus() {
			return reportStatus;
		}
		public void setReportStatus(Integer reportStatus) {
			this.reportStatus = reportStatus;
		}
		public String getReportNote() {
			return reportNote;
		}
		public void setReportNote(String reportNote) {
			this.reportNote = reportNote;
		}
		
}
