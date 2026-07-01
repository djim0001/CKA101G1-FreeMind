package com.freemind.login.admin.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.freemind.login.admin.model.Admin;
import com.freemind.login.admin.model.AdminService;

@Controller
@RequestMapping("/admin")
public class AdminController {

	@Autowired
	private AdminService adminSvc;

	/*
	 * 轉交至 addAdmin.html
	 */
	@GetMapping("addAdmin")
	public String addAdmin(ModelMap model) {
		Admin adminVO = new Admin();
		adminVO.setHiredate(LocalDateTime.now()); // 預設當前時間
		model.addAttribute("adminVO", adminVO);
		return "back-end/login/admin/addAdmin";
	}

	/*
	 * 新增管理員的表單提交
	 * 
	 * @Valid : 對前端傳過來的管理員資料進行校驗
	 * BindingResult result : 要在 @Valid 參數後面。如果 @Valid 發現有資料格式不對，會把錯誤紀錄存在這裡，程式不會直接崩潰。
	 * @RequestParam("profilePic") : 接收前端上傳的檔案（圖片）。可能有多個或單個檔案，所以用 MultipartFile []。
	 * throws IOException : 讀取檔案可能會有輸入輸出異常，所以拋出例外
	 * 
	 */
	@PostMapping("insert")
	public String insert(@Valid Admin adminVO, BindingResult result, ModelMap model,
			@RequestParam("profilePic") MultipartFile[] parts) throws IOException {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		// 清除 BindingResult 中關於 profilePic 欄位的 FieldError 紀錄
		result = removeFieldError(adminVO, result, "profilePic");

		if (parts[0].isEmpty()) { // 使用者未選擇要上傳的圖片時
			model.addAttribute("errorMessage", "管理員照片: 請上傳照片");
		} else {
			for (MultipartFile multipartFile : parts) {
				byte[] buf = multipartFile.getBytes();
				adminVO.setProfilePic(buf);
			}
		}
		
		//result.hasErrors() : 如果有錯誤或是沒上傳照片，就直接 return 原本的新增頁面，Thymeleaf 會自動把錯誤訊息渲染在對應欄位旁。

		if (result.hasErrors() || parts[0].isEmpty()) {
			return "back-end/login/admin/addAdmin"; // 驗證失敗，回新增頁面
		}

		/*************************** 2.開始新增資料 *****************************************/
		adminSvc.addAdmin(adminVO);
		//呼叫 Service 層將資料寫入資料庫。

		/*************************** 3.新增完成,準備轉交(Send the Success view) **************/
		return "redirect:/admin/listAllAdmin"; // 新增成功後重導向至管理員列表頁
		//防止重新整理（F5）導致重複新增資料，所以用redirect
	}

	/*
	 * 點擊修改按鈕時，查出單筆資料並轉交至修改頁面 (update_admin_input.html)
	 * 
	 * @RequestParam("adminId") String adminId：在管理員清單列表中，點擊某個管理員的「修改」按鈕時，會把該名管理員的 ID 傳過來。
	 */
	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("adminId") String adminId, ModelMap model) {
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		/*************************** 2.開始查詢資料 *****************************************/
		Admin adminVO = adminSvc.getOneAdmin(Integer.valueOf(adminId));

		/*************************** 3.查詢完成,準備轉交(Send the Success view) **************/
		model.addAttribute("adminVO", adminVO);
		//model.addAttribute("adminVO", adminVO) : 把查出來的舊資料放入 model 裡。
		return "back-end/login/admin/update_admin_input"; 
	}

	
	/*
	 * This method will be called on update_emp_input.html form submission, handling POST request It also validates the user input
	 *  
	 *
	 */
	@PostMapping("update")
	public String update(@Valid Admin adminVO, BindingResult result, ModelMap model,
			@RequestParam("profilePic") MultipartFile[] parts) throws IOException {

		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		// 清除 BindingResult 中關於 profilePic 欄位的 FieldError 紀錄
		result = removeFieldError(adminVO, result, "profilePic");

		if (parts[0].isEmpty()) { // 使用者在修改時「沒有重新選擇新圖片」
			// 自資料庫取出該管理員原本的舊圖片，重新塞回，防止被 null 覆蓋
			byte[] profilePic = adminSvc.getOneAdmin(adminVO.getAdminId()).getProfilePic();
			adminVO.setProfilePic(profilePic);
		} else {
			for (MultipartFile multipartFile : parts) {
				byte[] profilePic = multipartFile.getBytes();
				adminVO.setProfilePic(profilePic);
			}
		}

		if (result.hasErrors()) {
			return "back-end/login/admin/update_admin_input"; // 驗證失敗，回原修改頁面
		}

		/*************************** 2.開始修改資料 *****************************************/
		adminSvc.updateAdmin(adminVO);

		/*************************** 3.修改完成,準備轉交(Send the Success view) **************/
		model.addAttribute("success", "- (修改成功)");
		adminVO = adminSvc.getOneAdmin(Integer.valueOf(adminVO.getAdminId()));
		model.addAttribute("adminVO", adminVO);
		return "back-end/login/admin/listOneAdmin"; // 修改成功後轉交展示單筆資料的頁面
	}

	/*
	 * 處理刪除管理員請求
	 */
	@PostMapping("delete")
	public String delete(@RequestParam("adminId") String adminId) {
		/*************************** 1.接收請求參數 - 輸入格式的錯誤處理 ************************/
		/*************************** 2.開始刪除資料 *****************************************/
		adminSvc.deleteAdmin(Integer.valueOf(adminId));

		/*************************** 3.刪除完成,準備轉交(Send the Success view) **************/
		return "redirect:/admin/listAllAdmin"; // 刪除完成後重導向至管理員列表頁
	}

	/*
	 * 顯示管理員列表頁 (GET) - List all Admins.
	 */
	@GetMapping("listAllAdmin")
	public String listAllAdminPage(ModelMap model) {
		List<Admin> list = adminSvc.getAll();
		model.addAttribute("adminListData", list);
		return "back-end/login/admin/listAllAdmin";
	}

	/*
	 * 【下拉選單 / 共用資料準備】
	 * 提供帳號狀態的對應資料給前端（0代表未啟用，1代表啟用，2代表停權）
	 * 
	 * @ModelAttribute() : 這個 Controller 裡面的任何一個方法在返回網頁時，Spring 都會自動執行這個方法。
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
	 * 萬用複合查詢 - 接收前端所有篩選條件並進行動態查詢
	 */
	@PostMapping("listAdmins_ByCompositeQuery")
	public String listAllAdmin(HttpServletRequest req, Model model) {
		Map<String, String[]> map = req.getParameterMap();
		List<Admin> list = adminSvc.getAll(map);
		model.addAttribute("adminListData", list); 
		return "back-end/login/admin/listAllAdmin";
	}

	/*
	 * 輔助方法：手動去除 BindingResult 中特定欄位 (此處為 profilePic) 的 FieldError 紀錄
	 */
	public BindingResult removeFieldError(Admin adminVO, BindingResult result, String removedFieldname) {
		List<FieldError> errorsListToKeep = result.getFieldErrors().stream()
				.filter(fieldError -> !fieldError.getField().equals(removedFieldname))
				.collect(Collectors.toList());
		
		result = new BeanPropertyBindingResult(adminVO, "adminVO");
		
		for (FieldError fieldError : errorsListToKeep) {
			result.addError(fieldError);
		}
		return result;
	}

}