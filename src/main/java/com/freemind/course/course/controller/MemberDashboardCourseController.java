package com.freemind.course.course.controller;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
			@RequestParam(name = "orderBy", required = false) String orderBy,
			ModelMap model) {
		if (page < 1)  page = 1;
		Integer currentPage = page;
		String sortField = (orderBy == null || orderBy.isBlank()) ? "courseId" : orderBy;
		Page<Course> myBookmarks = courseSvc.getBookmarkCourses(member.getMemberId(), currentPage - 1, sortField);
		model.addAttribute("myBookmarks", myBookmarks);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", myBookmarks.getTotalPages());
		if(orderBy != null)
			model.addAttribute("orderBy", orderBy);
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
	

}
