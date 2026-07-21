package com.freemind.login.member.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
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

import com.freemind.login.admin.model.Admin;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;
import com.freemind.login.security.adminsecurity.AdminUserDetails;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

/**
 * 後台「會員查詢」——唯讀，不提供新增／修改／刪除。
 *
 * 只呼叫 MemberService 的查詢方法（getAll / getOneMember），
 * 完全不碰 addMember / updateMember / deleteMember。
 * 下面唯一的 POST（getOne_For_Display）是查詢表單，不會異動任何資料，
 * 寫成 POST 是為了與 AdminIdController 的既有查詢頁寫法一致。
 *
 * 權限：AdminSecurityConfig 已將 /admin/member/** 限定 super_admin
 *      （會員資料含手機、生日、地址等個資）。
 */
@Controller
@RequestMapping("/admin/member")
public class AdminMemberController {

	@Autowired
	private MemberService memberSvc;

	/*
	 * 供 adminHeader fragment 顯示登入中的管理員（帳號／姓名）。
	 * 直接取登入時載入的 principal，不需再查 DB；非管理員回 null。
	 */
	@ModelAttribute("admin")
	public Admin currentAdmin(Authentication authentication) {
		if (authentication == null
				|| !(authentication.getPrincipal() instanceof AdminUserDetails ud)) {
			return null;
		}
		return ud.getAdmin();
	}

	/*
	 * 帳號狀態對應（0未啟用 1啟用 2停權），select_page.html 顯示查詢結果用。
	 * LinkedHashMap 保證塞入順序，前端讀取時才會照 0→1→2 排列。
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
	 * 查全部 (GET) - 列出所有會員
	 */
	@GetMapping("listAllMember")
	public String listAllMember(ModelMap model) {
		model.addAttribute("memberListData", memberSvc.getAll());
		return "back-end/login/member/listAllMember";
	}

	/*
	 * 顯示查詢頁 (GET) - 進入 select_page.html
	 */
	@GetMapping("select_page")
	public String select_page() {
		return "back-end/login/member/select_page";
	}

	/*
	 * 當在 select_page.html 送出會員編號查詢時，由此方法處理 POST 請求，
	 * 並利用 Jakarta Validation 註解直接在參數上進行格式驗證。
	 *
	 * 注意：這是「查詢」而非異動，不會寫入任何資料。
	 */
	@PostMapping("getOne_For_Display")
	public String getOne_For_Display(
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		@NotEmpty(message = "會員編號: 請勿空白")
		@Digits(integer = 6, fraction = 0, message = "會員編號: 請填數字-請勿超過{integer}位數")
		@Min(value = 1, message = "會員編號: 不能小於{value}")
		@Max(value = 999999, message = "會員編號: 不能超過{value}")
		@RequestParam("memberId") String memberId,
		ModelMap model) {

		/*************************** 2.開始查詢資料 *********************************************/
		Member memberVO = memberSvc.getOneMember(Integer.valueOf(memberId));

		if (memberVO == null) {
			model.addAttribute("errorMessage", "查無資料");
			return "back-end/login/member/select_page";
		}

		/*************************** 3.查詢完成,準備轉交 ****************************************/
		model.addAttribute("memberVO", memberVO); // 查到的單筆會員資料
		return "back-end/login/member/select_page";
	}

	/**
	 * 異常處理器: 針對「方法級別驗證（Method-level validation）」報錯時的統一處理。
	 * 適用於 Spring 6.1 / Spring Boot 3.2+ （本專案 4.0.6，無須在類別上方加 @Validated）。
	 *
	 * 兩種例外都要攔：新版拋 HandlerMethodValidationException，
	 * 舊版或特定情況拋 ConstraintViolationException，少攔一個就會漏成 500。
	 */
	@ExceptionHandler(value = { HandlerMethodValidationException.class, ConstraintViolationException.class })
	public ModelAndView handleError(Exception e) {

		StringBuilder strBuilder = new StringBuilder();

		if (e instanceof HandlerMethodValidationException ex) {
			ex.getParameterValidationResults().forEach(result -> {
				result.getResolvableErrors().forEach(error ->
					strBuilder.append(error.getDefaultMessage()).append("<br>")
				);
			});
		} else if (e instanceof ConstraintViolationException ex) {
			ex.getConstraintViolations().forEach(violation ->
				strBuilder.append(violation.getMessage()).append("<br>")
			);
		}

		return new ModelAndView("back-end/login/member/select_page",
				"errorMessage", "請修正以下錯誤:<br>" + strBuilder.toString());
	}

}
