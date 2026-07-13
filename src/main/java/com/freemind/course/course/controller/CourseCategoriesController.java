package com.freemind.course.course.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.ModelAndView;

import com.freemind.course.course.model.CourseCategories;
import com.freemind.course.course.model.CourseCategoriesService;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/course")
public class CourseCategoriesController {
	
	@Autowired
	CourseCategoriesService courseCategoriesSvc;
	
//	@GetMapping("/select_course_categories")
//	public String selectCourseCategories(ModelMap model) {
//		CourseCategories courseCategories = new CourseCategories();
//		model.addAttribute("courseCategories", courseCategories);
//		return "back-end/course/course/selectCourseCategories";
//	}
//	@PostMapping("/insert_course_categories")
//	public String insertCourseCategories(
//			@Valid @ModelAttribute("courseCategories")CourseCategories courseCategories, 
//			BindingResult result, ModelMap model) {
//		if(result.hasErrors()) {
//			return "back-end/course/course/selectCourseCategories";
//		}
//		courseCategoriesSvc.addCourseCategories(courseCategories);
//		return "redirect:/course/select_course_categories";
//	}
//
//	@ModelAttribute("courseCategoriesListAll")
//	public List<CourseCategories> courseCategoriesListAll(){
//		List<CourseCategories> courseCategoriesListAll = courseCategoriesSvc.getAllCourseCategories();
//		return courseCategoriesListAll;
//	}
	
}
