package com.freemind.login.psychologist.controller;



import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.freemind.login.psychologist.entity.Psychologist;
import com.freemind.login.psychologist.service.PsychologistService;
import com.freemind.login.security.psychologistsecurity.PsychUserDetails;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 心理師版 Google 登入，邏輯完全比照 GoogleLoginController（會員版）。
 * 用同一組 google.client-id 設定值，因為是同一個 Google OAuth Client。
 *
 * 前端串接方式跟會員一樣：GIS callback 取得 credential(JWT) 後
 * POST 到 /front-end/psychologist/login/google，回傳 JSON {redirectUrl}。
 *
 * 這支只處理「登入」；如果你們心理師登入頁的前端 JS 跟會員共用同一支 ui.js，
 * 記得確認呼叫的網址有依身分切到這支，而不是誤打到會員那支。
 */
@RestController
public class GooglePsychologistLoginController {

	/** session attribute：已通過 Google 驗證、待註冊的 email（心理師專用，跟會員的 key 分開） */
	public static final String GOOGLE_REGISTER_EMAIL = "GOOGLE_REGISTER_EMAIL_PSYCH";

	private final PsychologistService psychologistService;
	private final GoogleIdTokenVerifier verifier;

	public GooglePsychologistLoginController(PsychologistService psychologistService,
			@Value("${google.client-id}") String clientId) {
		this.psychologistService = psychologistService;
		this.verifier = new GoogleIdTokenVerifier.Builder(
				new NetHttpTransport(), new GsonFactory())
				.setAudience(List.of(clientId))
				.build();
	}

	@PostMapping("/front-end/psychologist/login/google")
	public Map<String, String> googleLogin(@RequestParam("credential") String credential,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		GoogleIdToken idToken = verifier.verify(credential);
		if (idToken == null || !Boolean.TRUE.equals(idToken.getPayload().getEmailVerified())) {
			return Map.of("redirectUrl", "/front-end/psych/login?error=google");
		}
		String email = idToken.getPayload().getEmail();

		Psychologist psychologist = psychologistService.findByEmail(email);
		if (psychologist == null) { // 新用戶 → 補資料註冊
			request.getSession().setAttribute(GOOGLE_REGISTER_EMAIL, email);
			return Map.of("redirectUrl", "/front-end/psych/register");
		}
		if (psychologist.getAccountStatus() != null && psychologist.getAccountStatus() == 2) {
			return Map.of("redirectUrl", "/front-end/psych/login?error=disabled");
		}
		if (psychologist.getAccountStatus() == null || psychologist.getAccountStatus() == 0) {
			psychologist.setAccountStatus(1); // Google 已驗信箱 → 啟用
			psychologistService.updatePsychologist(psychologist);
		}

		request.changeSessionId(); // 防 session fixation
		PsychUserDetails userDetails = new PsychUserDetails(psychologist);
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
				userDetails, null, userDetails.getAuthorities());
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(auth);
		SecurityContextHolder.setContext(context);
		new HttpSessionSecurityContextRepository().saveContext(context, request, response);

		return Map.of("redirectUrl", "/front-end/psych/dashboard");
	}
}
