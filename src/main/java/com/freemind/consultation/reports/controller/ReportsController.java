package com.freemind.consultation.reports.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.freemind.consultation.orders.model.OrdersService;
import com.freemind.consultation.reports.model.Reports;
import com.freemind.consultation.reports.model.ReportsService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/reports")
public class ReportsController {

	@Autowired
	private ReportsService reportsSvc;
	
	@Autowired
	private OrdersService ordersSvc;
	
	@GetMapping("listAllReports")
	public String listAllReports(ModelMap model) {
		List<Reports> list = reportsSvc.getAll();
		model.addAttribute("reportsListData", list);
		return "back-end/consultation/reports/listAllReports";
	}
	
	
	/*
	 *轉交至addReports.html
	 */
	@GetMapping("addReports")
	public String addReports(ModelMap model) {
		Reports reports = new Reports(); //建立空的Reports物件
		model.addAttribute("reports", reports); //傳給畫面
		
	    // 傳訂單清單給下拉選單
		model.addAttribute("ordersList", ordersSvc.getAll());
		
		return "back-end/consultation/reports/addReports"; //顯示新增表單
	}
	
	/*
	 *轉交至select_Page.html，顯示查詢頁面
	 */
	@GetMapping("select_Page")
	public String select_Page(ModelMap model) {
		return "back-end/consultation/reports/select_Page";
	}
	
	/*
	 *新增表單提交
	 */
	@PostMapping("insert")
	public String insert(@Valid Reports reports, BindingResult result, ModelMap model) {
		
	/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/	
		if(result.hasErrors()) {
			model.addAttribute("ordersList", ordersSvc.getAll());
			return "back-end/consultation/reports/addReports"; //如果驗證有錯誤，回到新增表單頁面重新填寫
		} 
	/*************************** 2.開始新增資料 *****************************************/		
	reportsSvc.addReports(reports); //呼叫 Service 新增資料到資料庫
	/*************************** 3.新增完成,準備轉交(Send the Success view) **************/
	List<Reports> list = reportsSvc.getAll();
	model.addAttribute("reportsListData", list);
	model.addAttribute("success", "- (新增成功)");
	return "redirect:/reports/listAllReports";
	}
	
	/*
	 *點擊修改按鈕時，查出單筆資料並轉交至修改頁面
	 */
	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("reportId") String reportId, ModelMap model) { //接收從畫面傳來的reportId，ModelMap傳資料給畫面用 
		
		//步驟二：查詢這筆回報資料，步驟一不用寫，因為只有單筆
		Reports reports = reportsSvc.getOneReports(Integer.valueOf(reportId));//轉型成Integer
		
		//步驟三：傳給修改表單畫面
		model.addAttribute("reports", reports);
		return "back-end/consultation/reports/update_reports_input";
	}
	
	/*
	 *修改表單提交
	 */
	@PostMapping("update")
	public String update(@Valid Reports reports, BindingResult result, ModelMap model) {
	
		// 步驟一：驗證格式有錯誤
		if(result.hasErrors()) {
			model.addAttribute("ordersList", ordersSvc.getAll());
			return "back-end/consultation/reports/update_reports_input"; //如果驗證有錯誤，回到修改表單頁面
		}
	
	// 步驟二：修改資料
	reportsSvc.updateReports(reports);//呼叫Service修改資料
	
	// 步驟三：修改完成，回到這筆資料的詳細頁
	model.addAttribute("success", "-(修改成功)");
	Reports updatedReports = reportsSvc.getOneReports(reports.getReportId());
	model.addAttribute("reports", updatedReports);
	return "back-end/consultation/reports/listOneReports";
	}
	
	/*
	 *點擊修改按鈕時，處理刪除的方法
	 */		
	@PostMapping("delete")
	public String delete(@RequestParam("reportId") String reportId, ModelMap model) {
		
		// 步驟二：刪除資料
		reportsSvc.deleteReports(Integer.valueOf(reportId));
		
		// 步驟三：刪除完成，顯示最新列表
		List<Reports> list = reportsSvc.getAll();
		model.addAttribute("reportsListData", list);
		model.addAttribute("success", "-(刪除成功)");
		return "back-end/consultation/reports/listAllReports";
	}
	
	// 依 reportId 單筆查詢
	@PostMapping("getOne_For_Display")
	public String getOne_For_Display(@RequestParam("reportId") String reportId, ModelMap model) {
		Reports reports = reportsSvc.getOneReports(Integer.valueOf(reportId));
		model.addAttribute("reports", reports);
		return "back-end/consultation/reports/select_Page";
	}
	
	// 依 reportStatus 查詢
	@PostMapping("getByStatus")
	public String getByStatus(@RequestParam("reportStatus") String reportStatus, ModelMap model) {
		List<Reports> list = reportsSvc.getByReportsStatus(Integer.valueOf(reportStatus));
		model.addAttribute("reportsListData", list);
		return "back-end/consultation/reports/select_Page";
	}
	
	// 依 memberId 查詢
	@PostMapping("getByMember")
	public String getByMember(@RequestParam("memberId") String memberId, ModelMap model) {
		List<Reports> list = reportsSvc.getByMemberId(Integer.valueOf(memberId));
		model.addAttribute("reportsListData", list);
		return "back-end/consultation/reports/select_Page";
	}
	
	
	
}
