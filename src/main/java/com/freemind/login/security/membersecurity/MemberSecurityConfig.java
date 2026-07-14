package com.freemind.login.security.membersecurity;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.NullRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;

@Configuration
@EnableWebSecurity
public class MemberSecurityConfig {

    private final DataSource dataSource;
    private final MemberUserDetailsService memberUserDetailsService;

    public MemberSecurityConfig(DataSource dataSource, MemberUserDetailsService memberUserDetailsService) {
        this.dataSource = dataSource;
        this.memberUserDetailsService = memberUserDetailsService;
    }

    @Bean
    @Order(2)
    public SecurityFilterChain memberFilterChain(HttpSecurity http) throws Exception {
    	HttpSessionRequestCache requestCache = new HttpSessionRequestCache();
    	requestCache.setRequestMatcher(request -> "GET".equals(request.getMethod())); // POST(按讚/收藏)不保存，避免登入後被重放成JSON頁        
       
    	http
//            .securityMatcher("/","/front-end/**", "/member/**", "/course/**")
            .securityMatcher("/","/front-end/**","/member/**", "/article/**")

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/","/front-end/login").permitAll()
                // 註冊與忘記密碼（含 OTP 驗證）：給未登入的訪客用，開放
                .requestMatchers("/front-end/register/**", "/front-end/forgot/**").permitAll()
                .requestMatchers("/member/course/select_course","/member/course/get_one_course").permitAll()
                .requestMatchers("/article/**").permitAll() 
//                .requestMatchers("/member/article/**").permitAll() 
                
                .requestMatchers(
                	    "/member/activity/activityIndex",
                	    "/member/activity/select_page",
                	    "/member/activity/listAllActivity",
                	    "/member/activity/listActivities_ByCompositeQuery",
                	    "/member/activity/listOneActivity",
                	    "/member/activity/activityImage"
                	).permitAll()
                .anyRequest().hasRole("MEMBER")
            )

            .userDetailsService(memberUserDetailsService)
            
//            .securityContext(context -> context.requireExplicitSave(true))
//            .requestCache(cache -> cache.requestCache(new NullRequestCache()))
//          
            .requestCache(cache -> cache.requestCache(requestCache))

            .formLogin(form -> form
                .loginPage("/front-end/login")
                .loginProcessingUrl("/front-end/login")
                .usernameParameter("memberAccount")
                .passwordParameter("memberPassword")
                .successHandler(memberLoginSuccessHandler(requestCache))
                .failureHandler((request, response, exception) -> {
                    // 密碼打錯時保留 redirect 參數，重試成功後仍能回原頁
                    String redirect = request.getParameter("redirect");
                    String url = "/front-end/login?error=true";
                    if (isSafeRedirect(redirect)) {
                        url += "&redirect=" + URLEncoder.encode(redirect, StandardCharsets.UTF_8);
                    }
                    response.sendRedirect(url);
                })
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/front-end/logout")
                .logoutSuccessUrl("/front-end/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID", "remember-me-member")
                .permitAll()
            )

            .rememberMe(remember -> remember
                .key("memberSecretKeyUnique")
                .rememberMeCookieName("remember-me-member")
                .tokenRepository(memberPersistentTokenRepository())
                .tokenValiditySeconds(604800)
                .userDetailsService(memberUserDetailsService)
                .rememberMeParameter("remember-me")
            )

            .exceptionHandling(ex -> ex
            	    .authenticationEntryPoint((request, response, authException) -> {
            	        // fetch/AJAX（文章按讚收藏，前端帶 X-Requested-With）：回 401 給 JS 處理
            	        if (isAjax(request)) {
            	            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            	            return;
            	        }
            	        // 頁面瀏覽(GET)與傳統表單(課程收藏的POST)：導去登入頁
            	        requestCache.saveRequest(request, response); // 只有 GET 會真的被存
            	        response.sendRedirect(buildLoginRedirectUrl(request));
            	    })
            	    .accessDeniedHandler((request, response, accessDeniedException) -> {
            	        if (isAjax(request)) {
            	            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            	            return;
            	        }
            	        response.sendRedirect("/front-end/login");
            	    })
            	);
           

   

        return http.build();
    }
    /** 判斷是否為前端 fetch/AJAX 請求 */
    private static boolean isAjax(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    /** redirect 只允許站內路徑，防 open redirect */
    private static boolean isSafeRedirect(String redirect) {
        return redirect != null && redirect.startsWith("/") && !redirect.startsWith("//");
    }

    /**
     * 未登入被擋時的登入頁網址。
     * GET：requestCache 已存、登入後重放，直接去登入頁。
     * 非 GET（傳統表單）：用 Referer 的路徑當 redirect 參數，登入後回原頁。
     */
    private String buildLoginRedirectUrl(HttpServletRequest request) {
        if ("GET".equals(request.getMethod())) {
            return "/front-end/login";
        }
        String referer = request.getHeader("Referer");
        if (referer == null) {
            return "/front-end/login";
        }
        URI uri = URI.create(referer);
        String path = uri.getPath() + (uri.getQuery() != null ? "?" + uri.getQuery() : "");
        if (!isSafeRedirect(path)) {
            return "/front-end/login";
        }
        return "/front-end/login?redirect=" + URLEncoder.encode(path, StandardCharsets.UTF_8);
    }

    /** 登入成功：redirect 參數優先，否則重放存下的 GET，最後回首頁 */
    private AuthenticationSuccessHandler memberLoginSuccessHandler(RequestCache requestCache) {
        SavedRequestAwareAuthenticationSuccessHandler savedRequestHandler =
                new SavedRequestAwareAuthenticationSuccessHandler();
        savedRequestHandler.setRequestCache(requestCache);
        savedRequestHandler.setDefaultTargetUrl("/");

        return (request, response, authentication) -> {
            String redirect = request.getParameter("redirect");
            if (isSafeRedirect(redirect)) {
                response.sendRedirect(redirect);
                return;
            }
            savedRequestHandler.onAuthenticationSuccess(request, response, authentication);
        };
    }
    @Bean
    public PersistentTokenRepository memberPersistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
//        tokenRepository.setCreateTableOnStartup(true);
        return tokenRepository;
    }
}
