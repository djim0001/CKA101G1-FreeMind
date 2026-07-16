package com.freemind.consultation.reports.controller;

import java.beans.PropertyEditorSupport;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.freemind.consultation.orders.model.Orders;
import com.freemind.consultation.orders.model.OrdersService;
import com.freemind.consultation.reports.model.Reports;
import com.freemind.consultation.reports.model.ReportsService;
import com.freemind.login.member.model.Member;
import com.freemind.login.security.membersecurity.MemberUserDetails;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/member/reports")
public class ReportsMemberController {

	@Autowired
	private ReportsService reportsSvc;

	@Autowired
	private OrdersService ordersSvc;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Member.class, "member", new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				if (text == null || text.isBlank()) {
					setValue(null);
				} else {
					Member member = new Member();
					member.setMemberId(Integer.valueOf(text));
					setValue(member);
				}
			}
		});
	}

	@GetMapping("home")
	public String reportsHome(ModelMap model) {
		return "front-end/member/consultation/reports/consultationFeedbackHome";
	}

	@PostMapping("frontInsert")
	public String frontInsert(@Valid Reports reports, BindingResult result, ModelMap model) {
		if (result.hasErrors()) {
			return "front-end/member/consultation/reports/addReports";
		}
		reportsSvc.addReports(reports);
		model.addAttribute("success", "問題回報已送出，我們會盡快處理！");
		return "front-end/member/consultation/reports/reportSuccess";
	}

	@GetMapping("myReportsForm")
	public String myReportsForm(@AuthenticationPrincipal MemberUserDetails prinUserDetails, ModelMap model) {
		if (prinUserDetails == null) {
			return "redirect:/front-end/login";
		}
		Integer memberId = prinUserDetails.getMember().getMemberId();
		List<Reports> list = reportsSvc.getByMemberId(memberId);
		model.addAttribute("reportsListData", list);
		model.addAttribute("memberId", memberId);
		return "front-end/member/consultation/reports/myReportsForm";
	}



	@GetMapping("reportLookupForm")
	public String reportLookupForm(@AuthenticationPrincipal MemberUserDetails prinUserDetails, ModelMap model) {
		if (prinUserDetails == null) {
			return "redirect:/front-end/login";
		}
		Integer mid = prinUserDetails.getMember().getMemberId();
		List<Orders> allReportableOrders = ordersSvc.getConfirmedOrCompletedByMemberId(mid);
		List<Reports> myReports = reportsSvc.getByMemberId(mid);
		java.util.Set<Integer> reportedOrderIds = myReports.stream().filter(r -> r.getOrders() != null)
				.map(r -> r.getOrders().getOrderId()).collect(java.util.stream.Collectors.toSet());
		List<Orders> list = allReportableOrders.stream().filter(o -> !reportedOrderIds.contains(o.getOrderId()))
				.collect(java.util.stream.Collectors.toList());
		model.addAttribute("ordersListData", list);
		model.addAttribute("memberId", mid);
		return "front-end/member/consultation/reports/reportLookupForm";
	}

	@PostMapping("reportSelect")
	public String reportSelect(@RequestParam("orderId") String orderId,
			@AuthenticationPrincipal MemberUserDetails prinUserDetails, ModelMap model) {
		if (prinUserDetails == null) {
			return "redirect:/front-end/login";
		}
		Integer memberId = prinUserDetails.getMember().getMemberId();
		Reports reports = new Reports();
		Member member = new Member();
		member.setMemberId(memberId);
		reports.setMember(member);
		Orders orders = ordersSvc.getOneOrders(Integer.valueOf(orderId));
		reports.setOrders(orders);
		model.addAttribute("reports", reports);
		return "front-end/member/consultation/reports/addReports";
	}
}