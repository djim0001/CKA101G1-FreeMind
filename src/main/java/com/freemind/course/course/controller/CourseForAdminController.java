package com.freemind.course.course.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/course")
public class CourseForAdminController {
	@Autowired
	CourseService courseSvc;

	@Autowired
	CourseCategoriesService courseCategoriesSvc;
	
	
	@PostMapping("set_adminId_session")
	public String setAdminIdSession(@RequestParam(name = "adminIdSession") Integer adminIdSession, ModelMap model,
			HttpSession session) {
//		session.setAttribute("adminId", adminIdSession);
//		return "redirect:/course/adminSelectCourse";
//	}
//	@GetMapping("adminSelectCourse")
//	public String psychSelectCourse(
//			@SessionAttribute(name = "adminId", required = false) Integer adminId,
//			ModelMap model, HttpSession session) {
//
//		if (adminId != null) {
//			List<Course> courseListAll = courseSvc.getAllCourse();
//			model.addAttribute("courseListAll", courseListAll);
//		}
//
		return "back-end/course/course/adminSelectCourse";
	}
}
