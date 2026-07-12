package com.freemind.course.order.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.freemind.course.order.model.Payout;
import com.freemind.course.order.model.PayoutService;
import com.freemind.login.psychologist.entity.Psychologist;
import com.freemind.login.psychologist.service.PsychologistService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/psych/payout")

public class PayoutController {
	@Autowired
	private PayoutService payoutService;
	@Autowired
	private PsychologistService psychologistService;

	@PostMapping("set_psychId_session")
	public String setPsychIdSession(@RequestParam(name = "psychIdSession") Integer psychIdSession, ModelMap model,
			HttpSession session) {
		session.setAttribute("psychId", psychIdSession);

		return "redirect:/psych/payout/selectPayout";
	}

	@GetMapping("/selectPayout")
	public String psychSelectCourse(@SessionAttribute(name = "psychId", required = false) Integer psychId,
			ModelMap model) {
		if (psychId != null) {
			Psychologist psychologist = psychologistService.getOnePsychologist(psychId);

			model.addAttribute("psychologist", psychologist);
			
			 List<Payout> allPayout = payoutService.getByPsychId(psychId);
				model.addAttribute("allPayout", allPayout);
		}

		return "front-end/psych/course/Payout";
	}

	
	@PostMapping("/getAllPayout")
	public String getAllPayout(@SessionAttribute(name = "psychId", required = false) Integer psychId,
			ModelMap model) {
		
		return "front-end/psych/course/Payout";
	}
}
