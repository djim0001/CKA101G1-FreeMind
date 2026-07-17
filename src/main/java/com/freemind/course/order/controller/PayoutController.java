package com.freemind.course.order.controller;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.freemind.course.order.model.OrderDetailService;
import com.freemind.course.order.model.PayoutService;
import com.freemind.login.psychologist.entity.Psychologist;
import com.freemind.login.psychologist.service.PsychologistService;

@Controller
@RequestMapping("/psych/payout")

public class PayoutController {
	
	@Autowired
	private PsychologistService psychologistService;
	@Autowired
	private OrderDetailService orderDetailSvc;
	@Autowired
	private PayoutService payoutService;
	
	

//	@PostMapping("set_psychId_session")
//	public String setPsychIdSession(@RequestParam(name = "psychIdSession") Integer psychIdSession, ModelMap model,
//			HttpSession session) {
//		session.setAttribute("psychId", psychIdSession);
//
//		return "redirect:/psych/payout/selectPayout";
//	}

	@GetMapping("/selectPayout")
	public String psychSelectCourse(@SessionAttribute(name = "psychId", required = false) Integer psychId,
			ModelMap model) {
		if (psychId == null)
			return "/psych/psychologistLogin";
		Psychologist psychologist = psychologistService.getOnePsychologist(psychId);
		model.addAttribute("psychologist", psychologist);
		
		long psyMonthlySales = orderDetailSvc.getMonthlySales(psychId, YearMonth.now());
		int orderCountNow = orderDetailSvc.getMonthlyOrderCount(
		        psychId,
		        YearMonth.now()
		);
		List<Integer> monthlyOrderCounts = new ArrayList<>();
		List<String> monthlyLabels = new ArrayList<>();

		YearMonth currentMonth = YearMonth.now();

		DateTimeFormatter formatter =
		        DateTimeFormatter.ofPattern("yyyy年MM月");

		for (int i = 0; i < 5; i++) {

		    YearMonth targetMonth = currentMonth.minusMonths(i);

		    int orderCount = orderDetailSvc.getMonthlyOrderCount(
		            psychId,
		            targetMonth
		    );

		    monthlyOrderCounts.add(orderCount);
		    monthlyLabels.add(targetMonth.format(formatter));
		}

		model.addAttribute("monthlyOrderCounts", monthlyOrderCounts);
		model.addAttribute("monthlyLabels", monthlyLabels);
		
		
		model.addAttribute("psyMonthlySales", psyMonthlySales);
		model.addAttribute("orderCountNow", orderCountNow);
//		model.addAttribute("pendingPayoutCount", pendingPayoutCount);

		return "front-end/psych/course/Payout";
	}

	@PostMapping("/getAllPayout")
	public String getAllPayout(@SessionAttribute(name = "psychId", required = false) Integer psychId, ModelMap model) {

		return "front-end/psych/course/Payout";
	}
}
