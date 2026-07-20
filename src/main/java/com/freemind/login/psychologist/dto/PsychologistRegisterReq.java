package com.freemind.login.psychologist.dto;

import java.time.LocalDateTime;

import com.freemind.article.entity.ArticleCat;
import com.freemind.login.admin.model.Admin;
import com.freemind.login.psychologist.entity.Psychologist;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//心理師註冊帳號時用
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PsychologistRegisterReq {

	//心理師編號
	//心理師帳號
	@NotBlank(message="帳號請勿空白")
	@Size(max = 20, message = "帳號最多20字")
	private String psychAccount;
	//心理師密碼
	@NotBlank(message="密碼請勿空白")
	@Size(min = 6 , max = 20 , message = "密碼長度須為6~20字")
	private String psychPassword;
	
	@NotBlank(message = "請再次輸入密碼")
	private String confirmPassword;//帳號狀態
	//姓名
	@NotBlank(message="姓名請勿空白")
	@Size(max = 20, message = "姓名最多20字")
	private String name;
	//性別
	@NotBlank(message="性別請勿空白")
	@Size(max = 10, message = "性別最多10字")
	private String gender;
	//手機
	@NotBlank(message="手機請勿空白")
	@Pattern(regexp = "^09[0-9]{8}$" , message = "手機請輸入10碼數字")
	private String phoneNumber;
	//信箱
	@NotBlank(message = "信箱請勿空白")
    @Email(message = "信箱格式錯誤")
	@Size(max = 50, message = "信箱最多50字")
	private String email;
	//心理師證照
	@NotBlank(message="證照請勿空白")
	private String psychCertificate;
	//是否有執業許可
	//心理師工作地點
	@NotBlank(message="工作地點請勿空白")
	@Size(max = 50, message = "工作地點最多50字")
	private String psychLoc;
	//心理師諮詢費
	@NotNull(message="諮詢費請勿空白")
	@Min(value = 0, message = "諮詢費不可為負數")
	private Integer psychFee;
	//心理師可預約時段
	//加入時間
	//心理師照片
	//銀行帳號
}
