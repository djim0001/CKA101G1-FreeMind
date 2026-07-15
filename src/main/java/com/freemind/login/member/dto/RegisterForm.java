package com.freemind.login.member.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * 註冊表單專用物件（DTO）。
 *
 * 不直接用 Member 實體綁定表單的原因：
 * 1. 實體的 memberPassword 存的是 BCrypt ；
 *    「原始密碼」的強度規則要在這裡驗。
 * 2. 實體有 regisAt、accountStatus 等 @NotNull 欄位不在表單上，直接 @Valid 會誤報錯誤。
 */
public class RegisterForm {

	/**
	 * 密碼強度規則（註冊與重設密碼共用）：
	 * (?=.*[a-z]) 至少一個小寫、(?=.*[A-Z]) 至少一個大寫、(?=.*\d) 至少一個數字，長度 8~20
	 */
	public static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,20}$";
	public static final String PASSWORD_MESSAGE = "密碼: 長度需8到20字元，且必須包含英文大寫、小寫與數字";

	@NotEmpty(message = "會員帳號: 請勿空白")
	@Size(max = 20, message = "會員帳號: 長度不可超過20字元")
	@Pattern(regexp = "^[^@]+$", message = "會員帳號: 不可包含 @ 字元")
	private String memberAccount;

	@NotEmpty(message = "會員密碼: 請勿空白")
	@Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,20}$", message = "密碼: 長度需8到20字元，且必須包含英文大寫、小寫與數字")
	private String memberPassword;

	@NotEmpty(message = "姓名: 請勿空白")
	@Pattern(regexp = "^[(一-龥)(a-zA-Z0-9_)]{2,20}$", message = "姓名: 只能是中、英文字母、數字和_ , 且長度必需在2到20之間")
	private String name;

	@NotEmpty(message = "性別: 請選擇")
	private String gender;

	@NotEmpty(message = "手機號碼: 請勿空白")
	@Pattern(regexp = "^09[0-9]{8}$", message = "手機號碼格式不正確，必須為09開頭的10位數字")
	private String phoneNumber;

	@NotEmpty(message = "信箱: 請勿空白")
	@Email(message = "信箱格式不正確")
	private String email;

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
}
