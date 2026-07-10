package com.freemind.login.admin.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.freemind.activity.activity.model.Activity;
import com.freemind.consultation.reports.model.Reports;
import com.freemind.course.course.model.Course;
import com.freemind.login.permission.model.Permission;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "admin")
public class Admin implements java.io.Serializable {
	private static final long serialVersionUID = 1L;

	@Id //設定主鍵
	@GeneratedValue(strategy = GenerationType.IDENTITY) //自動遞增AUTO_INCREMENT
	@Column(name = "admin_id")
	private Integer adminId;          // admin_id => adminId (PK)(AI)
	
	@Column(name = "admin_account")
	@NotEmpty(message="管理員帳號: 請勿空白")
	private String adminAccount;

	@Column(name = "admin_password")
	@NotEmpty(message="管理員密碼: 請勿空白")
	@JsonIgnore
	private String adminPassword;

	@Column(name = "account_status", columnDefinition = "TINYINT")
	@NotNull(message="帳號狀態請選擇")
	private Integer accountStatus;

	@Column(name = "name")
	@NotEmpty(message="管理員姓名: 請勿空白")
	@Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,10}$", message = "管理員姓名: 只能是中、英文字母、數字和_ , 且長度必需在2到10之間")
	private String name;

	@Column(name = "phone_number")
	@NotEmpty(message="手機號碼請勿空白")
	@Pattern(regexp = "^09[0-9]{8}$", message = "手機號碼格式不正確，必須為09開頭的10位數字")
	private String phoneNumber;

	@Column(name = "hiredate")
	@NotNull(message="請輸入正確的入職日期與時間")
	private LocalDateTime hiredate;

	@Lob
	@Column(name = "profile_pic", columnDefinition = "LONGBLOB")
	private byte[] profilePic;  
	
	@OneToMany(mappedBy = "admin", fetch = FetchType.LAZY) 
	private List<Reports> reportsList;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(
		name = "admin_permissions",
		joinColumns = @JoinColumn(name = "admin_id"),
		inverseJoinColumns = @JoinColumn(name = "perm_id")
	)
	private Set<Permission> permissions = new HashSet<>();
	
	// 活動
	@OneToMany(mappedBy = "admin")
	private Set<Activity> activities;
	
	//課程
	@OneToMany(mappedBy = "admin",cascade = CascadeType.ALL)
	private Set<Course> courses;


	public Integer getAdminId() {
		return adminId;
	}

	public void setAdminId(Integer adminId) {
		this.adminId = adminId;
	}

	public String getAdminAccount() {
		return adminAccount;
	}

	public void setAdminAccount(String adminAccount) {
		this.adminAccount = adminAccount;
	}

	public String getAdminPassword() {
		return adminPassword;
	}

	public void setAdminPassword(String adminPassword) {
		this.adminPassword = adminPassword;
	}

	public Integer getAccountStatus() {
		return accountStatus;
	}

	public void setAccountStatus(Integer accountStatus) {
		this.accountStatus = accountStatus;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public LocalDateTime getHiredate() {
		return hiredate;
	}

	public void setHiredate(LocalDateTime hiredate) {
		this.hiredate = hiredate;
	}

	public byte[] getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(byte[] profilePic) {
		this.profilePic = profilePic;
	}

	public List<Reports> getReportsList() {
		return reportsList;
	}

	public void setReportsList(List<Reports> reportsList) {
		this.reportsList = reportsList;
	}
	
	public Set<Permission> getPermissions() {
		return permissions;
	}

	public void setPermissions(Set<Permission> permissions) {
		this.permissions = permissions;
	}
	
	public Set<Activity> getActivities() {
	    return activities;
	}

	public void setActivities(Set<Activity> activities) {
	    this.activities = activities;
	}

	public Set<Course> getCourses() {
		return courses;
	}

	public void setCourses(Set<Course> courses) {
		this.courses = courses;
	}
	
	
}
