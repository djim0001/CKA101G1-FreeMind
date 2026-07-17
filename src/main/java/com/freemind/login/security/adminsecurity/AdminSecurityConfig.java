package com.freemind.login.security.adminsecurity;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;


@Configuration
@EnableWebSecurity
public class AdminSecurityConfig {

	private final DataSource dataSource;
    private final AdminUserDetailsService adminUserDetailsService;

    public AdminSecurityConfig(DataSource dataSource, AdminUserDetailsService adminUserDetailsService) {
        this.dataSource = dataSource;
        this.adminUserDetailsService = adminUserDetailsService;
    }
    @Bean
    @Order(1)
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
        RequestCache requestCache = new HttpSessionRequestCache();

        http
//            .securityMatcher("/","/back-end/**", "/admin/**","/course_index/**")
            .securityMatcher("/","/back-end/**", "/admin/**")

            .authorizeHttpRequests(auth -> auth
                // 登入頁面允許所有人訪問
                .requestMatchers("/","/back-end/login").permitAll()
                
                //課程管理
                .requestMatchers("/admin/adminPayout","/admin/refund").hasAnyRole("super_admin","courses")
                
                //超級管理員
             // 改前
              //超級管理員
              .requestMatchers("/admin/select_page").hasRole("super_admin")

              // 改後
              // 超級管理員專區：管理員帳號 CRUD ＋ 權限 CRUD
              .requestMatchers(
                      // 管理員查詢頁（AdminIdController）
                      "/admin/select_page",
                      "/admin/getOne_For_Display",
                      // 管理員 CRUD（AdminController）
                      "/admin/addAdmin",
                      "/admin/insert",
                      "/admin/getOne_For_Update",
                      "/admin/update",
                      "/admin/delete",
                      "/admin/listAllAdmin",
                      "/admin/listAdmins_ByCompositeQuery",
                      "/admin/DBGifReader",
                      // 權限 CRUD（PermissionController）
                      "/admin/permissions/**"
              ).hasRole("super_admin")
                
                
                // 其他所有 /back-end/** 和 /admin/** 都需要「管理員」身分
                // （不能用 authenticated()：兩條 chain 共用 session，會員登入也算已認證）
                .anyRequest().hasRole("ADMIN")
            )

            .userDetailsService(adminUserDetailsService)

            .requestCache(cache -> cache.requestCache(requestCache))

            .formLogin(form -> form
                .loginPage("/back-end/login")
                .loginProcessingUrl("/back-end/login")
                .usernameParameter("adminAccount")
                .passwordParameter("adminPassword")
                .defaultSuccessUrl("/index_temp", false)
                .failureUrl("/back-end/login?error=true")
                .permitAll()
            )

            .logout(logout -> logout
                .logoutUrl("/back-end/logout")
                .logoutSuccessUrl("/back-end/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID","remember-me-admin")
                .permitAll()
            )

            .rememberMe(remember -> remember
                .key("adminSecretKeyUnique")
                .rememberMeCookieName("remember-me-admin")
                .tokenRepository(adminPersistentTokenRepository())
                .tokenValiditySeconds(604800)
                .userDetailsService(adminUserDetailsService)
                .rememberMeParameter("remember-me")
            )

            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    requestCache.saveRequest(request, response);
                    response.sendRedirect("/back-end/login");
                })
                // 已登入但不是管理員（例如會員）闖入後台 → 導向後台登入頁
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.sendRedirect("/back-end/login?unauthorized");
                })
            );

        return http.build();
    }

    @Bean
    public PersistentTokenRepository adminPersistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
//        tokenRepository.setCreateTableOnStartup(true);
        return tokenRepository;
    }

}
