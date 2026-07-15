package com.freemind.login.psychologist.dto;

import java.sql.Timestamp;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//心理師看自己
//心理師帳號
//帳號狀態
//姓名
//性別
//手機
//信箱
//心理師證照
//是否有執業許可
//心理師工作地點
//心理師諮詢費
//心理師可預約時段
//加入時間
//心理師照片
//銀行帳號
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PsychologistSelfRes {

	private String psychAccount;
	
    // 帳號狀態 0:未啟用 1:已啟用 2:停權
    private Integer accountStatus;
	
	private String name;

    private String gender;
	
	private String phoneNumber;
	
	private String email;
	// 心理師證照
	private String psychCertificate;
    // 審核狀態:是否有執業許可
    private Boolean hasPracticeLicense;

	private String psychLoc;
	
	private Integer psychFee;
	
	private String weeklyAvailability;
	
    private Timestamp regisAt;
	 // 心理師照片
  	private String profilePic;
    
	private String bankAccount;

	//專長
	private List<ExpertiseRes> expertiseList;

	
}
