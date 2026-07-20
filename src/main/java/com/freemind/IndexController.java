package com.freemind;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.freemind.course.course.model.Course;
import com.freemind.course.course.model.CourseService;
import com.freemind.login.admin.model.Admin;
import com.freemind.login.admin.model.AdminService;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;
import com.freemind.login.notice.service.NotificationService;

@Controller
public class IndexController {
	
    @Autowired
    private NotificationService notificationSvc;
    @Autowired
    private AdminService adminSvc;
    @Autowired
    private MemberService memberSvc;
    @Autowired
    private CourseService courseSvc;
    
    @ModelAttribute("admin")
    public Admin currentAdmin(Authentication authentication) {
        // 訪客（未登入或匿名）時不放 member 進 model
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return adminSvc.findByAccount(authentication.getName());
    }
    @ModelAttribute("member")
    public Member currentMember(Authentication authentication) {
    	// 訪客（未登入或匿名）時不放 member 進 model
    	if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
    		return null;
    	}
    	return memberSvc.findByAccount(authentication.getName());
    }

    @GetMapping("/")
    public String index(Model model) {
        // 首頁顯示已發布(顯示)的系統公告，新到舊排序
        model.addAttribute("notifications", notificationSvc.getPublished());
        // 前三熱門的課程	
        List<Course> top3Courses =
        		courseSvc.findTop3PopularListedCourses().getContent();
        model.addAttribute("top3Courses", top3Courses);
        
        return "index";
    }
    

    @GetMapping("/admin/home")
	public String adminHome() {

		return "back-end/adminHome";
	}

    // 聯繫客服（header / footer 的「聯繫客服」連結，未登入亦可瀏覽）
    @GetMapping("/support")
    public String support() {
        return "front-end/member/memberpage/support";
    }
  
}