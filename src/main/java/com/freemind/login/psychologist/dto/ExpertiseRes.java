package com.freemind.login.psychologist.dto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//專長選項/心理師已勾選專長
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExpertiseRes {

	private Integer expertiseId;

	private String expertiseName;
}
