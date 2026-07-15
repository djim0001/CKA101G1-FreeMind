package com.freemind.login.member.otp;

import java.security.SecureRandom;
import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * OTP（一次性驗證碼）服務，使用 Redis 存放：
 *
 *   key = otp:<purpose>:<email>        （例：otp:register:xxx@gmail.com）
 *   value = 6 位數驗證碼
 *   TTL = 5 分鐘，時間到 Redis 自動刪除（不用自己寫排程清理）
 *
 * 另外用 otp:cooldown:<purpose>:<email>（TTL 60 秒）做重寄冷卻，防止狂按重寄灌爆信箱。
 */
@Service
public class OtpService {

	/** 驗證碼有效時間 */
	private static final Duration OTP_TTL = Duration.ofMinutes(5);
	/** 重寄冷卻時間 */
	private static final Duration RESEND_COOLDOWN = Duration.ofSeconds(60);

	private final StringRedisTemplate stringRedisTemplate;
	private final SecureRandom random = new SecureRandom();

	public OtpService(StringRedisTemplate stringRedisTemplate) {
		this.stringRedisTemplate = stringRedisTemplate;
	}

	/**
	 * 產生 6 位數驗證碼並存入 Redis（5 分鐘有效）。
	 *
	 * @param purpose 用途，例如 "register"（註冊）、"reset"（重設密碼），不同用途的驗證碼互不通用
	 * @return 驗證碼；若仍在冷卻時間內（60 秒內已寄過）回傳 null
	 */
	public String generateOtp(String purpose, String email) {
		String cooldownKey = "otp:cooldown:" + purpose + ":" + email;
		// setIfAbsent = Redis 的 SETNX：key 不存在才寫入成功。寫入失敗代表 60 秒內已產生過
		Boolean firstTime = stringRedisTemplate.opsForValue().setIfAbsent(cooldownKey, "1", RESEND_COOLDOWN);
		if (!Boolean.TRUE.equals(firstTime)) {
			return null; // 冷卻中
		}

		String otp = randomAlphanumeric(8); // 8 位英數混合，例：aK3x9Qm2
		String otpKey = "otp:" + purpose + ":" + email;
		stringRedisTemplate.opsForValue().set(otpKey, otp, OTP_TTL);
		return otp;
	}
	
	/** 驗證碼字元集（大寫+小寫+數字） */
	private static final String OTP_CHARS =
	        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

	private String randomAlphanumeric(int length) {
	    StringBuilder sb = new StringBuilder(length);
	    for (int i = 0; i < length; i++) {
	        sb.append(OTP_CHARS.charAt(random.nextInt(OTP_CHARS.length())));
	    }
	    return sb.toString();
	}

	/**
	 * 驗證驗證碼；驗證成功即從 Redis 刪除（一次性，同一組驗證碼不能用第二次）。
	 */
	public boolean verifyOtp(String purpose, String email, String otp) {
		if (otp == null || otp.isBlank()) {
			return false;
		}
		String otpKey = "otp:" + purpose + ":" + email;
		String saved = stringRedisTemplate.opsForValue().get(otpKey);
		if (saved != null && saved.equals(otp.trim())) {
			stringRedisTemplate.delete(otpKey); // 用過即銷毀
			return true;
		}
		return false;
	}
}
