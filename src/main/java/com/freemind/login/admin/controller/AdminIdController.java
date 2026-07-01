package com.freemind.login.admin.controller;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import com.freemind.login.admin.model.Admin;
import com.freemind.login.admin.model.AdminService;

@Controller
@RequestMapping("/admin")
public class AdminIdController {
	
	@Autowired
	private AdminService adminSvc;

	/*
	 * 【下拉選單 / 共用資料準備】
	 * 提供帳號狀態的對應資料給前端（0代表未啟用，1代表啟用，2代表停權），select_page.html 顯示查詢結果用
	 * 
	 * LinkedHashMap：與一般的 HashMap 不同，它會保證塞入的順序。前端 Thymeleaf 讀取時，
	 * 下拉選單一定會按照 0(未啟用)1(啟用)2(停權) 的順序排列。
	 * 
	 * @ModelAttribute() : 這個 Controller 裡面，在返回網頁時，Spring 都會自動執行這個方法。
	 * 
	 */
	@ModelAttribute("accountStatusMap")
	protected Map<Integer, String> referenceMapData() {
		Map<Integer, String> map = new LinkedHashMap<Integer, String>();
		map.put(0, "未啟用");
		map.put(1, "啟用");
		map.put(2, "停權");
		return map;
	}

	/*
	 * 顯示查詢頁 (GET) - 進入 select_page.html
	 */
	@GetMapping("select_page")
	public String select_page(ModelMap model) {
		List<Admin> list = adminSvc.getAll();
		model.addAttribute("adminListData", list); // for select_page.html 用
		return "back-end/login/admin/select_page";
	}

	/*
	 * 當在 select_page.html 送出管理員編號查詢時，由此方法處理 POST 請求
	 * 並且利用 Jakarta Validation 註解直接在參數上進行格式驗證
	 */
	@PostMapping("getOne_For_Display")
	public String getOne_For_Display(
		/***************************1.接收請求參數 - 輸入格式的錯誤處理*************************/
		@NotEmpty(message="管理員編號: 請勿空白")
		@Digits(integer = 4, fraction = 0, message = "管理員編號: 請填數字-請勿超過{integer}位數")
		@Min(value = 1, message = "管理員編號: 不能小於{value}")
		@Max(value = 9999, message = "管理員編號: 不能超過{value}")
		@RequestParam("adminId") String adminId,
		ModelMap model) {
		
		/***************************2.開始查詢資料*********************************************/
		Admin adminVO = adminSvc.getOneAdmin(Integer.valueOf(adminId));
		
		// 為了讓 select_page.html 萬一要重新顯示下拉選單或列表時不報錯，先將所有管理員資料備妥塞回 model
		List<Admin> list = adminSvc.getAll();
		model.addAttribute("adminListData", list);     // for select_page.html 用
		
		if (adminVO == null) {
			model.addAttribute("errorMessage", "查無資料");
			return "back-end/login/admin/select_page";
		}
		
		/***************************3.查詢完成,準備轉交(Send the Success view) *****************/
		model.addAttribute("adminVO", adminVO); // 查到的單筆管理員資料
		
		// 查詢完成後轉交 select_page.html，由其動態包含（insert）展示區塊
		return "back-end/login/admin/select_page"; 
	}

	/**
	 * 異常處理器: 針對「方法級別驗證（Method-level validation）」報錯時的統一處理
	 * 適用於 Spring 6.1 / Spring Boot 3.2+ 版本（無須在類別上方加上 @Validated）
	 */
	@ExceptionHandler(value = { HandlerMethodValidationException.class, ConstraintViolationException.class })
	public ModelAndView handleError(Exception e, Model model) {
	    
		StringBuilder strBuilder = new StringBuilder();
	        
		// 處理新版 Spring 拋出的 HandlerMethodValidationException
		if (e instanceof HandlerMethodValidationException ex) {
			ex.getParameterValidationResults().forEach(result -> {
				result.getResolvableErrors().forEach(error ->
					strBuilder.append(error.getDefaultMessage()).append("<br>")
				);
			});
		} 	 
		// 處理舊版或特定情況下拋出的 ConstraintViolationException
		else if (e instanceof ConstraintViolationException ex) {
			ex.getConstraintViolations().forEach(violation -> 
				strBuilder.append(violation.getMessage()).append("<br>")
			);
		}
	    
		// 當驗證失敗返回首頁（select_page.html）時，必須重新補上頁面所需的下拉選單/列表資料，否則會報 Thymeleaf 渲染錯誤
		List<Admin> list = adminSvc.getAll();
		model.addAttribute("adminListData", list); // for select_page.html 用

		return new ModelAndView("back-end/login/admin/select_page", "errorMessage", "請修正以下錯誤:<br>" + strBuilder.toString());
	}
	
}