package com.freemind.activity.report.controller;

import java.util.ArrayList;
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
import com.freemind.activity.util.PageUtils;
import com.freemind.login.member.model.Member;
import com.freemind.login.security.membersecurity.MemberUserDetails;

@Controller
@RequestMapping("/member/activity/report")
public class ActivityReportController {

	@Autowired
	private ActivityReportService reportSvc;

	@Autowired
	private ActivityService activitySvc;
	
	private static final int PAGE_SIZE = 3;

	// 我的回報紀錄
	@GetMapping("myReports")
	public String myReports(@RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
							@AuthenticationPrincipal MemberUserDetails userDetails,
	                        ModelMap model) {
	    if (userDetails == null) {
	        return "redirect:/front-end/login";
	    }
		
		Member member = userDetails.getMember();
		List<ActivityReport> list = reportSvc.getMyReports(member);
		
		int totalPages = PageUtils.calculateTotalPages(list.size(), PAGE_SIZE);
	    if (currentPage < 1) {
	        currentPage = 1;
	    } else if (currentPage > totalPages) {
	        currentPage = totalPages;
	    }

	    int fromIndex = (currentPage - 1) * PAGE_SIZE;
	    int toIndex = Math.min(fromIndex + PAGE_SIZE, list.size());
	    List<ActivityReport> pageData = fromIndex < toIndex 
	            ? list.subList(fromIndex, toIndex) 
	            : new ArrayList<>();
		
	    model.addAttribute("reportListData", pageData);
	    model.addAttribute("currentPage", currentPage);
	    model.addAttribute("totalPages", totalPages);
		return "front-end/member/activity/report/myReports";
	}

	// 送出問題回報
	@PostMapping("submit")
	public String submit(@RequestParam("activityId") Integer activityId,
	                     @RequestParam("reportContent") String reportContent,
	                     @AuthenticationPrincipal MemberUserDetails userDetails,
	                     RedirectAttributes redirectAttributes) {
	    if (userDetails == null) {
	        redirectAttributes.addFlashAttribute("errorMessage", "請先登入");
	        return "redirect:/front-end/login";
	    }
		
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