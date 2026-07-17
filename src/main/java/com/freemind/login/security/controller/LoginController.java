package com.freemind.login.security.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    /** GIS 登入按鈕需要的公開 client-id（來自 application.properties 的 google.client-id） */
    @Value("${google.client-id}")
    private String googleClientId;

    /**
     * 管理員登入頁面
     */
    @GetMapping("/back-end/login")
    public String adminLogin() {
        return "back-end/login";
    }

    /**
     * 會員登入頁面
     */
    @GetMapping("/front-end/login")
    public String memberLogin(Model model) {
        model.addAttribute("googleClientId", googleClientId);
        return "front-end/login";
    }

    /**
     * 心理師登入頁面
     */
    @GetMapping("/psych/psychologistLogin")
    public String psychologistLogin() {
        return "front-end/psych/psychologistLogin";
    }
}