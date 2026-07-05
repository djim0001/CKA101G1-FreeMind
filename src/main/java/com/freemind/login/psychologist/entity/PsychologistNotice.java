package com.freemind.login.psychologist.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "psychologist_notice")
public class PsychologistNotice implements java.io.Serializable{

	private static final long serialVersionUID = 1L;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "psych_notice_id")
	private Integer psychNoticeId;
	
	
	@ManyToOne
	@JoinColumn(name = "psych_id" , insertable = false, updatable = false)
	private Psychologist psychologist;
	
	
	//心理師
	@Column(name = "psych_id" , nullable = false)//選單
	private Integer psychId;
	
	
	//員工
	@Column(name = "admin_id" , nullable = false)//系統抓
	private Integer adminId;
	
	
	//通知內容
	@Column(name = "notice_content" , nullable = false , length = 1000)
	@NotBlank(message = ": 請勿空白")
	private String noticeContent;
	
	
	//通知種類 
	@Column(name = "notice_type", nullable = false)//選單 0 1 2
	@NotNull(message = "請選擇通知種類")
	@Max(value = 0,message = "通知種類不正確" )
	@Min(value = 2,message = "通知種類不正確" )
	private Byte noticeType;

	
	//建立時間
	@Column(name = "created_at" , nullable = false)//系統抓
	private Timestamp createdAt;
	
	
	//是否已讀
	@Column(name = "is_read" , nullable = false)
	private Boolean isRead;

	


	public Integer getPsychNoticeId() {
		return psychNoticeId;
	}

	public void setPsychNoticeId(Integer psychNoticeId) {
		this.psychNoticeId = psychNoticeId;
	}

	public Integer getPsychId() {
		return psychId;
	}

	public void setPsychId(Integer psychId) {
		this.psychId = psychId;
	}

	public Integer getAdminId() {
		return adminId;
	}

	public void setAdminId(Integer adminId) {
		this.adminId = adminId;
	}

	public String getNoticeContent() {
		return noticeContent;
	}

	public void setNoticeContent(String noticeContent) {
		this.noticeContent = noticeContent;
	}

	public Timestamp getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Timestamp createdAt) {
		this.createdAt = createdAt;
	}

	public Boolean getIsRead() {
		return isRead;
	}

	public void setIsRead(Boolean isRead) {
		this.isRead = isRead;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
	public Byte getNoticeType() {
		return noticeType;
	}

	public void setNoticeType(Byte noticeType) {
		this.noticeType = noticeType;
	}
	
	public String getNotice_typemsg() {
		String notice_typemsg="";
		Byte n = this.getNoticeType();
		if(n==null) notice_typemsg = "其他通知";
		switch(n) {
			case 0:
				notice_typemsg = "文章審核通知";break;
			case 1:
				notice_typemsg = "預約通知";break;
			case 2:
				notice_typemsg = "課程通知";break;
			default:
				notice_typemsg = "其他通知";break;
		}return notice_typemsg;
	}

}
