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
			log.info("【開發模式-OTP】收件人={}, 用途={}, 驗證碼={}（30 秒內有效）", to, purposeText, otp);
			return;
		}

		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");  //指定郵件發送伺服器（SMTP）的主機名稱為 Google 的 SMTP 伺服器
		props.put("mail.smtp.port", "587");			    //587 Port。用於傳輸層安全協議（TLS）的標準 SMTP 連線埠
		props.put("mail.smtp.auth", "true");			//啟用 SMTP 身份驗證。發信前必須提供帳號與密碼進行登入
		props.put("mail.smtp.starttls.enable", "true"); //啟用 STARTTLS 安全加密。將原本明文的 TCP 連線升級為加密的 TLS 連線，確保傳輸過程中的帳號、密碼及郵件內容不被竊聽

		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
		});

		try {
			// MimeMessage 代表「一封電子郵件」的類別
			// 1.初始化信件物件，必須綁定一個連線會話 (Session)
			MimeMessage message = new MimeMessage(session); 
			
			// 2.設定寄件者 (From)
			message.setFrom(new InternetAddress(username));		//Java Mail API 中，InternetAddress 用來代表符合標準的電子郵件地址，
																//會檢查字串是否符合標準電子郵件格式（含有 @、是否有合法的網域名稱等）
																//自動將 Email 拆解為「個人名稱（Personal Name）」與「實際信箱（Address）
			// 3. 設定收件者 (To)
			message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
			
			// 4. 設定主旨 (Subject)，並指定 UTF-8 編碼避免中文亂碼
			message.setSubject("FreeMind " + purposeText + "驗證碼", "UTF-8");
			
			// 5. 設定內文 (Text)
			message.setText("您的驗證碼為：" + otp + "\n\n驗證碼 30 秒內有效，請勿將驗證碼提供給他人。", "UTF-8");
			Transport.send(message);
			log.info("OTP 驗證信已寄出：{}", to);
		} catch (Exception e) {
			// 寄信失敗不讓整個流程崩潰，記 log 讓使用者可按「重寄」再試
			log.error("OTP 驗證信寄送失敗：" + to, e);
		}
	}
}
