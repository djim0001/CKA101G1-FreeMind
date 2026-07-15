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

//告訴 Spring 容器「這是一個組態設定類別」
//Spring 在啟動時會掃描這個類別，並將內部所有標記 @Bean 的方法註冊到 IoC 容器中
@Configuration

//啟用 Spring Security
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
    	requestCache.setRequestMatcher(request -> "GET".equals(request.getMethod()));
    	// 請求方法是 GET 時，RequestCache 才會存入 Session
    	// POST(按讚/收藏)不保存，避免登入後被重放成JSON頁        
       
    	http
            .securityMatcher("/","/front-end/**","/member/**", "/article/**")
            .authorizeHttpRequests(auth -> auth
            		
            		//登入頁面
                .requestMatchers("/","/front-end/login").permitAll()//登入頁面
                
                	// 註冊與忘記密碼（含 OTP 驗證）給未登入的訪客
                .requestMatchers("/front-end/register/**",
                		"/front-end/forgot/**").permitAll()
                
                	//課程相關
                .requestMatchers("/member/course/select_course",
                		"/member/course/get_one_course").permitAll()
                
                	//文章相關
                .requestMatchers("/article/**").permitAll()
//                .requestMatchers("/member/article/**").permitAll() 
                
                	//活動相關
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
                .loginPage("/front-end/login")                    // 未登入訪問受保護頁面時導向此頁
                .loginProcessingUrl("/front-end/login")           // 對應 login.html 的 name="username"
                .usernameParameter("memberAccount")               // 對應 login.html 的 name="password"
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
                .logoutSuccessUrl("/front-end/login?logout")			 //login.html 會去偵測網址中是否有 logout 參數
                .invalidateHttpSession(true)							 //是否要在登出時刪除當前的 HTTP Session
                .deleteCookies("JSESSIONID", "remember-me-member")		 //登出時，瀏覽器刪除哪些 Cookie
                .permitAll()
            )

            .rememberMe(remember -> remember
                .key("memberSecretKeyUnique")                   		 // 後台專屬簽署私鑰
                .rememberMeCookieName("remember-me-member")     		 // 與前台 Cookie 名稱區隔
                .tokenRepository(memberPersistentTokenRepository())      // 用資料庫 persistent_logins 表存 token
                .tokenValiditySeconds(604800)                    		 // 7 天
                .userDetailsService(memberUserDetailsService)
                .rememberMeParameter("remember-me")              		 // 對應 login.html 的 checkbox name
            )

            .exceptionHandling(ex -> ex
            		
            		//未登入處理
            	    .authenticationEntryPoint((request, response, authException) -> {
            	        // fetch/AJAX（文章按讚收藏，前端帶 X-Requested-With）：回 401 給 JS 處理
            	        if (isAjax(request)) {
            	            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);			//回傳 401 
            	            return;
            	        }
            	        // 頁面瀏覽(GET)與傳統表單(課程收藏的POST)：導去登入頁
            	        requestCache.saveRequest(request, response); 							// 只有 GET 會真的被存
            	        response.sendRedirect(buildLoginRedirectUrl(request));
            	    })
            	    
            	    //權限不足處理
            	    .accessDeniedHandler((request, response, accessDeniedException) -> {
            	        if (isAjax(request)) {
            	            response.setStatus(HttpServletResponse.SC_FORBIDDEN); 				// 回傳 403
            	            return;
            	        }
            	        response.sendRedirect("/front-end/login"); 								//當權限不足時到這個網頁
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
        String referer = request.getHeader("Referer"); 										//Referer : 瀏覽器自動帶上的 HTTP 標頭，記錄了「使用者是在哪一個頁面發起這個請求的」。
        if (referer == null) {
            return "/front-end/login";
        }
        URI uri = URI.create(referer);				   										//將 Referer 字串轉為 Java URI 物件
        //uri.getPath() : 提取相對路徑去除 http://localhost:8080，只留下 "/back-end/login"
        String path = uri.getPath() + (uri.getQuery() != null ? "?" + uri.getQuery() : ""); //getQuery() : 如果使用者當前的網址有攜帶參數如 : /login?unauthorized，一併擷取下來拼回網址
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
    
    // 用 PersistentTokenRepository（series + token 雙欄位）取代純 Cookie 雜湊，
    // 一旦偵測到 token 異常（被竊取後重放），系統會強制該使用者的所有 Remember-Me 連線失效。
    @Bean
    public PersistentTokenRepository memberPersistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
//        tokenRepository.setCreateTableOnStartup(true);
        return tokenRepository;
    }
    
    // 註：PasswordEncoder 已在 com.securityConfig.passwordEncoder.PasswordEncoderConfig 全域宣告一次，
    // 這裡不重複宣告，避免 bean 衝突。
}
