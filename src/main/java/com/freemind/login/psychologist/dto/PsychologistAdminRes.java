package com.freemind.login.psychologist.dto;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.freemind.article.entity.ArticleCat;
import com.freemind.login.admin.model.Admin;
import com.freemind.login.psychologist.entity.Psychologist;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//管理員看心理師
//沒密碼
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PsychologistAdminRes {
	
	//心理師編號
	private Integer psychId;
	//心理師帳號	
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
    
    // 銀行帳號 撥款需要
    private String bankAccount;

    
	
	
}
