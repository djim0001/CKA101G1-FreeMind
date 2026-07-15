package com.freemind.login.psychologist.dto;


import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ConflictOrderRes {

	private Integer orderId;
	
	private LocalDateTime consStart;
	
	private LocalDateTime consEnd;
	
	private Integer orderStatus;
}
