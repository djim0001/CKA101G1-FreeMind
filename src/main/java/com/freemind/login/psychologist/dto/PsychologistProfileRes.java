package com.freemind.login.psychologist.dto;

import java.util.List;

import lombok.Data;
//會員看心理師回傳
//會員可以看到
//ID導覽用
//姓名
//性別
//手機
//心理師證照
//是否有執業許可
//心理師工作地點
//心理師諮詢費
//心理師照片
@Data
public class PsychologistProfileRes {

	private Integer psychId; 
	// 姓名
	private String name;
	// 性別
	private String gender;
	// 心理師證照
	private String psychCertificate;
	// 是否有執業許可
	private boolean hasPracticeLicense;
	// 心理師工作地點
	private String psychLoc;
	// 心理師諮詢費
	private Integer psychFee;
	// 心理師照片
	private String profilePic;
	
	private boolean scheduled;
	
	private List<String> expertiseList;
	
	private List<AvailableDateRes> availableDates;

}
