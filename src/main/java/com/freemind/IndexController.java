package com.freemind;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.freemind.login.notice.service.NotificationService;

@Controller
public class IndexController {

    @Autowired
    private NotificationService notificationSvc;

    @GetMapping("/")
    public String index(Model model) {
        // 首頁顯示已發布(顯示)的系統公告，新到舊排序
        model.addAttribute("notifications", notificationSvc.getPublished());
        return "index";
    }
    
    @GetMapping("/admin/home")   
	public String adminHome() {
		return "back-end/adminHome";
	}
  
}