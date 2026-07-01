package com.freemind.consultation.reports.model;

import java.time.LocalDateTime;

import com.freemind.consultation.orders.model.Orders;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
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
		
	@ManyToOne(fetch = FetchType.LAZY) 
	@JoinColumn(name = "order_id", referencedColumnName = "order_id")
	private Orders orders; //not null(FK), 缺FK>>已改
		
	@Column(name = "admin_id")
	private Integer adminId; //(FK), 缺FK
		
	@Column(name = "issue_desc", nullable = false, length = 200)
	@NotBlank(message = "問題描述：請勿空白，若無需描述，請填寫無")
	private String issueDesc; //not null,varchar(200)
		
	@Column(name = "report_date", nullable = false)
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
	
	@Override
	public String toString() {
		return "Reports [reportId=" + reportId + ", memberId=" + memberId + ", adminId="
				+ adminId + ", issueDesc=" + issueDesc + ", reportDate=" + reportDate + ", reportStatus=" + reportStatus
				+ ", reportNote=" + reportNote + "]";
	}
	
	public Orders getOrders() {
		return orders;
	}
	public void setOrders(Orders orders) {
		this.orders = orders;
	}
	

}
