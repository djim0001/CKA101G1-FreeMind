package com.freemind.login.psychologist.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

//心理師更新自己資料
@Data
public class PsychologistUpdateReq {

    @NotBlank(message = "姓名請勿空白")
    @Size(max = 20, message = "姓名最多20字")
    private String name;

    @NotBlank(message = "性別請勿空白")
    @Size(max = 10, message = "性別最多10字")
    private String gender;

    @NotBlank(message = "手機請勿空白")
    @Pattern(regexp = "^09[0-9]{8}$", message = "手機格式錯誤,請輸入09開頭共10碼")
    private String phoneNumber;

    @NotBlank(message = "信箱請勿空白")
    @Email(message = "信箱格式錯誤")
    @Size(max = 50, message = "信箱最多50字")
    private String email;

    @NotBlank(message = "工作地點請勿空白")
    @Size(max = 50, message = "工作地點最多50字")
    private String psychLoc;

    @NotNull(message = "諮詢費請勿空白")
    @Min(value = 0, message = "諮詢費不可為負數")
    private Integer psychFee;

    // 可預約時段(選填,168 碼 0/1 字串)
    @Size(max = 168, message = "時段格式錯誤")
    private String weeklyAvailability;

    // 銀行帳號(選填)
    @Size(max = 20, message = "銀行帳號最多20字")
    private String bankAccount;
}
