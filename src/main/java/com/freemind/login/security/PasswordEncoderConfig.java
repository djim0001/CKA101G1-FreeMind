package com.freemind.login.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class PasswordEncoderConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // 目前先用 NoOpPasswordEncoder（明文密碼）進行開發
        // 後續可改為 BCryptPasswordEncoder 進行加密
        return NoOpPasswordEncoder.getInstance();
    }
}
