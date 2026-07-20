package com.freemind.course.order.controller;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.freemind.course.order.model.OrderDetailService;
import com.freemind.course.order.model.Payout;
import com.freemind.course.order.model.PayoutService;
import com.freemind.login.notice.service.NoticeService;
import com.freemind.login.psychologist.dto.PsychologistSelfRes;
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
	@Autowired
	private NoticeService noticeSvc;
	
	@ModelAttribute("countPsychUnread")
	public Long psychNotice(
			ModelMap model,
			@ModelAttribute("psych") PsychologistSelfRes psych) {
		return (psych != null ? noticeSvc.countPsychUnread(psych.getPsychId()) : null);
	}

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
		List<Payout> psychPayout = payoutService.getByPsychId(psychId);

		int orderCount = orderDetailSvc.getMonthlyOrderCount(psychId, YearMonth.now());

		List<Long> monthlySales = new ArrayList<>();
		List<String> monthlyLabels = new ArrayList<>();
		YearMonth currentMonth = YearMonth.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月");
		for (int i = 4; i >= 0; i--) {
			YearMonth targetMonth = currentMonth.minusMonths(i);
			long psyMonthlySales = orderDetailSvc.getMonthlySales(psychId, targetMonth);
			monthlySales.add(psyMonthlySales);
			monthlyLabels.add(targetMonth.format(formatter));
		}
		int countPendingPayouts = payoutService.countPendingPayouts(psychId);

		model.addAttribute("monthlySales", monthlySales);
		model.addAttribute("monthlyLabels", monthlyLabels);
		model.addAttribute("orderCount", orderCount);
		model.addAttribute("countPendingPayouts", countPendingPayouts);

		model.addAttribute("psychPayout", psychPayout);
		return "front-end/psych/course/Payout";
	}

	@PostMapping("/getAllPayout")
	public String getAllPayout(@SessionAttribute(name = "psychId", required = false) Integer psychId, ModelMap model) {

		return "front-end/psych/course/Payout";
	}
}
