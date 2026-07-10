package com.freemind.course.course.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.freemind.course.course.model.Course;
import com.freemind.course.course.model.CourseCategories;
import com.freemind.course.course.model.CourseCategoriesService;
import com.freemind.course.course.model.CourseService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/course")
public class CourseForAdminController {
	
	private final CourseService courseSvc;
	private final CourseCategoriesService courseCategoriesSvc;;
	
	public CourseForAdminController(
			CourseService courseSvc,
			CourseCategoriesService courseCategoriesSvc) {
		this.courseSvc = courseSvc;
		this.courseCategoriesSvc = courseCategoriesSvc;
	}
	
	
	@PostMapping("set_adminId_session")
	public String setAdminIdSession(@RequestParam(name = "adminIdSession") Integer adminIdSession, ModelMap model,
			HttpSession session) {
		session.setAttribute("adminId", adminIdSession);
		return "redirect:/admin/course/select_course";
	}
	@PostMapping("/listed")
	public String listed() {
		courseSvc.checkAllCourseStatus();
		return "redirect:/admin/course/select_course";
	}
	@GetMapping("/select_course")
	public String admunSelectCourse(
			@SessionAttribute(name = "adminId", required = false) Integer adminId,
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(name = "orderBy", required = false) String orderBy,
			ModelMap model, HttpSession session) {

		if (adminId == null) 
			return "back-end/course/course/selectCourse";
		
		if (page < 1)  page = 1;
		Integer currentPage = page;		
		String sortField = (orderBy == null || orderBy.isBlank()) ? "courseId" : orderBy;
		Page<Course> courseListSubmit = courseSvc.findCoursesExcludeStatus((byte)0, currentPage - 1, sortField);
		
		model.addAttribute("courseListSubmit", courseListSubmit);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", courseListSubmit.getTotalPages());
		if(orderBy != null)
			model.addAttribute("orderBy", orderBy);
		

		return "back-end/course/course/selectCourse";
	}
	
	@PostMapping("/get_one_course")
	public String adminGetOneCourse(@RequestParam("courseId") Integer courseId, ModelMap model) {
		Course course = courseSvc.getOneCourse(courseId);
		model.addAttribute("course", course);
		return "back-end/course/course/listOneCourse";
	}
	
	@PostMapping("/examine_course")
	public String adminExamineCourse(
			@RequestParam("courseId") Integer courseId, 
			@RequestParam("courseStatus") Byte courseStatus, 
			@SessionAttribute(name = "adminId", required = false) Integer adminId,
			ModelMap model) {
		Course course = courseSvc.getOneCourse(courseId);
		course.setCourseStatus(courseStatus);
		course.setAdminId(adminId);
		courseSvc.updateCourse(course);
		model.addAttribute("course", course);
		return "back-end/course/course/listOneCourse";
	}
	
	
	
	//課程分類
	@GetMapping("/select_course_categories")
	public String selectCourseCategories(ModelMap model) {
		CourseCategories courseCategories = new CourseCategories();
		model.addAttribute("courseCategories", courseCategories);
		return "back-end/course/course/selectCourseCategories";
	}
	@PostMapping("/insert_course_categories")
	public String insertCourseCategories(
			@Valid @ModelAttribute("courseCategories")CourseCategories courseCategories, 
			BindingResult result, ModelMap model) {
		if(result.hasErrors()) {
			return "back-end/course/course/selectCourseCategories";
		}
		courseCategoriesSvc.addCourseCategories(courseCategories);
		return "redirect:/admin/course/select_course_categories";
	}

	@ModelAttribute("courseCategoriesListAll")
	public List<CourseCategories> courseCategoriesListAll(){
		List<CourseCategories> courseCategoriesListAll = courseCategoriesSvc.getAllCourseCategories();
		return courseCategoriesListAll;
	}
	
	
}
