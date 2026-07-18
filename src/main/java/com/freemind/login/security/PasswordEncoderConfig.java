package com.freemind.login.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

	// 目前先用 NoOpPasswordEncoder（明文密碼）進行開發
	// 後續可改為 BCryptPasswordEncoder 進行加密
	
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }
    
    
	// BCrypt：業界主流、抗暴力破解的密碼加密方案。
	// 內建加鹽（Salt），即便兩個使用者明文密碼相同，加密後的密文也不同。
    
//    @Bean
//	public PasswordEncoder passwordEncoder() {
//		return new BCryptPasswordEncoder();
//	}
    
    
	// 註：其他類別（例如 EmpService、UserService、Test_Application_CommandLineRunner）
	// 需要使用 PasswordEncoder 時，直接用建構子注入即可，不需要在這裡額外處理。
}
