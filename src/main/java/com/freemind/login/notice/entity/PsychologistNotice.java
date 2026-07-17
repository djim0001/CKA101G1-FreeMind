package com.freemind.login.notice.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "psychologist_notice")
@NoArgsConstructor
@Getter
@Setter
public class PsychologistNotice  implements java.io.Serializable {

	private static  long  serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "psych_notice_id")
	private Integer psychNoticeId;
	
	@Column(name = "psych_id" , nullable = false)
	private Integer psychId;
	
	@Column(name = "admin_id" , nullable = false)
	private Integer adminId;
	
	@Column(name = "notice_content" , nullable = false , length = 1000)
	@NotBlank(message = "填寫通知內容")
	private String noticeContent;
	
	@Column(name = "notice_type" , nullable = false)
	@NotNull(message = "請選擇通知種類")
	@Min(value = 0 , message = "通知種類不正確")
	@Max(value = 2 , message = "通知種類不正確")
	private Byte noticeType;
	
	@Column(name = "created_at" , nullable = false)
	private Timestamp createdAt;
	
	@Column(name = "is_read" , nullable = false)
	private Boolean isRead = false;
	
	public String getNoticeTypeMsg() {
		Byte n = this.noticeType;
		if (n == null) {
			return "其他通知";
		}
		switch (n) {
			case 0: return "文章審核通知";
			case 1: return "預約通知";
			case 2: return "課程通知";
			default: return "其他通知";
		}
	}
	
	
}
