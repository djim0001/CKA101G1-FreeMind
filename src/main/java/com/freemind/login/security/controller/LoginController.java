package com.freemind.login.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

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
    public String memberLogin() {
        return "front-end/login";
    }

}
