package com.freemind.course.course.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.course.course.model.Course;
import com.freemind.course.course.model.CourseCategories;
import com.freemind.course.course.model.CourseCategoriesService;
import com.freemind.course.course.model.CourseService;
import com.freemind.course.order.model.CourseOrder;
import com.freemind.course.order.model.CourseOrderService;
import com.freemind.course.order.model.OrderDetail;
import com.freemind.course.order.model.OrderDetailService;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/member/course")
public class CourseForMemberController {
	private final String NEW_COURSE_ORDER = "listedAt";

	private final CourseService courseSvc;
	private final MemberService memberSvc;
	private final OrderDetailService orderDetailSvc;
	private final CourseOrderService courseOrderSvc;
	private final CourseCategoriesService courseCategoriesSvc;
	
	public CourseForMemberController(
			CourseService courseSvc, 
			MemberService memberSvc,
			CourseOrderService courseOrderSvc,
			OrderDetailService orderDetailSvc,
			CourseCategoriesService courseCategoriesSvc) {
		this.courseSvc = courseSvc;
		this.memberSvc = memberSvc;
		this.courseOrderSvc = courseOrderSvc;
		this.orderDetailSvc = orderDetailSvc;
		this.courseCategoriesSvc = courseCategoriesSvc;
	}
	
	@ModelAttribute("member")
    public Member currentMember(Authentication authentication) {
        // 訪客（未登入或匿名）時不放 member 進 model
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return memberSvc.findByAccount(authentication.getName());
    }
    @ModelAttribute("courseCategoriesListAll")
	public List<CourseCategories> courseCategoriesListAll(){
		List<CourseCategories> courseCategoriesListAll = courseCategoriesSvc.getAllCourseCategories();
		return courseCategoriesListAll;
	}
	
	@GetMapping("/select_course")
	public String memberSelectCourse(
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(name = "orderBy", required = false) String orderBy,
			ModelMap model, HttpSession session) {

		Member member = (Member)model.getAttribute("member");
		if (page < 1)  page = 1;
		Integer currentPage = page;
		String sortField = (orderBy == null || orderBy.isBlank()) ? "courseId" : orderBy;
		Page<Course> courseList = courseSvc.findCourseByCourseStstus((byte)4, currentPage - 1, sortField);
		if(member!=null) {
			for(Course course : courseList) {
				course.setSaved(courseSvc
						.isCourseInBookmark(member.getMemberId(), course.getCourseId()));
			}
			model.addAttribute("memberName", member.getName());
		}
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("courseList", courseList);
		model.addAttribute("totalPages", courseList.getTotalPages());
		if(orderBy != null)
			model.addAttribute("orderBy", orderBy);

		return "front-end/member/course/selectCourse";
	}
	
	@GetMapping("/my_course_order")
	public String memberSelectCourseOrder(
			@RequestParam(defaultValue = "1") Integer page,
			@ModelAttribute("member") Member member,
			ModelMap model, HttpSession session) {
		if (page < 1)  page = 1;
		Integer currentPage = page;		
		Page<CourseOrder> allMyCourseOrder = courseOrderSvc.getOrdersByMember(member, currentPage - 1);
		model.addAttribute("allMyCourseOrder", allMyCourseOrder);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", allMyCourseOrder.getTotalPages());
		
		return "front-end/member/course/allMyCourseOrder";
	}
	@GetMapping("/myOrder/detail/{orderId}")
	public String memberOrderDetail(
			@PathVariable("orderId") Integer orderId,
			@RequestParam(value = "returnUrl", required = false) String returnUrl,
			ModelMap model,
			RedirectAttributes redirectAttributes) {
		List<OrderDetail> details = orderDetailSvc.getOrderDetailsByCourseOrderId(orderId);
		
		redirectAttributes.addFlashAttribute("details", details);
		redirectAttributes.addFlashAttribute("detailsMsg", "show");
		
		
		return "redirect:/member/course/my_course_order";
	}
	@GetMapping("/get_one_course")
	public String memberGetOneCourse(
			@RequestParam("courseId") Integer courseId,
			@ModelAttribute("member") Member member,ModelMap model) {
		Course course = courseSvc.getOneCourse(courseId);
		course.setSaved(courseSvc
				.isCourseInBookmark(member.getMemberId(), course.getCourseId()));
		boolean coursePermission = orderDetailSvc.hasCoursePermission(member.getMemberId(), courseId);
		String cartMsg = orderDetailSvc.addCourseToCart(member.getMemberId(), courseId);
		if(cartMsg != "")
			model.addAttribute("cartMsg", cartMsg);
		model.addAttribute("coursePermission", coursePermission);
		model.addAttribute("course", course);
		return "front-end/member/course/listOneCourse";
	}
//	@GetMapping("/one_my_course/{courseId}")
//	public String oneMyCourse(
//			@PathVariable("courseId") Integer courseId,
//			@ModelAttribute("member") Member member,ModelMap model) {
//		Course course = courseSvc.getOneCourse(courseId);
//		course.setSaved(courseSvc
//				.isCourseInBookmark(member.getMemberId(), course.getCourseId()));
//		boolean coursePermission = orderDetailSvc.hasCoursePermission(member.getMemberId(), courseId);
//		model.addAttribute("course", course);
//		model.addAttribute("coursePermission", coursePermission);
//		return "front-end/member/course/listOneCourse";
//	}
	
	@GetMapping("/search-by-category")
	public String searchCourseByCategory(
	        @RequestParam Integer courseCatId,
	        @RequestParam(defaultValue = "1") Integer page,
	        @RequestParam(defaultValue = "courseIdDesc") String orderBy,
	        ModelMap model) {

	    if (page == null || page < 1) {
	        page = 1;
	    }

	    Page<Course> coursePage =
	            courseSvc.findListedCoursesByCategory(
	                    courseCatId,
	                    page - 1,
	                    orderBy
	            );

	    model.addAttribute("courseList", coursePage);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", coursePage.getTotalPages());

	    model.addAttribute("courseCatId", courseCatId);
	    model.addAttribute("orderBy", orderBy);

	    return "front-end/member/course/selectCourse";
	}
	
}
