package com.freemind.login.psychologist.dto;

import java.time.LocalDate;

import lombok.Data;
//HTML顯示的時間表
@Data
public class AvailableDateRes {
	 	private LocalDate date;
	    private boolean hasAvailability; // 當天是否至少有一個小時可約
	    private boolean empty; //空白格
}
	