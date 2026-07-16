package com.freemind.activity.report.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.activity.report.model.ActivityReportService;
import com.freemind.login.security.adminsecurity.AdminUserDetails;

@Controller
@RequestMapping("/admin/activity/report") 
public class ActivityReportAdminController {

	@Autowired
	private ActivityReportService reportSvc;

	// 回報列表(先做「全部」,狀態篩選之後再加)
	@GetMapping("list")
	public String list(@AuthenticationPrincipal AdminUserDetails userDetails,
						ModelMap model) {
		model.addAttribute("reportListData", reportSvc.getAllReports());
		model.addAttribute("currentAdminId", userDetails.getAdmin().getAdminId());
		return "back-end/activity/report/reportList";
	}

	// 後台人員受理問題
	@PostMapping("takeOver")
	public String takeOver(@RequestParam("reportId") Integer reportId,
	                       @AuthenticationPrincipal AdminUserDetails userDetails,  
	                       RedirectAttributes redirectAttributes) {
		try {
			reportSvc.takeOverReport(reportId, userDetails.getAdmin());  
			redirectAttributes.addFlashAttribute("successMessage", "已受理此問題回報");
		} catch (IllegalArgumentException | IllegalStateException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/admin/activity/report/list";
	}

	// 後台人員回覆問題
	@PostMapping("reply")
	public String reply(@RequestParam("reportId") Integer reportId,
	                    @RequestParam("replyContent") String replyContent,
	                    @AuthenticationPrincipal AdminUserDetails userDetails,
	                    RedirectAttributes redirectAttributes) {
		try {
			reportSvc.replyReport(reportId, userDetails.getAdmin(), replyContent);
			redirectAttributes.addFlashAttribute("successMessage", "已完成回覆");
		} catch (IllegalArgumentException | IllegalStateException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/admin/activity/report/list";
	}
}
