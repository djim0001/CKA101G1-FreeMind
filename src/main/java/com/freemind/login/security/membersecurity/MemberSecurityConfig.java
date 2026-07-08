package com.freemind.login.security.membersecurity;

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
        RequestCache requestCache = new HttpSessionRequestCache();

        http
//            .securityMatcher("/","/front-end/**", "/member/**", "/course/**")
            .securityMatcher("/","/front-end/**","/member/**","/course/member/**")

            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/","/front-end/login").permitAll()
//                .requestMatchers("/front-end/**").authenticated()
//                .requestMatchers("/member/**").authenticated()
//                .requestMatchers("/course/").permitAll()
                .anyRequest().authenticated()
            )

            .userDetailsService(memberUserDetailsService)

            .requestCache(cache -> cache.requestCache(requestCache))

            .formLogin(form -> form
                .loginPage("/front-end/login")
                .loginProcessingUrl("/front-end/login")
                .usernameParameter("memberAccount")
                .passwordParameter("memberPassword")
                .defaultSuccessUrl("/", false)
                .failureUrl("/front-end/login?error=true")
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
                    requestCache.saveRequest(request, response);
                    response.sendRedirect("/front-end/login");
                })
            );

   

        return http.build();
    }

    @Bean
    public PersistentTokenRepository memberPersistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepository = new JdbcTokenRepositoryImpl();
        tokenRepository.setDataSource(dataSource);
//        tokenRepository.setCreateTableOnStartup(true);
        return tokenRepository;
    }
}
