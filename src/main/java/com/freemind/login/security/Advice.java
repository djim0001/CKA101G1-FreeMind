package com.freemind.login.security;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.freemind.login.psychologist.dto.PsychologistSelfRes;
import com.freemind.login.psychologist.service.PsychologistService;
import com.freemind.login.security.psychologistsecurity.PsychUserDetails;

import jakarta.servlet.http.HttpSession;
@ControllerAdvice
public class Advice {

	
	 private final PsychologistService psychologistService;

	    public Advice(PsychologistService psychologistService) {
	        this.psychologistService = psychologistService;
	    }



	@ModelAttribute("psych")
	public PsychologistSelfRes getPsych(Authentication auth, HttpSession session) {
		if (auth == null || !(auth.getPrincipal() instanceof PsychUserDetails ud)) {
			return null;
		}
		
		session.setAttribute("psychId", ud.getPsychologist().getPsychId());
		return psychologistService.getPsychSelf(ud.getPsychologist().getPsychId());
	}

}

