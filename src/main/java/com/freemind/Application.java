package com.freemind;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}

//測試 BCrypt 雜湊值加密 : PasswordEncoderConfig 註解上面的 NoOpPasswordEncoder（明文密碼），開啟下方 BCrypt，資料庫執行 bCrypt.sql 手動加密現有密碼
//測試寄驗證信 : 預設為顯示在Console，使用gmail時 application 開啟 OTP-setting 輸入信箱、應用程式密碼
