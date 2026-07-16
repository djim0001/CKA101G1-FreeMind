package com.freemind.activity.report.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.activity.activity.model.Activity;
import com.freemind.activity.activity.model.ActivityService;
import com.freemind.activity.report.model.ActivityReport;
import com.freemind.activity.report.model.ActivityReportService;
import com.freemind.login.member.model.Member;
import com.freemind.login.security.membersecurity.MemberUserDetails;

@Controller
@RequestMapping("/member/activity/report")
public class ActivityReportController {

	@Autowired
	private ActivityReportService reportSvc;

	@Autowired
	private ActivityService activitySvc;

	// 我的回報紀錄
	@GetMapping("myReports")
	public String myReports(@AuthenticationPrincipal MemberUserDetails userDetails,
	                        ModelMap model) {
		Member member = userDetails.getMember();
		List<ActivityReport> list = reportSvc.getMyReports(member);
		model.addAttribute("reportListData", list);
		return "front-end/member/activity/report/myReports";
	}

	// 送出問題回報
	@PostMapping("submit")
	public String submit(@RequestParam("activityId") Integer activityId,
	                     @RequestParam("reportContent") String reportContent,
	                     @AuthenticationPrincipal MemberUserDetails userDetails,
	                     RedirectAttributes redirectAttributes) {
		Member member = userDetails.getMember();
		Activity activity = activitySvc.getOneActivity(activityId);
		
		if (activity == null) {
			redirectAttributes.addFlashAttribute("errorMessage", "活動不存在");
			return "redirect:/member/activity/report/myReports";
		}
		try {
			reportSvc.submitReport(member, activity, reportContent);
			redirectAttributes.addFlashAttribute("successMessage", "問題回報已送出");
		} catch (IllegalArgumentException | IllegalStateException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/member/activity/report/myReports";
	}
}