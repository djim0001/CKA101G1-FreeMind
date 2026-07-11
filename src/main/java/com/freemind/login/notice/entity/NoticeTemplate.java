package com.freemind.login.notice.entity;

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
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "notice_templates")
public class NoticeTemplate implements java.io.Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "template_id")
	private Integer templateId;

	@ManyToOne
	@JoinColumn(name = "admin_id", insertable = false, updatable = false)
	private Admin admin;

	//員工
	@Column(name = "admin_id", nullable = false)//系統抓
	private Integer adminId;

	//範本內容
	@Column(name = "template_content", nullable = false, length = 1000)
	@NotBlank(message = "範本內容: 請勿空白")
	@Size(max = 1000, message = "範本內容: 長度不可超過1000字")
	private String templateContent;

	public Integer getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
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

	public String getTemplateContent() {
		return templateContent;
	}

	public void setTemplateContent(String templateContent) {
		this.templateContent = templateContent;
	}

}
