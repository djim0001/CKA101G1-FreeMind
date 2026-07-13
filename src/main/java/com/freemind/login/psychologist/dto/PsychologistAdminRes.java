package com.freemind.login.psychologist.dto;

import java.time.LocalDateTime;

import lombok.Data;

//管理員看心理師
@Data
public class PsychologistAdminRes {

	private Integer psychId;

    private String psychAccount;

    private String name;

    private String gender;

    private String phoneNumber;

    private String email;

    private String psychCertificate;

    // 審核狀態:是否有執業許可
    private Boolean hasPracticeLicense;

    // 帳號狀態 0:未啟用 1:已啟用 2:停權
    private Integer accountStatus;

    private String psychLoc;

    private Integer psychFee;

    // 撥款需要
    private String bankAccount;

    private LocalDateTime regisAt;
	
	
}
