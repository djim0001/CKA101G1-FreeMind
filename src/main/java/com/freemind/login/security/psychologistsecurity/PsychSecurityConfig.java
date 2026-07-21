package com.freemind.login.security.psychologistsecurity;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

@Configuration
public class PsychSecurityConfig {

	private final DataSource dataSource;
	private final PsychUserDetailsService psychUserDetailsService;

	public PsychSecurityConfig(DataSource dataSource, PsychUserDetailsService psychUserDetailsService) {
		this.dataSource = dataSource;
		this.psychUserDetailsService = psychUserDetailsService;
	}

	@Bean
	@Order(3) 
	public SecurityFilterChain psychFilterChain(HttpSecurity http) throws Exception {
		RequestCache requestCache = new HttpSessionRequestCache();

		http
			// 圈地盤:/psych/** 全歸這條鏈管(登入頁、個人頁、文章、預約管理都在內)
			.securityMatcher("/psych/**")

			.authorizeHttpRequests(auth -> auth
				.requestMatchers("/psych/psychologistLogin").permitAll()

            	// Google 登入
//				.requestMatchers("/front-end/login/google").permitAll()

            	// 註冊與忘記密碼（含 OTP 驗證）給未登入的訪客
				.requestMatchers("/psych/register/**","/psych/forgot/**").permitAll()
           
				.anyRequest().hasRole("PSYCH")
			)

			.userDetailsService(psychUserDetailsService)

			.requestCache(cache -> cache.requestCache(requestCache))

			.formLogin(form -> form
				.loginPage("/psych/psychologistLogin")
				.loginProcessingUrl("/psych/psychologistLogin")
				.usernameParameter("psychAccount")
				.passwordParameter("psychPassword")
				// 關鍵:登入成功當下把 psychId 塞進 Session,
				// 讓 @SessionAttribute("psychId") 與 Advice 全部直接可用
				.successHandler((request, response, authentication) -> {
					PsychUserDetails ud = (PsychUserDetails) authentication.getPrincipal();
					request.getSession().setAttribute("psychId",
							ud.getPsychologist().getPsychId());

					// 有被攔截前想去的頁面就送回去,否則去個人頁
					SavedRequest saved = requestCache.getRequest(request, response);
					response.sendRedirect(saved != null ? saved.getRedirectUrl() : "/");
				})
				.failureUrl("/psych/psychologistLogin?error=true")
				.permitAll()
			)

			.logout(logout -> logout
				.logoutUrl("/psych/logout")
				.logoutSuccessUrl("/")
				.invalidateHttpSession(true)
				// cookie名絕不能跟member共用,否則同瀏覽器登兩種身分會互蓋
				.deleteCookies("JSESSIONID", "remember-me-psych")
				.permitAll()
			)

			.rememberMe(remember -> remember
				.key("psychSecretKeyUnique") // key也要跟member不同
				.rememberMeCookieName("remember-me-psych")
				.tokenRepository(psychPersistentTokenRepository())
				.tokenValiditySeconds(604800)
				.userDetailsService(psychUserDetailsService)
				.rememberMeParameter("remember-me")
			)

//			.exceptionHandling(ex -> ex
//				.authenticationEntryPoint((request, response, authException) -> {
//					requestCache.saveRequest(request, response);
//					response.sendRedirect("/psych/psychologistLogin");
//				})
//			);
				
			.exceptionHandling(ex -> ex
			    .authenticationEntryPoint((request, response, authException) -> {
			        requestCache.saveRequest(request, response);
			        response.sendRedirect("/psych/psychologistLogin");
			    })
			    .accessDeniedHandler((request, response, accessDeniedException) ->
			        response.sendRedirect("/psych/psychologistLogin?unauthorized"))
			);
		
		
		
		return http.build();
	}

	@Bean
	public PersistentTokenRepository psychPersistentTokenRepository() {
		// 跟member共用同一張persistent_logins表,沒問題,不要再開setCreateTableOnStartup
		JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
		tokenRepository.setDataSource(dataSource);
		return tokenRepository;
	}
}