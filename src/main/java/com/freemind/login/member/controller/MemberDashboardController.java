package com.freemind.login.member.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.article.entity.Article;
import com.freemind.article.service.ArticleInteractionService;
import com.freemind.course.course.model.Course;
import com.freemind.course.course.model.CourseService;
import com.freemind.course.order.model.OrderDetail;
import com.freemind.course.order.model.OrderDetailService;
import com.freemind.course.order.model.ShoppingCartRedisService;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;
import com.freemind.login.notice.service.NoticeService;
import com.freemind.login.security.membersecurity.MemberUserDetails;


@Controller
@RequestMapping("/member/dashboard")
public class MemberDashboardController {

	@Autowired
	private ArticleInteractionService articleInteractionService;

	@Autowired
	private CourseService courseSvc;
	@Autowired
	private MemberService memberSvc;
	@Autowired
	private OrderDetailService orderDetailSvc;
	@Autowired
	private ShoppingCartRedisService shoppingCartRedisSvc;
	@Autowired
	private NoticeService noticeSvc;
	
	@ModelAttribute
	public void addMemberAttributes(
	        Authentication authentication,
	        ModelMap model) {

	    // 訪客的預設資料
	    model.addAttribute("member", null);
	    model.addAttribute("countMemberUnread", 0L);
	    model.addAttribute("countMemberCartCount", 0L);

	    // 未登入或匿名使用者
	    if (authentication == null
	            || authentication instanceof AnonymousAuthenticationToken
	            || !authentication.isAuthenticated()) {
	        return;
	    }
	    Member member =
	            memberSvc.findByAccount(authentication.getName());
	    if (member == null) {
	        System.out.println("找不到對應會員資料");
	        return;
	    }
	    Long unreadCount =
	            noticeSvc.countMemberUnread(member.getMemberId());
	    Long cartCount =
	    		shoppingCartRedisSvc.getCourseCount(member.getMemberId());
	    model.addAttribute("member", member);
	    model.addAttribute(
	            "countMemberUnread",
	            unreadCount != null ? unreadCount : 0L
	    );
	    model.addAttribute(
	            "countMemberCartCount",
	            cartCount != null ? cartCount : 0L
	    );
	}
	@GetMapping("/myAppointment")
	public String myAppointment() {
		return "redirect:/member/orders/myOrders";
	}
	
	@GetMapping
    public String getDashboard() {
        return "front-end/member/memberpage/dashboard";
    }
	
	@GetMapping("/myActivityRegistration")
	public String myActivityRegistration() {
	    return "redirect:/member/activity/registration/myRegistrations";
	}

	@GetMapping("/myCollection")
    public String getMyCollection() {
        return "front-end/member/memberpage/myCollection";
    }
	
	@GetMapping("/myCollection/article")
	public String articleTabs(Model model,
			 @RequestParam(defaultValue = "bookmark") String type,
			 @RequestParam(defaultValue = "1") Integer page,
			 @AuthenticationPrincipal MemberUserDetails prinUserDetails) {
		
		if (prinUserDetails == null) {
		    return "redirect:/front-end/login";
		}
		
		Integer memberId = prinUserDetails.getMember().getMemberId();
	
		Page<Article> articlePage;
		switch(type) {
			case "like":
				articlePage = articleInteractionService.getLikedArticles(memberId, page);
				break;
			case "history":
				articlePage = articleInteractionService.getViewHistory(memberId, page);
				break;
			default:
				type = "bookmark";
				articlePage = articleInteractionService.getSavedArticles(memberId, page);
				break;
		}
		
		model.addAttribute("activeTab", type);
		model.addAttribute("articlePage", articlePage);
	    model.addAttribute("currentPage", page);
		return "front-end/member/article/myArticleCollection";
	}
	
	@GetMapping("/myCollection/activity")
	public String myCollectionActivity() {
	    return "redirect:/member/activity/follow/myFollows";
	}
	
	@GetMapping("/myCollection/course")
	public String courseTabs(
			@ModelAttribute("member") Member member,
			@RequestParam(defaultValue = "1") Integer page,
			@ModelAttribute("countMemberCartCount") Long countMemberCartCount,
			ModelMap model) {
		if (page < 1)  page = 1;
		Integer currentPage = page;
		Page<Course> myBookmarks = courseSvc.getBookmarkCourses(member.getMemberId(), currentPage - 1);
		
		
		model.addAttribute("myBookmarks", myBookmarks);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", myBookmarks.getTotalPages());
		return "front-end/member/course/myBookmarks";
	}
	
	@GetMapping("/myLearning")
	public String myCourses(
			@ModelAttribute("member") Member member,
			@RequestParam(defaultValue = "1") Integer page,
			ModelMap model) {
		if (page < 1)  page = 1;
		Integer currentPage = page;
		
		Page<OrderDetail> myCoursePage =
                orderDetailSvc.getAccessibleOrderDetails(member, currentPage - 1);
		Long cartCount = (member != null ? shoppingCartRedisSvc.getCourseCount(member.getMemberId()) : null);
		
		model.addAttribute("myCoursePage", myCoursePage);
		model.addAttribute("myCourses", myCoursePage.getContent());
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", myCoursePage.getTotalPages());
		return "front-end/member/course/allMyCourse";
	}
	
	@PostMapping("/favorite/toggle")
	public String courseFavoriteToggle(
			@ModelAttribute("member") Member member,
	        @RequestParam(name = "currentPage", required = false, defaultValue = "1") Integer page,
	        @RequestParam(name = "orderBy", required = false) String orderBy,
	        @RequestParam(name = "courseId") Integer courseId,
	        @RequestParam(value = "returnUrl", required = false) String returnUrl,
	        RedirectAttributes redirectAttributes) {
		Course course = courseSvc.getOneCourse(courseId);
		Integer saveCount = course.getSaveCount();
	    if (!courseSvc.isCourseInBookmark(member.getMemberId(), courseId)) {
	        courseSvc.addCourseBookmark(member.getMemberId(), courseId);
	        course.setSaveCount(saveCount + 1);
	    } else {
	        courseSvc.removeCourseBookmark(member.getMemberId(), courseId);
	        if(saveCount == 0) saveCount = 1;
	        course.setSaveCount(saveCount - 1);
	    }
	    courseSvc.updateCourse(course);

	    redirectAttributes.addFlashAttribute("page", page);
	    redirectAttributes.addFlashAttribute("orderBy", orderBy);
	    return "redirect:" + returnUrl;
	}
	
}
