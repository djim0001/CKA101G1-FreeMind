package com.freemind.course.course.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.course.course.model.Course;
import com.freemind.course.course.model.CourseService;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/course/member")
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
		
		return "redirect:/course/member/select_course";
	}
	
	@GetMapping("/select_course")
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
		for(Course course : courseListListed) {
			course.setSaved(courseSvc
					.isCourseInBookmark(memberId, course.getCourseId()));
		}
		
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("courseListListed", courseListListed);
		model.addAttribute("totalPages", courseListListed.getTotalPages());
		if(orderBy != null)
			model.addAttribute("orderBy", orderBy);

		return "front-end/member/course/selectCourse";
	}
	
	@GetMapping("/get_one_course")
	public String memberGetOneCourse(
			@SessionAttribute(name = "memberId", required = false) Integer memberId,
			@RequestParam("courseId") Integer courseId, ModelMap model) {
		Course course = courseSvc.getOneCourse(courseId);
		course.setSaved(courseSvc
				.isCourseInBookmark(memberId, course.getCourseId()));
		model.addAttribute("course", course);
		return "front-end/member/course/listOneCourse";
	}
	@GetMapping("/my_bookmarks")
	public String myBookmarks(
			@SessionAttribute(name = "memberId", required = false) Integer memberId,
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(name = "orderBy", required = false) String orderBy,
			ModelMap model) {
		if (page < 1)  page = 1;
		Integer currentPage = page;
		String sortField = (orderBy == null || orderBy.isBlank()) ? "courseId" : orderBy;
		Page<Course> myBookmarks = courseSvc.getBookmarkCourses(memberId, currentPage - 1, sortField);
		model.addAttribute("myBookmarks", myBookmarks);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", myBookmarks.getTotalPages());
		if(orderBy != null)
			model.addAttribute("orderBy", orderBy);
		return "front-end/member/course/myBookmarks";
	}
	@GetMapping("/my_courses")
	public String myCourses(
			@SessionAttribute(name = "memberId", required = false) Integer memberId,
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(name = "orderBy", required = false) String orderBy,
			ModelMap model) {
		if (page < 1)  page = 1;
		Integer currentPage = page;
		String sortField = (orderBy == null || orderBy.isBlank()) ? "courseId" : orderBy;
		Page<Course> myBookmarks = courseSvc.getBookmarkCourses(memberId, currentPage - 1, sortField);
		model.addAttribute("myBookmarks", myBookmarks);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", myBookmarks.getTotalPages());
		if(orderBy != null)
			model.addAttribute("orderBy", orderBy);
		return "front-end/member/course/allMyCourse";
	}
	
	
	@PostMapping("/favorite/toggle")
	public String favoriteToggle(
	        @SessionAttribute(name = "memberId", required = false) Integer memberId,
	        @RequestParam(name = "currentPage", required = false, defaultValue = "1") Integer page,
	        @RequestParam(name = "orderBy", required = false) String orderBy,
	        @RequestParam(name = "courseId") Integer courseId,
	        @RequestParam(value = "returnUrl", required = false) String returnUrl,
	        RedirectAttributes redirectAttributes) {

	    System.out.println("page = " + page);
	    System.out.println("orderBy = " + orderBy);

	    if (memberId == null) {
	        redirectAttributes.addFlashAttribute("mError", "先登入方可加入收藏");
	        redirectAttributes.addFlashAttribute("page", page);
	        redirectAttributes.addFlashAttribute("orderBy", orderBy);
	        return "redirect:/course/memberSelectCourse";
	    }

	    if (!courseSvc.isCourseInBookmark(memberId, courseId)) {
	        courseSvc.addCourseBookmark(memberId, courseId);
	    } else {
	        courseSvc.removeCourseBookmark(memberId, courseId);
	    }

	    redirectAttributes.addFlashAttribute("page", page);
	    redirectAttributes.addFlashAttribute("orderBy", orderBy);

	    return "redirect:" + returnUrl;
	}
	
}
