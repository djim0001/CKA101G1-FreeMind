package com.freemind.login.psychologist.dto;

import lombok.Data;

//心理師看自己
@Data
public class PsychologistSelfRes {

	private String psychAccount;
	
	private String name;
	
	private String phoneNumber;
	
	private String email;
	
	private String psychCertificate;
	
	private String psychLoc;
	
	private Integer psychFee;
	
	private String weeklyAvailability;
	
	private String bankAccount;
	
	
}
