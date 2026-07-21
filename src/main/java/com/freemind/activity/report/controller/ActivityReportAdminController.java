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

import com.freemind.activity.report.model.ActivityReport;
import com.freemind.activity.report.model.ActivityReportService;
import com.freemind.activity.util.PageUtils;
import com.freemind.login.security.adminsecurity.AdminUserDetails;

@Controller
@RequestMapping("/admin/activity/report") 
public class ActivityReportAdminController {

	@Autowired
	private ActivityReportService reportSvc;
	
	private static final int PAGE_SIZE = 3;

	// 回報列表
	@GetMapping("list")
	public String list(@RequestParam(value = "tab", defaultValue = "pending") String tab,
						@RequestParam(value = "page", defaultValue = "1") Integer page,
						@AuthenticationPrincipal AdminUserDetails userDetails,
						ModelMap model) {
		Integer status;
	    if ("processing".equals(tab)) {
	        status = 1;
	    } else if ("done".equals(tab)) {
	        status = 2;
	    } else {
	        tab = "pending";
	        status = 0;
	    }

	    List<ActivityReport> allList = reportSvc.getReportsByStatus(status);

		
		int totalPages = PageUtils.calculateTotalPages(allList.size(), PAGE_SIZE);
		Integer currentPage = page;
		if (currentPage < 1) {
			currentPage = 1;
		} else if (currentPage > totalPages) {
			currentPage = totalPages;
		}

		int fromIndex = (currentPage - 1) * PAGE_SIZE;
		int toIndex = Math.min(fromIndex + PAGE_SIZE, allList.size());
		List<ActivityReport> pageData = fromIndex < toIndex 
				? allList.subList(fromIndex, toIndex) 
				: new ArrayList<>();
		
		model.addAttribute("reportListData", pageData);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", totalPages);
		model.addAttribute("currentTab", tab);
		model.addAttribute("currentAdminId", userDetails.getAdmin().getAdminId());
		model.addAttribute("qs", "tab=" + tab);
		
		// 後台統計卡片數字
		model.addAttribute("countPending", reportSvc.countByStatus(0));
		model.addAttribute("countProcessing", reportSvc.countByStatus(1));
		model.addAttribute("countDone", reportSvc.countByStatus(2));
		
		return "back-end/activity/report/reportList";
	}

	// 後台人員受理問題
	@PostMapping("takeOver")
	public String takeOver(@RequestParam("reportId") Integer reportId,
							@RequestParam(value = "tab", required = false) String tab,
							@RequestParam(value = "page", required = false) Integer page,
							@AuthenticationPrincipal AdminUserDetails userDetails,  
	                       RedirectAttributes redirectAttributes) {
		try {
			reportSvc.takeOverReport(reportId, userDetails.getAdmin());  
			redirectAttributes.addFlashAttribute("successMessage", "已受理此問題回報");
		} catch (IllegalArgumentException | IllegalStateException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		String t = (tab != null) ? tab : "pending";
		int p = (page != null) ? page : 1;
		return "redirect:/admin/activity/report/list?tab=" + t + "&page=" + p;
	}

	// 後台人員回覆問題
	@PostMapping("reply")
	public String reply(@RequestParam("reportId") Integer reportId,
	                    @RequestParam("replyContent") String replyContent,
	                    @RequestParam(value = "tab", required = false) String tab,
	                    @RequestParam(value = "page", required = false) Integer page,
	                    @AuthenticationPrincipal AdminUserDetails userDetails,
	                    RedirectAttributes redirectAttributes) {
		try {
			reportSvc.replyReport(reportId, userDetails.getAdmin(), replyContent);
			redirectAttributes.addFlashAttribute("successMessage", "已完成回覆");
		} catch (IllegalArgumentException | IllegalStateException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		String t = (tab != null) ? tab : "pending";
		int p = (page != null) ? page : 1;
		return "redirect:/admin/activity/report/list?tab=" + t + "&page=" + p;
	}
}
