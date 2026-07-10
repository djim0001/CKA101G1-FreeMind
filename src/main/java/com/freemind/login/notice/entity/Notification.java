package com.freemind.login.notice.entity;

import java.sql.Timestamp;

import com.freemind.login.admin.model.Admin;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "notifications")
public class Notification implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "notice_id")
	private Integer noticeId;

	@ManyToOne
	@JoinColumn(name = "admin_id", insertable = false, updatable = false)
	private Admin admin;

	//員工
	@Column(name = "admin_id", nullable = false)//系統抓
	private Integer adminId;

	//內容
	@Column(name = "system_content", nullable = false, length = 1000)
	@NotBlank(message = "公告內容: 請勿空白")
	@Size(max = 1000, message = "公告內容: 長度不可超過1000字")
	private String systemContent;

	//建立時間
	@Column(name = "created_at", nullable = false)//系統抓
	private Timestamp createdAt;

	//公告狀態 0:隱藏 1:顯示
	@Column(name = "notice_status", nullable = false)//選單
	@NotNull(message = "請選擇公告狀態")
	private Byte noticeStatus;

	public Integer getNoticeId() {
		return noticeId;
	}

	public void setNoticeId(Integer noticeId) {
		this.noticeId = noticeId;
	}

	public Admin getAdmin() {
		return admin;
	}

	public void setAdmin(Admin admin) {
		this.admin = admin;
	}

	public Integer getAdminId() {
		return adminId;
	}

	public void setAdminId(Integer adminId) {
		this.adminId = adminId;
	}

	public String getSystemContent() {
		return systemContent;
	}

	public void setSystemContent(String systemContent) {
		this.systemContent = systemContent;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Byte getNoticeStatus() {
		return noticeStatus;
	}

	public void setNoticeStatus(Byte noticeStatus) {
		this.noticeStatus = noticeStatus;
	}

}
