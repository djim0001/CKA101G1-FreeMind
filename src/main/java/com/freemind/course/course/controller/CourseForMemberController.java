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
import com.freemind.login.psychologist.model.PsychologistService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/course")
public class CourseForMemberController {

	@Autowired
	CourseService courseSvc;

	@Autowired
	CourseCategoriesService courseCategoriesSvc;
	
	@Autowired
	PsychologistService psychologistService;
	
	@Autowired
	MemberService memberService;
	
	@PostMapping("set_memberId_session")
	public String setMemberIdSession(@RequestParam(name = "memberIdSession") Integer memberIdSession, ModelMap model,
			HttpSession session) {
		session.setAttribute("memberId", memberIdSession);
		
		return "redirect:/course/memberSelectCourse";
	}
	
	@GetMapping("memberSelectCourse")
	public String memberSelectCourse(
			@SessionAttribute(name = "memberId", required = false) Integer memberId,
			@RequestParam(name = "page", required = false) String page,
			@SessionAttribute(name = "psychCoursePageQty", required = false) String pageQty, 
			ModelMap model, HttpSession session) {

		if (memberId != null) {
			Member member = memberService.getOneMember(memberId);
			model.addAttribute("member", member);
		}
		Integer currentPage = (page == null) ? 1 : Integer.parseInt(page);
		model.addAttribute("currentPage", currentPage);
		Page<Course> courseListListed = courseSvc.findCourseByCourseStstus((byte)4, currentPage - 1);
		model.addAttribute("courseListListed", courseListListed);
//		if(session.getAttribute(pageQty) == null) 
//			session.setAttribute("psychCoursePageQty", 1);

		return "front-end/member/course/selectCourse";
	}
	
	@PostMapping("memberGetOneCourse")
	public String memberGetOneCourse(@RequestParam("courseId") Integer courseId, ModelMap model) {
		Course course = courseSvc.getOneCourse(courseId);
		model.addAttribute("course", course);
		return "front-end/member/course/listOneCourse";
	}
	
}
