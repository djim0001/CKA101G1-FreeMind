package com.freemind.login.security.membersecurity;

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

import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
public class GoogleLoginController {

    /** session attribute：已通過 Google 驗證、待註冊的 email（原本在 GoogleLoginSuccessHandler） */
    public static final String GOOGLE_REGISTER_EMAIL = "GOOGLE_REGISTER_EMAIL";

    private final MemberService memberService;
    private final GoogleIdTokenVerifier verifier;

    public GoogleLoginController(MemberService memberService,
            @Value("${google.client-id}") String clientId) {
        this.memberService = memberService;
        this.verifier = new GoogleIdTokenVerifier.Builder(
                new NetHttpTransport(), new GsonFactory())
                .setAudience(List.of(clientId))   // 驗 token 是發給本站的
                .build();
    }

    /** GIS callback：前端 fetch POST credential(JWT)，回 JSON {redirectUrl} */
    @PostMapping("/front-end/login/google")
    public Map<String, String> googleLogin(@RequestParam("credential") String credential,
            HttpServletRequest request, HttpServletResponse response) throws Exception {

        GoogleIdToken idToken = verifier.verify(credential);
        if (idToken == null || !Boolean.TRUE.equals(idToken.getPayload().getEmailVerified())) {
            return Map.of("redirectUrl", "/front-end/login?error=google");
        }
        String email = idToken.getPayload().getEmail();

        Member member = memberService.findByEmail(email);
        if (member == null) {                       // 新用戶 → 補資料註冊
            request.getSession().setAttribute(GOOGLE_REGISTER_EMAIL, email);
            return Map.of("redirectUrl", "/front-end/register");
        }
        if (member.getAccountStatus() != null && member.getAccountStatus() == 2) {
            return Map.of("redirectUrl", "/front-end/login?error=disabled");
        }
        if (member.getAccountStatus() == null || member.getAccountStatus() == 0) {
            member.setAccountStatus(1);             // Google 已驗信箱 → 啟用
            memberService.updateMember(member);
        }

        request.changeSessionId();                  // 防 session fixation
        MemberUserDetails userDetails = new MemberUserDetails(member);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(auth);
        SecurityContextHolder.setContext(context);
        new HttpSessionSecurityContextRepository().saveContext(context, request, response);

        return Map.of("redirectUrl", "/");
    }
}
