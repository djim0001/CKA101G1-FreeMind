package com.freemind.course.course.controller;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.course.course.model.Course;
import com.freemind.course.course.model.CourseService;
import com.freemind.course.order.model.CourseOrderService;
import com.freemind.course.order.model.OrderDetail;
import com.freemind.course.order.model.OrderDetailService;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;

@Controller      
@RequestMapping("/member/dashboard")
public class MemberDashboardCourseController {
	
	private final CourseService courseSvc;
	private final MemberService memberSvc;
	private final OrderDetailService orderDetailSvc;
	public MemberDashboardCourseController(
			CourseService courseSvc, MemberService memberSvc,
			CourseOrderService courseOrderSvc,
			OrderDetailService orderDetailSvc) {
		this.courseSvc = courseSvc;
		this.memberSvc = memberSvc;
		this.orderDetailSvc = orderDetailSvc;
	}
	
	@ModelAttribute("member")
    public Member currentMember(Authentication authentication) {
        return memberSvc.findByAccount(authentication.getName());
    }
	
	@GetMapping("/myCollection/course")
	public String courseTabs(
			@ModelAttribute("member") Member member,
			@RequestParam(defaultValue = "1") Integer page,
			ModelMap model) {
		if (page < 1)  page = 1;
		Integer currentPage = page;
		Page<Course> myBookmarks = courseSvc.getBookmarkCourses(member.getMemberId(), currentPage - 1);
		model.addAttribute("myBookmarks", myBookmarks);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", myBookmarks.getTotalPages());
		return "front-end/member/course/myBookmarks";
	}
	
	@GetMapping("/mybookmarks/search")
	public String mybookmarksSearch(
	        @ModelAttribute("member") Member member,
	        @RequestParam(defaultValue = "1") Integer page,
	        @RequestParam(defaultValue = "") String keyword,
	        ModelMap model) {

	    if (page == null || page < 1) {
	        page = 1;
	    }

	    keyword = keyword == null ? "" : keyword.trim();

	    Page<Course> myBookmarks = courseSvc.getBookmarkCourses(
	            member.getMemberId(),
	            page,
	            keyword
	    );

	    model.addAttribute("myBookmarks", myBookmarks);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", myBookmarks.getTotalPages());
	    model.addAttribute("keyword", keyword);

	    return "front-end/member/course/myBookmarks";
	}
	
	@GetMapping("/myLearning")
	public String myCourses(
			@ModelAttribute("member") Member member,
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(name = "orderBy", required = false) String orderBy,
			ModelMap model) {
		if (page < 1)  page = 1;
		Integer currentPage = page;
		String sortField = (orderBy == null || orderBy.isBlank()) ? "courseId" : orderBy;
		
		Page<OrderDetail> myCoursePage =
                orderDetailSvc.getMyCourses(member.getMemberId(), currentPage - 1, sortField);
		
		model.addAttribute("myCoursePage", myCoursePage);
		model.addAttribute("myCourses", myCoursePage.getContent());
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", myCoursePage.getTotalPages());
		if(orderBy != null)
			model.addAttribute("orderBy", orderBy);
		return "front-end/member/course/allMyCourse";
	}
	
	@PostMapping("/favorite/toggle")
	public String favoriteToggle(
			@ModelAttribute("member") Member member,
	        @RequestParam(name = "currentPage", required = false, defaultValue = "1") Integer page,
	        @RequestParam(name = "orderBy", required = false) String orderBy,
	        @RequestParam(name = "courseId") Integer courseId,
	        @RequestParam(value = "returnUrl", required = false) String returnUrl,
	        RedirectAttributes redirectAttributes) {

	    if (member == null) {
	        redirectAttributes.addFlashAttribute("mError", "先登入方可加入收藏");
	        redirectAttributes.addFlashAttribute("page", page);
	        redirectAttributes.addFlashAttribute("orderBy", orderBy);
	        return "redirect:/member/course/select_course";
	    }

	    if (!courseSvc.isCourseInBookmark(member.getMemberId(), courseId)) {
	        courseSvc.addCourseBookmark(member.getMemberId(), courseId);
	    } else {
	        courseSvc.removeCourseBookmark(member.getMemberId(), courseId);
	    }

	    redirectAttributes.addFlashAttribute("page", page);
	    redirectAttributes.addFlashAttribute("orderBy", orderBy);
	    return "redirect:" + returnUrl;
	}
	
	@GetMapping("/one_my_course")
	public String oneMyCourse(
			@RequestParam("courseId") Integer courseId,
			@ModelAttribute("member") Member member,ModelMap model) {
		Course course = courseSvc.getOneCourse(courseId);
		course.setSaved(courseSvc
				.isCourseInBookmark(member.getMemberId(), course.getCourseId()));
		boolean coursePermission = orderDetailSvc.hasCoursePermission(member.getMemberId(), courseId);
		model.addAttribute("course", course);
		model.addAttribute("coursePermission", coursePermission);
		return "front-end/member/course/listOneCourse";
	}
	

}
