package com.freemind.login.psychologist.dto;

import lombok.Data;

//會員看心理師回傳
@Data
public class PsychologistProfileRes {

	private String name;
	
	private String gender;
	
	private String psychoc;
	
	private Integer psychFee;
	
	private Boolean hasPracticeLicense;
	
}
