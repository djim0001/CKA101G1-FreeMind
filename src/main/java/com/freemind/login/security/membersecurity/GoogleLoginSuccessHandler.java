package com.freemind.login.security.membersecurity;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Component;

import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Google 登入成功後的處理：用 Google 回傳的 email 對應會員。
 *
 * 1. email 是既有會員 → 把 Authentication 換成 MemberUserDetails 的 token 再登入，
 *    這樣 authentication.getName() 仍回傳 memberAccount，既有 Controller 全部不用改。
 *    若 accountStatus=0（註冊後沒驗 OTP），因 Google 已證明信箱所有權，直接啟用。
 * 2. email 查無會員 → 不登入，把 email 存進 session（GOOGLE_REGISTER_EMAIL），
 *    導去註冊頁讓使用者補填其餘資料。存 session 而非 URL 參數，
 *    避免有人偽造參數冒用「已通過 Google 驗證」的信箱。
 * 3. accountStatus=2（停權）→ 不登入，導回登入頁顯示停權訊息。
 */
@Component
public class GoogleLoginSuccessHandler implements AuthenticationSuccessHandler {

	/** session attribute：存放已通過 Google 驗證、待註冊的 email（MemberAuthController 看的東西） */
	public static final String GOOGLE_REGISTER_EMAIL = "GOOGLE_REGISTER_EMAIL";

	private final MemberService memberService;

	public GoogleLoginSuccessHandler(MemberService memberService) {
		this.memberService = memberService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException {

		// oauth2Login 的 principal 是 OAuth2User（openid scope 下實際為 OidcUser）
		OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
		String email = oauthUser.getAttribute("email");

		if (email == null) {
			// 理論上 scope 有 email 就拿得到；拿不到視為授權失敗
			clearAuthentication(request);								//清空這一次未完成的登入認證狀態，清空session
			response.sendRedirect("/front-end/login?error=google");
			return;
		}

		Member member = memberService.findByEmail(email);

		// (2) 查無會員：不登入，記下 email 導去註冊頁
		if (member == null) {
			clearAuthentication(request);
			request.getSession().setAttribute(GOOGLE_REGISTER_EMAIL, email);
			response.sendRedirect("/front-end/register");
			return;
		}

		// (3) 停權會員：拒絕登入
		if (member.getAccountStatus() != null && member.getAccountStatus() == 2) {
			clearAuthentication(request);
			response.sendRedirect("/front-end/login?error=disabled");
			return;
		}

		// (1) 既有會員：未啟用就直接啟用（Google 已驗證信箱）
		if (member.getAccountStatus() == null || member.getAccountStatus() == 0) {
			member.setAccountStatus(1);
			memberService.updateMember(member);
		}

		// 把 OAuth2 的 Authentication 換成與表單登入相同的 MemberUserDetails token，
		// 並存回 session，讓後續請求的身分與一般登入完全一致
		MemberUserDetails userDetails = new MemberUserDetails(member);
		UsernamePasswordAuthenticationToken newAuth =
				new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(newAuth);
		SecurityContextHolder.setContext(context);
		new HttpSessionSecurityContextRepository().saveContext(context, request, response);

		response.sendRedirect("/");
	}

	/** 不採用這次 OAuth2 登入身分：清掉 SecurityContext 與 session 裡已存的認證 */
	private void clearAuthentication(HttpServletRequest request) {
		SecurityContextHolder.clearContext();
		HttpSession session = request.getSession(false);
		if (session != null) {
			session.removeAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);
		}
	}
}
