package com.freemind.login.notice.controller;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

import com.freemind.login.notice.entity.Notification;
import com.freemind.login.notice.service.NotificationService;
import com.freemind.login.security.adminsecurity.AdminUserDetails;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/notifications")
public class NotificationController {

	@Autowired
	private NotificationService notificationSvc;

	/*
	 * 顯示系統公告列表頁
	 */
	@GetMapping("listAllNotification")
	public String listAllNotification(ModelMap model) {
		List<Notification> list = notificationSvc.getAll();
		model.addAttribute("notificationListData", list);
		return "back-end/login/notice/notifications/listAllNotification";
	}

	/*
	 * 轉交至 addNotification.html
	 */
	@GetMapping("addNotification")
	public String addNotification(ModelMap model) {
		model.addAttribute("notification", new Notification());
		return "back-end/login/notice/notifications/addNotification";
	}

	/*
	 * 新增系統公告的表單提交
	 * admin_id 由系統抓目前登入的管理員帶入，created_at 為當下時間
	 */
	@PostMapping("insert")
	public String insert(@Valid @ModelAttribute("notification") Notification notification,
			BindingResult result, Authentication authentication) {

		if (result.hasErrors()) {
			return "back-end/login/notice/notifications/addNotification"; // 驗證失敗，回新增頁面
		}

		AdminUserDetails userDetails = (AdminUserDetails) authentication.getPrincipal();
		notification.setAdminId(userDetails.getAdmin().getAdminId());
		notification.setCreatedAt(new Timestamp(System.currentTimeMillis()));

		notificationSvc.addNotification(notification);

		return "redirect:/admin/notifications/listAllNotification"; // 防止 F5 重複新增
	}

	/*
	 * 點擊修改按鈕時，查出單筆資料並轉交至修改頁面
	 */
	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("noticeId") Integer noticeId, ModelMap model) {
		Notification notification = notificationSvc.getOneNotification(noticeId);
		model.addAttribute("notification", notification);
		return "back-end/login/notice/notifications/update_notification_input";
	}

	/*
	 * 修改系統公告的表單提交
	 * created_at 保留原值（表單不帶，自資料庫舊資料回填），admin_id 更新為本次操作的管理員
	 */
	@PostMapping("update")
	public String update(@Valid @ModelAttribute("notification") Notification notification,
			BindingResult result, Authentication authentication) {

		if (result.hasErrors()) {
			return "back-end/login/notice/notifications/update_notification_input"; // 驗證失敗，回修改頁面
		}

		Timestamp createdAt = notificationSvc.getOneNotification(notification.getNoticeId()).getCreatedAt();
		notification.setCreatedAt(createdAt);

		AdminUserDetails userDetails = (AdminUserDetails) authentication.getPrincipal();
		notification.setAdminId(userDetails.getAdmin().getAdminId());

		notificationSvc.updateNotification(notification);

		return "redirect:/admin/notifications/listAllNotification";
	}

	/*
	 * 處理刪除系統公告請求
	 */
	@PostMapping("delete")
	public String delete(@RequestParam("noticeId") Integer noticeId) {
		notificationSvc.deleteNotification(noticeId);
		return "redirect:/admin/notifications/listAllNotification";
	}

	/*
	 * 【下拉選單 / 共用資料準備】公告狀態對照（0隱藏 1顯示）
	 */
	@ModelAttribute("noticeStatusMap")
	protected Map<Byte, String> referenceMapData() {
		Map<Byte, String> map = new LinkedHashMap<Byte, String>();
		map.put((byte) 0, "隱藏");
		map.put((byte) 1, "顯示");
		return map;
	}

}
