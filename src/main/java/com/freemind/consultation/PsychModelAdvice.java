package com.freemind.consultation;   

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.freemind.consultation.orders.controller.OrdersPsychController;
import com.freemind.consultation.slots.controller.SlotsPsychController;
import com.freemind.login.security.psychologistsecurity.PsychUserDetails;

@ControllerAdvice(assignableTypes = { OrdersPsychController.class, SlotsPsychController.class })
public class PsychModelAdvice {

	@ModelAttribute("psych")
	public Object currentPsych(@AuthenticationPrincipal PsychUserDetails principal) {
		return principal != null ? principal.getPsychologist() : null;
	}
}