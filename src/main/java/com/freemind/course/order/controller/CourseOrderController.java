package com.freemind.course.order.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.freemind.course.coupon.model.MemberCoupon;
import com.freemind.course.coupon.model.MemberCouponService;
import com.freemind.course.course.model.Course;
import com.freemind.course.course.model.CourseQaCommentService;
import com.freemind.course.course.model.CourseService;
import com.freemind.course.dto.ReviewDTO;
import com.freemind.course.order.model.CourseOrder;
import com.freemind.course.order.model.CourseOrderService;
import com.freemind.course.order.model.OrderDetail;
import com.freemind.course.order.model.OrderDetailService;
import com.freemind.login.admin.model.Admin;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/member/course")
public class CourseOrderController {

	@Autowired
	private CourseOrderService courseOrderService;
	@Autowired
	private MemberService memberSvc;
	@Autowired
	private MemberCouponService memberCouponSvc;
	@Autowired
	private CourseService courseSvc;
	@Autowired
	private OrderDetailService orderDetailSvc;
	@Autowired
	private CourseQaCommentService commentService;

	@ModelAttribute("member")
	public Member currentMember(Authentication authentication) {
		return memberSvc.findByAccount(authentication.getName());
	}

//	@GetMapping("acb")
//	public String getOrderById(ModelMap model, @ModelAttribute("member") Member member) {
//
//		// 1. 從 Session 撈出登入時存進去的會員物件 (請確保跟您登入功能存的 Key 一致，這裡假設叫 "member")
////        Member loginMember = (Member) session.getAttribute("member"); 
//
//		Member mem = memberSvc.getOneMember(member.getMemberId());
//		// 安全檢查：如果沒登入，強制踢回登入頁
//		if (mem == null) {
//			return "redirect:/login";
//		}
//
//		// 2. 動態撈出「這個登入的人」的所有訂單
//		List<CourseOrder> orders = courseOrderService.getOrdersByMember(mem);
//
//		// 3. 把訂單清單交給 Thymeleaf
//		model.addAttribute("orders", orders);
//
//		return "front-end/member/course/CourseOrder";
//	}

//用「會員編號」查詢該會員的所有訂單，並跳轉到我的訂單網頁

//	@GetMapping("/member/memberId")
//	public String getOrdersByMemberId(@PathVariable Integer memberId, org.springframework.ui.Model model) {
//
//		Member member = new Member();
//		member.setMemberId(memberId);
//
//		// 呼叫 Service 撈出該會員的所有訂單列表
//		List<CourseOrder> orders = courseOrderService.getOrdersByMember(member);
//
//		// 將訂單列表塞入 model
//		model.addAttribute("orders", orders);
//		return "front-end/course/CourseOrder";
//	}

	@PostMapping("/order/pay_or_cancel")
	public String cancelOrder(
			@ModelAttribute("member") Member member,
			@RequestParam("courseOrderId") Integer courseOrderId,
			@RequestParam(value = "returnUrl", required = false) String returnUrl,
			@RequestParam(value = "pay", required = false) String pay,
			RedirectAttributes redirectAttributes,
			ModelMap model) {
		if(pay == null) pay = "";
		if(pay.equals("pay")) {
			try {
		        courseOrderService.paymentSuccess(courseOrderId);

		        redirectAttributes.addFlashAttribute(
		                "payMessage",
		                "付款成功，課程權限已開通"
		        );

		    } catch (RuntimeException e) {

		        redirectAttributes.addFlashAttribute(
		                "payMessage",
		                e.getMessage()
		        );
		    }
		}else {
			try {
				MemberCoupon memCoupon = 
						courseOrderService.getMemberCouponByOrderId(courseOrderId);
				if(memCoupon != null) {
					memCoupon.setCouponStatus((byte)0);
					memberCouponSvc.updateCoupon(memCoupon);
				}
				courseOrderService.cancelOrder(courseOrderId);
		        redirectAttributes.addFlashAttribute(
		                "payMessage",
		                "訂單已取消，課程已還原為可購買"
		        );

		    } catch (RuntimeException e) {
		        redirectAttributes.addFlashAttribute(
		                "payMessage",
		                e.getMessage()
		        );
		    }
		}
		return "redirect:" + returnUrl;
	}
	
//	@PostMapping("/order_search")
//	public String courseSearch(
//			@RequestParam("courseId") Integer courseId, 
//			@RequestParam("courseStatus") Byte courseStatus, 
//			@ModelAttribute("admin") Admin admin,
//			ModelMap model) {
////		Course course = courseSvc.getOneCourse(courseId);
////		course.setCourseStatus(courseStatus);
////		course.setAdmin(admin);
////		courseSvc.updateCourse(course);
////		model.addAttribute("course", course);
//		return "back-end/course/course/listOneCourse";
//		
//	}
	
	@PostMapping("/my_course_comment")
	public String memberCommentCourse(
			@RequestParam(value = "returnUrl", required = false) String returnUrl,
			@RequestParam(value = "courseQuestion", required = false) String courseQuestion,
			@RequestParam(value = "courseId", required = false) Integer courseId,
			@ModelAttribute("member") Member member,
			ModelMap model,
			RedirectAttributes redirectAttributes) {
		if(courseQuestion == null || courseQuestion.isBlank()) {
			redirectAttributes.addFlashAttribute(
	                "courseQuestionMsg",
	                "提問內容不能為空"
	        );
			return "redirect:" + returnUrl;
		}
		if(orderDetailSvc.hasCoursePermission(member.getMemberId(), courseId)) 
			commentService.addQuestion(courseSvc.getOneCourse(courseId), member, courseQuestion);
		else
			redirectAttributes.addFlashAttribute(
	                "courseQuestionMsg",
	                "您尚未購買此課程"
	        );
		return "redirect:" + returnUrl;
	}
	
	@GetMapping("/my_review")
	public String myReview(
			@ModelAttribute("member") Member member,
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(name = "orderBy", required = false) String orderBy,
			ModelMap model) {
		if (page < 1)  page = 1;
		Integer currentPage = page;
		Page<OrderDetail> myCoursePage =
                orderDetailSvc.getReviewCoursesByMemberId(member.getMemberId(), currentPage - 1);
		ReviewDTO review = new ReviewDTO();
		List<OrderDetail> unreviewedCourses =
		        orderDetailSvc.getReviewableCoursesByMemberId(member.getMemberId());
		model.addAttribute("unreviewedCourses", unreviewedCourses);
		model.addAttribute("review", review);
		model.addAttribute("myCoursePage", myCoursePage);
		model.addAttribute("myCourses", myCoursePage.getContent());
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", myCoursePage.getTotalPages());
		return "front-end/member/course/myReview";
	}
	
	@PostMapping("/review")
	public String memberReviewCourse(
			@RequestParam(value = "returnUrl", required = false) String returnUrl,
			@RequestParam(defaultValue = "1") Integer page,
			@Valid @ModelAttribute("review") ReviewDTO review, BindingResult result,
			@ModelAttribute("member") Member member,
			RedirectAttributes redirectAttributes,
			ModelMap model) {
		
		if (result.hasErrors()) {
	        List<OrderDetail> unreviewedCourses =
	                orderDetailSvc.getReviewableCoursesByMemberId(
	                        member.getMemberId()
	                );
	        if (page == null || page < 1) {
	            page = 1;
	        }
	        Integer currentPage = page;
	        Page<OrderDetail> myCoursePage =
	                orderDetailSvc.getReviewCoursesByMemberId(
	                        member.getMemberId(),
	                        currentPage - 1
	                );
	        model.addAttribute("reviewModalMsg", "show");

	        model.addAttribute("unreviewedCourses", unreviewedCourses);
	        model.addAttribute("myCoursePage", myCoursePage);
	        model.addAttribute("myCourses", myCoursePage.getContent());
	        model.addAttribute("currentPage", currentPage);
	        model.addAttribute("totalPages", myCoursePage.getTotalPages());

	        return "front-end/member/course/myReview";
	    }
		
		OrderDetail item = orderDetailSvc.getAccessibleOrderDetail(review.getCourseId(), member);
		item.setRating(review.getRating());
		item.setReviewContent(review.getReviewContent());
		item.setReviewedAt(LocalDateTime.now());
		orderDetailSvc.updateOrderDetail(item);
		
		Course course = courseSvc.getOneCourse(review.getCourseId());
		course.setReviewCount(course.getReviewCount() + 1);
		course.setStarCount(course.getStarCount() + 1);
		courseSvc.updateCourse(course);
		
		return "redirect:" + returnUrl;
	}

}
