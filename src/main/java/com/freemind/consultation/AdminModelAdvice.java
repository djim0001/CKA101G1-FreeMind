package com.freemind.consultation;


import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.freemind.login.security.adminsecurity.AdminUserDetails;

@ControllerAdvice(basePackages = "com.freemind.consultation")
public class AdminModelAdvice {

	@ModelAttribute("admin")
	public Object admin(@AuthenticationPrincipal AdminUserDetails prinAdmin) {
		return prinAdmin != null ? prinAdmin.getAdmin() : null;
	}
}