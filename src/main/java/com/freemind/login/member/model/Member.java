package com.freemind.login.member.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.freemind.consultation.orders.model.Orders;
import com.freemind.consultation.reports.model.Reports;
import com.freemind.course.course.model.CourseQaComment;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "member")
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Integer memberId;

	@Column(name = "member_account", length = 20)
	@NotEmpty(message = "會員帳號:請勿空白")
	@Size(max = 20, message = "會員帳號: 長度不可超過20字元")
	private String memberAccount;

	@Column(name = "member_password", length = 20)
	@NotEmpty(message = "會員密碼: 請勿空白")
	@Size(max = 20, message = "會員密碼: 長度不可超過20字元")
	@JsonIgnore
	private String memberPassword;

	@Column(name = "account_status", columnDefinition = "TINYINT DEFAULT 0")
	@NotNull(message = "帳號狀態: 請選擇")
	private Integer accountStatus;

	@Column(name = "name", length = 20)
	@NotEmpty(message = "姓名: 請勿空白")
	@Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,20}$", message = "姓名: 只能是中、英文字母、數字和_ , 且長度必需在2到20之間")
	private String name;

	@Column(name = "gender", length = 10)
	@NotEmpty(message = "性別: 請選擇")
	private String gender;

	@Column(name = "phone_number", length = 10)
	@NotEmpty(message = "手機號碼: 請勿空白")
	@Pattern(regexp = "^09[0-9]{8}$", message = "手機號碼格式不正確，必須為09開頭的10位數字")
	private String phoneNumber;

	@Column(name = "birthday")
	private LocalDate birthday;

	@Column(name = "city", length = 5)
	private String city;

	@Column(name = "dist", length = 5)
	private String dist;

	@Column(name = "address", length = 40)
	private String address;

	@Column(name = "regis_at")
	@NotNull(message = "加入時間: 請輸入正確的日期與時間")
	private LocalDateTime regisAt;

	@Lob
	@Column(name = "profile_pic", columnDefinition = "LONGBLOB")
	private byte[] profilePic;

	@Column(name = "card_number", length = 20)
	private String cardNumber;

	@Column(name = "nickname", length = 200)
	private String nickname;

	@Column(name = "email", length = 50)
	@NotEmpty(message = "信箱: 請勿空白")
	@Email(message = "信箱格式不正確")
	private String email;

	
	// 課程提問
	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	@OrderBy("member_id asc")
	private Set<CourseQaComment> courseQaComment;
	
	//諮商問題回報
	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
	private List<Reports> reportsList;
	
	//諮商訂單
	@OneToMany(mappedBy = "member", fetch = FetchType.LAZY)
	private List<Orders> ordersList;
		
	
	@Column(name = "bank_account", length = 20)
	private String bankAccount;

	public Integer getMemberId() {
		return memberId;
	}
	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}
	public String getMemberAccount() {
		return memberAccount;
	}
	public void setMemberAccount(String memberAccount) {
		this.memberAccount = memberAccount;
	}
	public String getMemberPassword() {
		return memberPassword;
	}
	public void setMemberPassword(String memberPassword) {
		this.memberPassword = memberPassword;
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
	public LocalDate getBirthday() {
		return birthday;
	}
	public void setBirthday(LocalDate birthday) {
		this.birthday = birthday;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getDist() {
		return dist;
	}
	public void setDist(String dist) {
		this.dist = dist;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public LocalDateTime getRegisAt() {
		return regisAt;
	}
	public void setRegisAt(LocalDateTime regisAt) {
		this.regisAt = regisAt;
	}
	public byte[] getProfilePic() {
		return profilePic;
	}
	public void setProfilePic(byte[] profilePic) {
		this.profilePic = profilePic;
	}
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getBankAccount() {
		return bankAccount;
	}
	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}
	public Set<CourseQaComment> getCourseQaComment() {
		return courseQaComment;
	}
	public void setCourseQaComment(Set<CourseQaComment> courseQaComment) {
		this.courseQaComment = courseQaComment;
	}
	public List<Reports> getReportsList() {
		return reportsList;
	}
	public void setReportsList(List<Reports> reportsList) {
		this.reportsList = reportsList;
	}
	public List<Orders> getOrdersList() {
		return ordersList;
	}
	public void setOrdersList(List<Orders> ordersList) {
		this.ordersList = ordersList;
	}
	
	
	
	
	
	
	
	
}
