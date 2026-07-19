package com.freemind.course.order.controller;

import java.time.YearMonth;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.freemind.course.order.model.OrderDetailService;
import com.freemind.course.order.model.Payout;
import com.freemind.course.order.model.PayoutService;
import com.freemind.login.admin.model.Admin;
import com.freemind.login.admin.model.AdminService;
import com.freemind.login.psychologist.entity.Psychologist;

@Controller
@RequestMapping("/admin/adminPayout")
public class AdminPayoutController {

    @Autowired
    private PayoutService payoutService;
	@Autowired
	private OrderDetailService orderDetailService;
	@Autowired
	private  AdminService adminSvc;
	
	@ModelAttribute("admin")
	public Admin currentAdmin(Authentication authentication) {
		return adminSvc.findByAccount(authentication.getName());
	}

    // 顯示全部撥款資料
    @GetMapping("/listAll")
    public String listAllPayout(
            @RequestParam(value = "psychId", required = false) Integer psychId,
            ModelMap model) {

        List<Payout> allPayout;

        if (psychId == null) {
            allPayout = payoutService.getAll();
        } else {
            allPayout = payoutService.getByPsychId(psychId);
        }
//        List<Payout> allPayout = payoutService.getByPsychId(psychId);
//		model.addAttribute("allPayout", allPayout);

		List<Psychologist> topPsychologistsFive = orderDetailService.getTopPsychologistsByRevenue(5);
		for (int i = 0; i < topPsychologistsFive.size(); i++) {
			Psychologist psych = topPsychologistsFive.get(i);
			long totalSales = orderDetailService.getMonthlySales(psych.getPsychId(), YearMonth.now());
			model.addAttribute("psychologistNo" + (i + 1), psych);
			model.addAttribute("totalSales" + (i + 1), totalSales);
		}

		int UnpaidPayouts = payoutService.countUnpaidPayouts();
		int allDetailCount = orderDetailService.countAllOrderDetails();

		model.addAttribute("UnpaidPayouts", UnpaidPayouts);
		model.addAttribute("allDetailCount", allDetailCount);
        model.addAttribute("allPayout", allPayout);
        model.addAttribute("psychId", psychId);

        return "back-end/course/course/Payout";
    }

}