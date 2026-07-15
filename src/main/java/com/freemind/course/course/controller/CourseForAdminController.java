package com.freemind.course.course.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.course.course.model.Course;
import com.freemind.course.course.model.CourseCategories;
import com.freemind.course.course.model.CourseCategoriesService;
import com.freemind.course.course.model.CourseService;
import com.freemind.course.course.model.DelistReason;
import com.freemind.course.order.model.CourseOrder;
import com.freemind.course.order.model.CourseOrderService;
import com.freemind.course.order.model.OrderDetail;
import com.freemind.course.order.model.OrderDetailService;
import com.freemind.login.admin.model.Admin;
import com.freemind.login.admin.model.AdminService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/course")
public class CourseForAdminController {
	
	private final CourseService courseSvc;
	private final AdminService adminSvc;
	private final CourseOrderService courseOrderSvc;
	private final OrderDetailService orderDetailSvc;
	private final CourseCategoriesService courseCategoriesSvc;
	
	public CourseForAdminController(
			CourseService courseSvc,
			AdminService adminSvc,
			CourseOrderService courseOrderSvc,
			OrderDetailService orderDetailSvc,
			CourseCategoriesService courseCategoriesSvc) {
		this.courseSvc = courseSvc;
		this.adminSvc = adminSvc;
		this.courseOrderSvc = courseOrderSvc;
		this.orderDetailSvc = orderDetailSvc;
		this.courseCategoriesSvc = courseCategoriesSvc;
	}
	
	@ModelAttribute("admin")
    public Admin currentAdmin(Authentication authentication) {
        return adminSvc.findByAccount(authentication.getName());
    }
	@ModelAttribute("delistReasons")
	public DelistReason[] getDelistReasons() {
		return DelistReason.values();
	}
	
	@PostMapping("/listed")
	public String listed() {
		courseSvc.checkAllCourseStatus();
		return "redirect:/admin/course/select_course";
	}
	@GetMapping("/select_course")
	public String admunSelectCourse(
			@ModelAttribute("admin") Admin admin,
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(name = "orderBy", required = false) String orderBy,
			ModelMap model, HttpSession session) {
		
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
	@GetMapping("/goto_delisted")
	public String gotoDelisted(
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(name = "orderBy", required = false) String orderBy,
			ModelMap model, HttpSession session) {
		Integer currentPage = page;
		String sortField = (orderBy == null || orderBy.isBlank()) ? "courseId" : orderBy;
		Page<Course> courseList = courseSvc.findCourseByCourseStstus((byte)4, currentPage - 1, sortField);
		
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("courseList", courseList);
		model.addAttribute("totalPages", courseList.getTotalPages());
		if(orderBy != null)
			model.addAttribute("orderBy", orderBy);
		return "back-end/course/course/detailCourse";
	}
	
	@PostMapping("/delisted_course")
	public String delistCourse(
			@RequestParam Integer courseId,
			@RequestParam DelistReason delistReason,
			RedirectAttributes redirectAttributes) {

		courseSvc.delistCourse(courseId, delistReason);

		redirectAttributes.addFlashAttribute(
			"successMessage",
			"課程下架成功"
		);

		return "redirect:/admin/course/goto_delisted";
	}

	@GetMapping("/select_course_order")
	public String adminSelectCourseOrder(
			@ModelAttribute("admin") Admin admin,
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(name = "orderBy", required = false) String orderBy,
			ModelMap model) {
		if (page < 1)  page = 1;
		Integer currentPage = page;		
		String sortField = (orderBy == null || orderBy.isBlank()) ? "orderedAt" : orderBy;
		Page<CourseOrder> allCourseOrder = courseOrderSvc.getAllOrder(currentPage - 1, sortField);
		model.addAttribute("allCourseOrder", allCourseOrder);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", allCourseOrder.getTotalPages());
		return "back-end/course/course/allCourseOrder";
	}
	
	@GetMapping("/order/detail/{orderId}")
	public String adminOrderDetail(
			@PathVariable("orderId") Integer orderId,
			@RequestParam(value = "returnUrl", required = false) String returnUrl,
			ModelMap model,
			RedirectAttributes redirectAttributes) {
		List<OrderDetail> details = orderDetailSvc.getOrderDetailsByCourseOrderId(orderId);
		
		redirectAttributes.addFlashAttribute("details", details);
		redirectAttributes.addFlashAttribute("detailsMsg", "show");
		
		
		return "redirect:/admin/course/select_course_order";
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
			@ModelAttribute("admin") Admin admin,
			ModelMap model) {
		Course course = courseSvc.getOneCourse(courseId);
		course.setCourseStatus(courseStatus);
		course.setAdmin(admin);
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
