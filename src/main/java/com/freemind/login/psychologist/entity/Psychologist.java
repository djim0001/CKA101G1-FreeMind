package com.freemind.login.psychologist.entity;

import java.sql.Timestamp;
import java.util.Set;

import com.freemind.article.entity.Article;
import com.freemind.consultation.orders.model.Orders;
import com.freemind.consultation.slots.model.Slots;
import com.freemind.course.course.model.Course;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "psychologist")
public class Psychologist implements java.io.Serializable{
	
	private static final long serialVersionUID = 1L;
	
	
	//心理師編號
	@Id 
	@Column(name = "psych_id")
	@GeneratedValue(strategy =  GenerationType.IDENTITY)
	private Integer psychId;
	
	
	//心理師帳號
	@Column(name = "psych_account", length = 20,nullable = false)
	private String psychAccount;
	
	
	//心理師密碼
	@Column(name = "psych_password", length = 60,nullable = false)
	private String psychPassword;
	
	
	//帳號狀態
	@Column(name = "account_status",nullable = false)
	private Integer accountStatus = 0;
	
	
	//姓名
	@Column(name = "name", length = 20,nullable = false)
	private String name;
	
	
	//性別
	@Column(name = "gender", length = 10,nullable = false)
	
	private String gender;
	
	
	//手機
	@Column(name = "phone_number", length = 10,nullable = false)
	private String phoneNumber;
	
	
	//信箱
	@Column(name = "email", length = 50,nullable = false)
	private String email;
	
	
	//心理師證照
	@Column(name = "psych_certificate", length = 30,nullable = false)
	private String psychCertificate;
	
	
	//是否有執業許可
	@Column(name = "has_practice_license",nullable = false)
	private Boolean hasPracticeLicense = false;
	
	
	//心理師工作地點
	@Column(name = "psych_loc", length = 50	,nullable = false)
	private String psychLoc;
	
	
	//心理師諮詢費
	@Column(name = "psych_fee",nullable = false)
	private Integer psychFee;
	
	
	//心理師可預約時段
	@Column(name = "weekly_availability", length = 168)
	private String weeklyAvailability;
	
	
	//加入時間
	@Column(name = "regis_at",nullable = false)
	private Timestamp regisAt;
	
	
	//心理師照片
	@Column(name = "profile_pic")
	private String profilePic;
	
	
	//銀行帳號
	@Column(name = "bank_account", length = 20)
	private String bankAccount;
	
	
	//心理師有啥專長
	@OneToMany(mappedBy = "psychologist", cascade = CascadeType.ALL)
	private Set<PsychologistExpertise> psychologistExpertises;

	public Set<PsychologistExpertise> getPsychologistExpertises() {
	    return psychologistExpertises;
	}

	public void setPsychologistExpertises(Set<PsychologistExpertise> psychologistExpertises) {
	    this.psychologistExpertises = psychologistExpertises;
	}
	
//	//預約訂單
	@OneToMany(mappedBy = "psychologist" , cascade = CascadeType.ALL)
	private Set<Orders> consultationOrders;
	
	
	public Set<Orders> getConsultationOrders() {
		return consultationOrders;
	}

	public void setConsultationOrders(Set<Orders> consultationOrders) {
		this.consultationOrders = consultationOrders;
	}

	
//	//預約時段
	@OneToMany(mappedBy = "psychologist" , cascade = CascadeType.ALL)
	
	private Set<Slots> consultationSlots;
	
	public Set<Slots> getConsultationSlots() {
		return consultationSlots;
	}
	
	public void setConsultationSlots(Set<Slots> consultationSlots) {
		this.consultationSlots = consultationSlots;
	}

	
	//課程
	@OneToMany(mappedBy = "psychologist",cascade = CascadeType.ALL)
	@OrderBy("course_id asc")
	private Set<Course> courses;

	public Set<Course> getCourses() {
		return courses;
	}

	public void setCourse(Set<Course> courses) {
		this.courses = courses;
	}
//
//	
//	//課程撥款紀錄
//	@OneToMany(mappedBy = "psychologist" , cascade = CascadeType.ALL)
//	
//	private set<Payouts> payouts;
//	
//	public set<Payouts> getPayouts() {
//		return payouts;
//	}
//
//	public void setPayouts(set<Payouts> payouts) {
//		this.payouts = payouts;
//	}
//
//	
	//文章
	@OneToMany(mappedBy = "psychologist" , cascade = CascadeType.ALL)
	
	private Set<Article> articles;
	
	public Set<Article> getArticles() {
		return articles;
	}

	public void setArticles(Set<Article> articles) {
		this.articles = articles;
	}

	

	

	


	
	
	
	
	
	
	public Integer getPsychId() {
		return psychId;
	}

	public void setPsychId(Integer psychId) {
		this.psychId = psychId;
	}

	public String getPsychAccount() {
		return psychAccount;
	}

	public void setPsychAccount(String psychAccount) {
		this.psychAccount = psychAccount;
	}

	public String getPsychPassword() {
		return psychPassword;
	}

	public void setPsychPassword(String psychPassword) {
		this.psychPassword = psychPassword;
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

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPsychCertificate() {
		return psychCertificate;
	}

	public void setPsychCertificate(String psychCertificate) {
		this.psychCertificate = psychCertificate;
	}

	public Boolean getHasPracticeLicense() {
		return hasPracticeLicense;
	}

	public void setHasPracticeLicense(Boolean hasPracticeLicense) {
		this.hasPracticeLicense = hasPracticeLicense;
	}

	public String getPsychLoc() {
		return psychLoc;
	}

	public void setPsychLoc(String psychLoc) {
		this.psychLoc = psychLoc;
	}

	public Integer getPsychFee() {
		return psychFee;
	}

	public void setPsychFee(Integer psychFee) {
		this.psychFee = psychFee;
	}

	public String getWeeklyAvailability() {
		return weeklyAvailability;
	}

	public void setWeeklyAvailability(String weeklyAvailability) {
		this.weeklyAvailability = weeklyAvailability;
	}

	public Timestamp getRegisAt() {
		return regisAt;
	}

	public void setRegisAt(Timestamp regisAt) {
		this.regisAt = regisAt;
	}

	public String getProfilePic() {
		return profilePic;
	}

	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	
	
	
	
	
}
