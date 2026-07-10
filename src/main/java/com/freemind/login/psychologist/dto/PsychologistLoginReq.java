package com.freemind.login.psychologist.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

//心理師登入時用
@Data
public class PsychologistLoginReq implements java.io.Serializable{
	
	@NotBlank(message = "帳號請勿空白")
    private String psychAccount;

    @NotBlank(message = "密碼請勿空白")
    private String psychPassword;
}
