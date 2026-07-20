package com.freemind.login.notice.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.freemind.login.admin.model.Admin;
import com.freemind.login.notice.entity.NoticeTemplate;
import com.freemind.login.notice.service.NoticeTemplateService;
import com.freemind.login.security.adminsecurity.AdminUserDetails;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/notice")
public class NoticeTemplateController {

	@Autowired
	private NoticeTemplateService noticeTemplateSvc;

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
	 * 顯示通知範本列表頁
	 */
	@GetMapping("listAllNoticeTemplate")
	public String listAllNoticeTemplate(ModelMap model) {
		List<NoticeTemplate> list = noticeTemplateSvc.getAll();
		model.addAttribute("noticeTemplateListData", list);
		return "back-end/login/notice/templates/listAllNoticeTemplate";
	}

	/*
	 * 轉交至 addNoticeTemplate.html
	 */
	@GetMapping("addNoticeTemplate")
	public String addNoticeTemplate(ModelMap model) {
		model.addAttribute("noticeTemplate", new NoticeTemplate());
		return "back-end/login/notice/templates/addNoticeTemplate";
	}

	/*
	 * 新增通知範本的表單提交
	 * admin_id 不開放表單輸入，由系統抓目前登入的管理員帶入
	 */
	@PostMapping("insert")
	public String insert(@Valid @ModelAttribute("noticeTemplate") NoticeTemplate noticeTemplate,
			BindingResult result, Authentication authentication) {

		if (result.hasErrors()) {
			return "back-end/login/notice/templates/addNoticeTemplate"; // 驗證失敗，回新增頁面
		}

		AdminUserDetails userDetails = (AdminUserDetails) authentication.getPrincipal();
		noticeTemplate.setAdminId(userDetails.getAdmin().getAdminId());

		noticeTemplateSvc.addNoticeTemplate(noticeTemplate);

		return "redirect:/admin/notice/listAllNoticeTemplate"; // 防止 F5 重複新增
	}

	/*
	 * 點擊修改按鈕時，查出單筆資料並轉交至修改頁面
	 */
	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("templateId") Integer templateId, ModelMap model) {
		NoticeTemplate noticeTemplate = noticeTemplateSvc.getOneNoticeTemplate(templateId);
		model.addAttribute("noticeTemplate", noticeTemplate);
		return "back-end/login/notice/templates/update_noticeTemplate_input";
	}

	/*
	 * 修改通知範本的表單提交
	 * admin_id 更新為本次操作的管理員
	 */
	@PostMapping("update")
	public String update(@Valid @ModelAttribute("noticeTemplate") NoticeTemplate noticeTemplate,
			BindingResult result, Authentication authentication) {

		if (result.hasErrors()) {
			return "back-end/login/notice/templates/update_noticeTemplate_input"; // 驗證失敗，回修改頁面
		}

		AdminUserDetails userDetails = (AdminUserDetails) authentication.getPrincipal();
		noticeTemplate.setAdminId(userDetails.getAdmin().getAdminId());

		noticeTemplateSvc.updateNoticeTemplate(noticeTemplate);

		return "redirect:/admin/notice/listAllNoticeTemplate";
	}

	/*
	 * 處理刪除通知範本請求
	 */
	@PostMapping("delete")
	public String delete(@RequestParam("templateId") Integer templateId) {
		noticeTemplateSvc.deleteNoticeTemplate(templateId);
		return "redirect:/admin/notice/listAllNoticeTemplate";
	}

}
