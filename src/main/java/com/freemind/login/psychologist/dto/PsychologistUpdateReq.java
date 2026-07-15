package com.freemind.login.psychologist.dto;

import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//心理師更新自己資料
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PsychologistUpdateReq {



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
    @Pattern(regexp = "^(?:[01]{168})$", message = "時段格式錯誤")
    private String weeklyAvailability;

    private String profilePic;
    
    // 銀行帳號(選填)
    @Size(max = 20, message = "銀行帳號最多20字")
    private String bankAccount;
    
    
    
    // 專長(勾選既有 Expertise 的 id,整批取代舊設定)
 	// 傳 null = 不更動專長;傳空 list = 清空專長
 	private List<Integer> expertiseIds;

 	// ===== 更改上班時間撞到已被預約的時段時,逐筆決定 =====
 	// 確認視窗中每筆衝突訂單勾「保留」或「取消」:
 	// keepOrderIds:保留(照常做完會談)
 	// cancelOrderIds:取消(orderStatus改2、釋放slot)
 	// 衝突訂單只要有任何一筆「兩邊都沒出現」,後端就不更新並退回衝突清單
 	private List<Integer> keepOrderIds;

 	private List<Integer> cancelOrderIds;

}
