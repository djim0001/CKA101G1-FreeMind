package com.freemind.login.security.bCrypt;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 獨立小工具（非 Spring Bean，直接 main() 執行），
 * 用來手動產生測試帳號的 BCrypt 雜湊值，方便塞測試資料到資料庫。
 * 密碼加密方案配置見 com.securityConfig.passwordEncoder.PasswordEncoderConfig
 */
public class BCrypt_encrypt {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println("===== 管理員帳號 =====");
        System.out.println("admin01 (p@ss1234): " + encoder.encode("p@ss1234"));
        System.out.println("admin02 (p@ss5678): " + encoder.encode("p@ss5678"));
        System.out.println("admin03 (p@ss9012): " + encoder.encode("p@ss9012"));
        System.out.println("admin04 (p@ss3456): " + encoder.encode("p@ss3456"));
        System.out.println("admin05 (p@ss6656): " + encoder.encode("p@ss6656"));
        System.out.println("admin06 (p@ss6656): " + encoder.encode("p@ss6656"));

        System.out.println("\n===== 會員帳號 =====");
        System.out.println("user01 (pwd12345): " + encoder.encode("pwd12345"));
        System.out.println("user02 (pwd23456): " + encoder.encode("pwd23456"));
        System.out.println("user03 (pwd34567): " + encoder.encode("pwd34567"));
        System.out.println("user04 (pwd45678): " + encoder.encode("pwd45678"));

    }

}