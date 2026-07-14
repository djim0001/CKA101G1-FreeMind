package com.freemind.login.member.otp;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * 寄送 OTP 驗證信。
 *
 * 開發模式（mail.otp.enabled=false，預設）：不真的寄信，把驗證碼印在伺服器 console，
 * 方便本機測試不用真信箱。
 *
 * 正式模式（mail.otp.enabled=true）：用 Gmail SMTP 寄信，
 * 需在 application.properties 填 mail.otp.username（Gmail 帳號）
 * 與 mail.otp.password（Google「應用程式密碼」，不是登入密碼）。
 */
@Service
public class OtpMailService {

	private static final Logger log = LoggerFactory.getLogger(OtpMailService.class);

	//@Value 的作用： 它會去讀取專案的設定檔（通常是 application.properties 或 application.yml）裡面的設定值。冒號後面（如 :false）代表預設值（如果設定檔沒寫，就直接套用這個值）
	@Value("${mail.otp.enabled:false}")
	private boolean mailEnabled;

	@Value("${mail.otp.username:}")
	private String username;

	@Value("${mail.otp.password:}")
	private String password;

	/**
	 * @param to          收件信箱
	 * @param purposeText 用途文字（顯示在信件主旨），例如「會員註冊」、「重設密碼」
	 * @param otp         6 位數驗證碼
	 */
	public void sendOtp(String to, String purposeText, String otp) {
		if (!mailEnabled) {
			// 開發模式：印在 console 就好
			log.info("【開發模式-OTP】收件人={}, 用途={}, 驗證碼={}（5 分鐘內有效）", to, purposeText, otp);
			return;
		}

		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");

		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(username));
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
			message.setSubject("FreeMind " + purposeText + "驗證碼", "UTF-8");
			message.setText("您的驗證碼為：" + otp + "\n\n驗證碼 5 分鐘內有效，請勿將驗證碼提供給他人。", "UTF-8");
			Transport.send(message);
			log.info("OTP 驗證信已寄出：{}", to);
		} catch (Exception e) {
			// 寄信失敗不讓整個流程崩潰，記 log 讓使用者可按「重寄」再試
			log.error("OTP 驗證信寄送失敗：" + to, e);
		}
	}
}
