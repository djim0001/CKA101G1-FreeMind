package com.freemind.login.psychologist.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

//心理師改密碼
@Data
public class PsychologistPasswordReq {

	@NotBlank(message = "請輸入就密碼")
	private String oldPassword;
	
	@NotBlank(message = "請輸入新密碼")
	@Size(min = 6 , max = 20 , message = "密碼長度須為6~20字")
	private String newPassword;
	
	@NotBlank(message = "請再次輸入新密碼")
	private String confiirmPasswoed;
}
