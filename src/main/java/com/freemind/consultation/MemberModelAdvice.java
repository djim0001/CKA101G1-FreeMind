package com.freemind.consultation;   

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.freemind.consultation.orders.controller.OrdersMemberController;
import com.freemind.consultation.reports.controller.ReportsMemberController;
import com.freemind.login.security.membersecurity.MemberUserDetails;   

@ControllerAdvice(assignableTypes = { OrdersMemberController.class, ReportsMemberController.class })
public class MemberModelAdvice {

	@ModelAttribute("member")
	public Object currentMember(@AuthenticationPrincipal MemberUserDetails principal) {
		return principal != null ? principal.getMember() : null;
	}
}