package com.freemind.consultation.reports.controller;

import java.beans.PropertyEditorSupport;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.freemind.consultation.orders.model.OrdersService;
import com.freemind.consultation.reports.model.Reports;
import com.freemind.consultation.reports.model.ReportsService;
import com.freemind.login.admin.model.Admin;
import com.freemind.login.member.model.Member;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/reports")
public class ReportsController {

	@Autowired
	private ReportsService reportsSvc;

	@Autowired
	private OrdersService ordersSvc;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Admin.class, "admin", new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				if (text == null || text.isBlank()) {
					setValue(null);
				} else {
					Admin admin = new Admin();
					admin.setAdminId(Integer.valueOf(text));
					setValue(admin);
				}
			}
		});
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

	@GetMapping("listAllReports")
	public String listAllReports(ModelMap model) {
		List<Reports> list = reportsSvc.getAll();
		model.addAttribute("reportsListData", list);
		return "back-end/consultation/reports/listAllReports";
	}

	@GetMapping("addReports")
	public String addReports(ModelMap model) {
		Reports reports = new Reports();
		model.addAttribute("reports", reports);
		model.addAttribute("ordersList", ordersSvc.getAll());
		return "back-end/consultation/reports/addReports";
	}

	@GetMapping("select_Page")
	public String select_Page(ModelMap model) {
		return "back-end/consultation/reports/select_Page";
	}

	@PostMapping("insert")
	public String insert(@Valid Reports reports, BindingResult result, ModelMap model) {
		if (result.hasErrors()) {
			model.addAttribute("ordersList", ordersSvc.getAll());
			return "back-end/consultation/reports/addReports";
		}
		reportsSvc.addReports(reports);
		List<Reports> list = reportsSvc.getAll();
		model.addAttribute("reportsListData", list);
		model.addAttribute("success", "- (新增成功)");
		return "redirect:/admin/reports/listAllReports";
	}

	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("reportId") String reportId, ModelMap model) {
		Reports reports = reportsSvc.getOneReports(Integer.valueOf(reportId));
		if (reports.getAdmin() == null) {
			reports.setAdmin(new Admin());
		}
		model.addAttribute("reports", reports);
		return "back-end/consultation/reports/update_reports_input";
	}

	@PostMapping("update")
	public String update(@Valid Reports reports, BindingResult result, ModelMap model) {
		if (result.hasErrors()) {
			model.addAttribute("ordersList", ordersSvc.getAll());
			return "back-end/consultation/reports/update_reports_input";
		}
		reportsSvc.updateReports(reports);
		model.addAttribute("success", "-(修改成功)");
		Reports updatedReports = reportsSvc.getOneReports(reports.getReportId());
		model.addAttribute("reports", updatedReports);
		return "back-end/consultation/reports/listOneReports";
	}

	@PostMapping("delete")
	public String delete(@RequestParam("reportId") String reportId, ModelMap model) {
		reportsSvc.deleteReports(Integer.valueOf(reportId));
		List<Reports> list = reportsSvc.getAll();
		model.addAttribute("reportsListData", list);
		model.addAttribute("success", "-(刪除成功)");
		return "back-end/consultation/reports/listAllReports";
	}

	@PostMapping("getOne_For_Display")
	public String getOne_For_Display(@RequestParam("reportId") String reportId, ModelMap model) {
		Reports reports = reportsSvc.getOneReports(Integer.valueOf(reportId));
		model.addAttribute("reports", reports);
		return "back-end/consultation/reports/select_Page";
	}

	@PostMapping("getByStatus")
	public String getByStatus(@RequestParam("reportStatus") String reportStatus, ModelMap model) {
		List<Reports> list = reportsSvc.getByReportsStatus(Integer.valueOf(reportStatus));
		model.addAttribute("reportsListData", list);
		return "back-end/consultation/reports/select_Page";
	}

	@PostMapping("getByMember")
	public String getByMember(@RequestParam("memberId") String memberId, ModelMap model) {
		List<Reports> list = reportsSvc.getByMemberId(Integer.valueOf(memberId));
		model.addAttribute("reportsListData", list);
		return "back-end/consultation/reports/select_Page";
	}

	@PostMapping("getOne_For_Reply")
	public String getOne_For_Reply(@RequestParam("reportId") String reportId, ModelMap model) {
		Reports reports = reportsSvc.getOneReports(Integer.valueOf(reportId));
		if (reports.getAdmin() == null) {
			reports.setAdmin(new Admin());
		}
		model.addAttribute("reports", reports);
		return "back-end/consultation/reports/reply_reports_input";
	}

	@PostMapping("reply")
	public String reply(@RequestParam("reportId") String reportId,
			@RequestParam(value = "adminId", required = false) String adminId,
			@RequestParam("reportStatus") String reportStatus,
			@RequestParam(value = "reportNote", required = false) String reportNote, ModelMap model) {
		Reports reports = reportsSvc.getOneReports(Integer.valueOf(reportId));
		if (adminId != null && !adminId.isBlank()) {
			Admin admin = new Admin();
			admin.setAdminId(Integer.valueOf(adminId));
			reports.setAdmin(admin);
		}
		reports.setReportStatus(Integer.valueOf(reportStatus));
		reports.setReportNote(reportNote);
		reportsSvc.updateReports(reports);
		model.addAttribute("success", "-(回覆成功)");
		Reports updatedReports = reportsSvc.getOneReports(reports.getReportId());
		model.addAttribute("reports", updatedReports);
		return "back-end/consultation/reports/listOneReports";
	}

	@PostMapping("search")
	public String search(@RequestParam(value = "reportId", required = false) String reportId,
			@RequestParam(value = "memberId", required = false) String memberId,
			@RequestParam(value = "reportStatus", required = false) String reportStatus, ModelMap model) {
		if (reportId != null && !reportId.isBlank()) {
			Reports reports = reportsSvc.getOneReports(Integer.valueOf(reportId));
			model.addAttribute("reports", reports);
			return "back-end/consultation/reports/select_Page";
		}
		Integer memberIdVal = (memberId != null && !memberId.isBlank()) ? Integer.valueOf(memberId) : null;
		Integer reportStatusVal = (reportStatus != null && !reportStatus.isBlank()) ? Integer.valueOf(reportStatus)
				: null;
		if (memberIdVal == null && reportStatusVal == null) {
			model.addAttribute("errorMessage", "請至少填寫一個查詢條件");
			return "back-end/consultation/reports/select_Page";
		}
		List<Reports> list = reportsSvc.search(memberIdVal, reportStatusVal);
		model.addAttribute("reportsListData", list);
		return "back-end/consultation/reports/select_Page";
	}
}