package com.freemind.course.course.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
import com.freemind.course.dto.CourseOrderSearchCondition;
import com.freemind.course.dto.CourseSearchCondition;
import com.freemind.course.order.model.CourseOrder;
import com.freemind.course.order.model.CourseOrderService;
import com.freemind.course.order.model.OrderDetail;
import com.freemind.course.order.model.OrderDetailService;
import com.freemind.course.order.model.RefundService;
import com.freemind.login.admin.model.Admin;
import com.freemind.login.admin.model.AdminService;
import com.freemind.login.notice.service.NoticeService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/course")
public class CourseForAdminController {

	private final AdminService adminSvc;
	private final CourseService courseSvc;
	private final NoticeService noticeSvc;
	private final RefundService refundService;
	private final CourseOrderService courseOrderSvc;
	private final OrderDetailService orderDetailSvc;
	private final CourseCategoriesService courseCategoriesSvc;

	public CourseForAdminController(
			AdminService adminSvc, 
			RefundService refundService,
			CourseService courseSvc, 
			NoticeService noticeSvc,
			CourseOrderService courseOrderSvc,
			OrderDetailService orderDetailSvc, 
			CourseCategoriesService courseCategoriesSvc) {
		this.adminSvc = adminSvc;
		this.courseSvc = courseSvc;
		this.noticeSvc = noticeSvc;
		this.refundService = refundService;
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
	public String admunSelectCourse(@ModelAttribute("admin") Admin admin,
			@ModelAttribute("condition") CourseSearchCondition condition,
			@RequestParam(defaultValue = "1") Integer reviewPage, 
			@RequestParam(defaultValue = "1") Integer delistPage,
			@RequestParam(defaultValue = "courseIdDesc") String reviewOrderBy,
			@RequestParam(defaultValue = "courseIdDesc") String delistOrderBy, 
			ModelMap model) {

//		if(condition.getCourseStatus()==null) condition.setCourseStatus((byte)1);
		if (reviewPage < 1)
			reviewPage = 1;
		Integer reviewCurrentPage = reviewPage;
		if (delistPage < 1)
			delistPage = 1;
		Integer delistCurrentPage = delistPage;
		String reviewSortField = (reviewOrderBy == null || reviewOrderBy.isBlank()) ? "courseId" : reviewOrderBy;
		String delistSortField = (delistOrderBy == null || delistOrderBy.isBlank()) ? "courseId" : delistOrderBy;
		Page<Course> courseListSubmit = courseSvc.adminSearchCourses(condition, reviewCurrentPage - 1);
		Page<Course> courseList = courseSvc.findCourseByCourseStstus(
									(byte) 4, delistCurrentPage - 1, delistSortField);
		
		int wait = courseSvc.countCoursesByStatus((byte) 1);
		int success = courseSvc.countCoursesByStatus((byte) 2);
		int fail = courseSvc.countCoursesByStatus((byte) 3);
		int listed = courseSvc.countCoursesByStatus((byte) 4);
		int delisted = courseSvc.countCoursesByStatus((byte) 5);

		model.addAttribute("courseCountAwait", wait);
		model.addAttribute("courseCountSuccess", success);
		model.addAttribute("courseCountFail", fail);
		model.addAttribute("courseCountListed", listed);
		model.addAttribute("courseCountDelisted", delisted);

		model.addAttribute("courseListSubmit", courseListSubmit);
		model.addAttribute("reviewCurrentPage", reviewCurrentPage);
		model.addAttribute("reviewTotalPages", courseListSubmit.getTotalPages());
		if (reviewSortField != null)
			model.addAttribute("reviewSortField", reviewSortField);
		
		model.addAttribute("courseList", courseList);
		model.addAttribute("delistCurrentPage", delistCurrentPage);
		model.addAttribute("delistTotalPages", courseList.getTotalPages());
		
		if (delistSortField != null)
			model.addAttribute("delistSortField", delistSortField);

		return "back-end/course/course/selectCourse";
	}

	@PostMapping("/delisted_course")
	public String delistCourse(@RequestParam Integer courseId, @RequestParam DelistReason delistReason,
			RedirectAttributes redirectAttributes) {

		courseSvc.delistCourse(courseId, delistReason);

		redirectAttributes.addFlashAttribute("successMessage", "課程下架成功");

		return "redirect:/admin/course/select_course";
	}

	@GetMapping("/select_course_order")
	public String adminSelectCourseOrder(@ModelAttribute("admin") Admin admin,
			@RequestParam(defaultValue = "1") Integer page,
			@ModelAttribute("condition") CourseOrderSearchCondition condition,
			@RequestParam(name = "orderBy", required = false) String orderBy, ModelMap model) {
		if (page < 1)
			page = 1;
		Integer currentPage = page;
		String sortField = (orderBy == null || orderBy.isBlank()) ? "orderedAt" : orderBy;
		Page<CourseOrder> allCourseOrder = courseOrderSvc.getAllOrder(currentPage - 1, sortField);

		model.addAttribute("refundList", refundService.getAllRefund());
		model.addAttribute("allCourseOrder", allCourseOrder);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", allCourseOrder.getTotalPages());
		return "back-end/course/course/allCourseOrder";
	}
	@GetMapping("/order_search")
	public String searchOrders(@ModelAttribute("condition") CourseOrderSearchCondition condition,
			@RequestParam(name = "orderBy", required = false) String orderBy,
			@RequestParam(defaultValue = "1") Integer page,

			Model model) {
		Integer currentPage = page;
		String sortField = (orderBy == null || orderBy.isBlank()) ? "orderedAt" : orderBy;

		Page<CourseOrder> orderPage = courseOrderSvc.searchOrders(condition, page);

		model.addAttribute("allCourseOrder", orderPage);
		model.addAttribute("refundList", refundService.getAllRefund());
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", orderPage.getTotalPages());

		model.addAttribute("totalElements", orderPage.getTotalElements());

		return "back-end/course/course/allCourseOrder";
	}

	@GetMapping("/order/detail/{orderId}")
	public String adminOrderDetail(@PathVariable("orderId") Integer orderId,
			@RequestParam(value = "returnUrl", required = false) String returnUrl, ModelMap model,
			RedirectAttributes redirectAttributes) {
		List<OrderDetail> details = orderDetailSvc.getOrderDetailsByCourseOrderId(orderId);

		redirectAttributes.addFlashAttribute("details", details);
		redirectAttributes.addFlashAttribute("detailsMsg", "show");

		return "redirect:/admin/course/select_course_order";
	}

	

	

	@PostMapping("/get_one_course")
	public String adminGetOneCourse(@RequestParam("courseId") Integer courseId,
			@ModelAttribute("delistReasons") DelistReason[] delistReasons, ModelMap model) {
		Course course = courseSvc.getOneCourse(courseId);
		model.addAttribute("course", course);
		return "back-end/course/course/listOneCourse";
	}

	@PostMapping("/examine_course")
	public String adminExamineCourse(@RequestParam("courseId") Integer courseId,
			@RequestParam("courseStatus") Byte courseStatus, @ModelAttribute("admin") Admin admin, ModelMap model) {
		Course course = courseSvc.getOneCourse(courseId);
		course.setCourseStatus(courseStatus);
		course.setAdmin(admin);
		courseSvc.updateCourse(course);
		model.addAttribute("course", course);
		return "back-end/course/course/listOneCourse";

	}

	@PostMapping("/course_search")
	public String courseSearch(@RequestParam("courseId") Integer courseId,
			@RequestParam("courseStatus") Byte courseStatus, @ModelAttribute("admin") Admin admin, ModelMap model) {
		Course course = courseSvc.getOneCourse(courseId);
		course.setCourseStatus(courseStatus);
		course.setAdmin(admin);
		courseSvc.updateCourse(course);
		model.addAttribute("course", course);
		return "back-end/course/course/listOneCourse";

	}

	// 課程分類
	@GetMapping("/select_course_categories")
	public String selectCourseCategories(ModelMap model) {
		CourseCategories courseCategories = new CourseCategories();
		model.addAttribute("courseCategories", courseCategories);
		return "back-end/course/course/selectCourseCategories";
	}

	@PostMapping("/insert_course_categories")
	public String insertCourseCategories(@Valid @ModelAttribute("courseCategories") CourseCategories courseCategories,
			BindingResult result, ModelMap model) {
		if (result.hasErrors()) {
			return "back-end/course/course/selectCourseCategories";
		}
		courseCategoriesSvc.addCourseCategories(courseCategories);
		return "redirect:/admin/course/select_course_categories";
	}

	@ModelAttribute("courseCategoriesListAll")
	public List<CourseCategories> courseCategoriesListAll() {
		List<CourseCategories> courseCategoriesListAll = courseCategoriesSvc.getAllCourseCategories();
		return courseCategoriesListAll;
	}

}
