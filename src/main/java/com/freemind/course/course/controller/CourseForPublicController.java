package com.freemind.course.course.controller;

import org.springframework.data.domain.Page;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import com.freemind.course.course.model.Course;
import com.freemind.course.course.model.CourseService;
import com.freemind.course.order.model.CourseOrderService;
import com.freemind.course.order.model.OrderDetailService;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;

import jakarta.servlet.http.HttpSession;

public class CourseForPublicController {
	private final CourseService courseSvc;
	private final MemberService memberSvc;
	private final OrderDetailService orderDetailSvc;
	private final CourseOrderService courseOrderSvc;
	
	public CourseForPublicController(
			CourseService courseSvc, 
			MemberService memberSvc,
			CourseOrderService courseOrderSvc,
			OrderDetailService orderDetailSvc) {
		this.courseSvc = courseSvc;
		this.memberSvc = memberSvc;
		this.courseOrderSvc = courseOrderSvc;
		this.orderDetailSvc = orderDetailSvc;
	}

	@GetMapping("/select_course")
	public String memberSelectCourse(
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(name = "orderBy", required = false) String orderBy,
			@ModelAttribute("member") Member member,
			ModelMap model, HttpSession session) {

		if (page < 1)  page = 1;
		Integer currentPage = page;
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
	
}
