package com.freemind.course.course.controller;

import java.util.ArrayList;
import java.util.List;

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
import com.freemind.course.order.model.CourseOrder;
import com.freemind.course.order.model.CourseOrderService;
import com.freemind.course.order.model.OrderDetail;
import com.freemind.course.order.model.OrderDetailService;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/course/member")
public class CourseForMemberController {

	private final CourseService courseSvc;
	private final MemberService memberSvc;
	private final CourseOrderService courseOrderSvc;
	private final OrderDetailService orderDetailSvc;
	public CourseForMemberController(
			CourseService courseSvc, MemberService memberSvc,
			CourseOrderService courseOrderSvc,
			OrderDetailService orderDetailSvc) {
		this.courseSvc = courseSvc;
		this.memberSvc = memberSvc;
		this.courseOrderSvc = courseOrderSvc;
		this.orderDetailSvc = orderDetailSvc;
	}
	
    @ModelAttribute("member")
    public Member currentMember(Authentication authentication) {
        return memberSvc.findByAccount(authentication.getName());
    }
	
	@GetMapping("/select_course")
	public String memberSelectCourse(
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(name = "orderBy", required = false) String orderBy,
			@ModelAttribute("member") Member member,
			ModelMap model, HttpSession session) {

		if (page < 1)  page = 1;
		Integer currentPage = page;
		System.out.println(member);
		System.out.println(member.getName());
		String sortField = (orderBy == null || orderBy.isBlank()) ? "courseId" : orderBy;
		Page<Course> courseListListed = courseSvc.findCourseByCourseStstus((byte)4, currentPage - 1, sortField);
		for(Course course : courseListListed) {
			course.setSaved(courseSvc
					.isCourseInBookmark(member.getMemberId(), course.getCourseId()));
		}
		model.addAttribute("memberName", member.getName());
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("courseListListed", courseListListed);
		model.addAttribute("totalPages", courseListListed.getTotalPages());
		if(orderBy != null)
			model.addAttribute("orderBy", orderBy);

		return "front-end/member/course/selectCourse";
	}
	
	@GetMapping("/get_one_course")
	public String memberGetOneCourse(
			@RequestParam("courseId") Integer courseId,
			@ModelAttribute("member") Member member,ModelMap model) {
		Course course = courseSvc.getOneCourse(courseId);
		course.setSaved(courseSvc
				.isCourseInBookmark(member.getMemberId(), course.getCourseId()));
		boolean coursePermission = orderDetailSvc.hasCoursePermission(member.getMemberId(), courseId);
		model.addAttribute("coursePermission", coursePermission);
		model.addAttribute("course", course);
		return "front-end/member/course/listOneCourse";
	}
	@GetMapping("/my_bookmarks")
	public String myBookmarks(
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
	@GetMapping("/my_courses")
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
		
		System.out.println("目前 memberId = " + member.getMemberId());
		System.out.println("查到筆數 = " + myCoursePage.getTotalElements());
	    System.out.println("目前頁資料筆數 = " + myCoursePage.getContent().size());
	    
		model.addAttribute("myCoursePage", myCoursePage);
		model.addAttribute("myCourses", myCoursePage.getContent());
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", myCoursePage.getTotalPages());
		if(orderBy != null)
			model.addAttribute("orderBy", orderBy);
		return "front-end/member/course/allMyCourse";
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
	
	
	@PostMapping("/favorite/toggle")
	public String favoriteToggle(
			@ModelAttribute("member") Member member,
	        @RequestParam(name = "currentPage", required = false, defaultValue = "1") Integer page,
	        @RequestParam(name = "orderBy", required = false) String orderBy,
	        @RequestParam(name = "courseId") Integer courseId,
	        @RequestParam(value = "returnUrl", required = false) String returnUrl,
	        RedirectAttributes redirectAttributes) {

	    System.out.println("page = " + page);
	    System.out.println("orderBy = " + orderBy);

	    if (member == null) {
	        redirectAttributes.addFlashAttribute("mError", "先登入方可加入收藏");
	        redirectAttributes.addFlashAttribute("page", page);
	        redirectAttributes.addFlashAttribute("orderBy", orderBy);
	        return "redirect:/course/memberSelectCourse";
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
	
}
