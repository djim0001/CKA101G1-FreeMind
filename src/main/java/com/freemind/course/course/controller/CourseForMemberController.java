package com.freemind.course.course.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.freemind.course.course.model.Course;
import com.freemind.course.course.model.CourseCategoriesService;
import com.freemind.course.course.model.CourseService;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;
import com.freemind.login.psychologist.service.PsychologistService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/course")
public class CourseForMemberController {

	private final CourseService courseSvc;
	private final MemberService memberSvc;
	public CourseForMemberController(
			CourseService courseSvc, MemberService memberSvc) {
		this.courseSvc = courseSvc;
		this.memberSvc = memberSvc;
	}
	
	@PostMapping("set_memberId_session")
	public String setMemberIdSession(@RequestParam(name = "memberIdSession") Integer memberIdSession, ModelMap model,
			HttpSession session) {
		session.setAttribute("memberId", memberIdSession);
		
		return "redirect:/course/memberSelectCourse";
	}
	
	@GetMapping("memberSelectCourse")
	public String memberSelectCourse(
			@SessionAttribute(name = "memberId", required = false) Integer memberId,
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(name = "orderBy", required = false) String orderBy,
			ModelMap model, HttpSession session) {

		if (memberId != null) {
			Member member = memberSvc.getOneMember(memberId);
			model.addAttribute("member", member);
		}
		
		if (page < 1)  page = 1;
		Integer currentPage = page;
		
		String sortField = (orderBy == null || orderBy.isBlank()) ? "courseId" : orderBy;
		Page<Course> courseListListed = courseSvc.findCourseByCourseStstus((byte)4, currentPage - 1, sortField);
		
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("courseListListed", courseListListed);
		model.addAttribute("totalPages", courseListListed.getTotalPages());

		return "front-end/member/course/selectCourse";
	}
	
	@GetMapping("memberGetOneCourse")
	public String memberGetOneCourse(@RequestParam("courseId") Integer courseId, ModelMap model) {
		Course course = courseSvc.getOneCourse(courseId);
		model.addAttribute("course", course);
		return "front-end/member/course/listOneCourse";
	}
	
}
