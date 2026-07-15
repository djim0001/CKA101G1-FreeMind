package com.freemind.course.course.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.course.course.model.Course;
import com.freemind.course.course.model.CourseQaCommentService;
import com.freemind.course.course.model.CourseService;
import com.freemind.course.dto.ReviewDTO;
import com.freemind.course.order.model.CourseOrderService;
import com.freemind.course.order.model.OrderDetail;
import com.freemind.course.order.model.OrderDetailService;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;

import jakarta.validation.Valid;

@Controller      
@RequestMapping("/member/dashboard")
public class MemberDashboardCourseController {
	
	private final CourseService courseSvc;
	private final MemberService memberSvc;
	private final OrderDetailService orderDetailSvc;
	private final CourseQaCommentService commentService;
	public MemberDashboardCourseController(
			CourseService courseSvc, MemberService memberSvc,
			CourseOrderService courseOrderSvc,
			OrderDetailService orderDetailSvc,
			CourseQaCommentService commentService) {
		this.courseSvc = courseSvc;
		this.memberSvc = memberSvc;
		this.orderDetailSvc = orderDetailSvc;
		this.commentService = commentService;
	}
	
	@ModelAttribute("member")
    public Member currentMember(Authentication authentication) {
        return memberSvc.findByAccount(authentication.getName());
    }
	
	@GetMapping("/myCollection/course")
	public String courseTabs(
			@ModelAttribute("member") Member member,
			@RequestParam(defaultValue = "1") Integer page,
			ModelMap model) {
		if (page < 1)  page = 1;
		Integer currentPage = page;
		Page<Course> myBookmarks = courseSvc.getBookmarkCourses(member.getMemberId(), currentPage - 1);
		model.addAttribute("myBookmarks", myBookmarks);
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", myBookmarks.getTotalPages());
		return "front-end/member/course/myBookmarks";
	}
	
	@GetMapping("/mybookmarks/search")
	public String mybookmarksSearch(
	        @ModelAttribute("member") Member member,
	        @RequestParam(defaultValue = "1") Integer page,
	        @RequestParam(defaultValue = "") String keyword,
	        ModelMap model) {

	    if (page == null || page < 1) {
	        page = 1;
	    }

	    keyword = keyword == null ? "" : keyword.trim();

	    Page<Course> myBookmarks = courseSvc.getBookmarkCourses(
	            member.getMemberId(),
	            page,
	            keyword
	    );

	    model.addAttribute("myBookmarks", myBookmarks);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("totalPages", myBookmarks.getTotalPages());
	    model.addAttribute("keyword", keyword);

	    return "front-end/member/course/myBookmarks";
	}
	
	@GetMapping("/myLearning")
	public String myCourses(
			@ModelAttribute("member") Member member,
			@RequestParam(defaultValue = "1") Integer page,
			ModelMap model) {
		if (page < 1)  page = 1;
		Integer currentPage = page;
		
		Page<OrderDetail> myCoursePage =
                orderDetailSvc.getAccessibleOrderDetails(member, currentPage - 1);
		
		model.addAttribute("myCoursePage", myCoursePage);
		model.addAttribute("myCourses", myCoursePage.getContent());
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("totalPages", myCoursePage.getTotalPages());
		return "front-end/member/course/allMyCourse";
	}
	
	@PostMapping("/favorite/toggle")
	public String favoriteToggle(
			@ModelAttribute("member") Member member,
	        @RequestParam(name = "currentPage", required = false, defaultValue = "1") Integer page,
	        @RequestParam(name = "orderBy", required = false) String orderBy,
	        @RequestParam(name = "courseId") Integer courseId,
	        @RequestParam(value = "returnUrl", required = false) String returnUrl,
	        RedirectAttributes redirectAttributes) {
		Course course = courseSvc.getOneCourse(courseId);
		Integer saveCount = course.getSaveCount();
	    if (!courseSvc.isCourseInBookmark(member.getMemberId(), courseId)) {
	        courseSvc.addCourseBookmark(member.getMemberId(), courseId);
	        course.setSaveCount(saveCount + 1);
	    } else {
	        courseSvc.removeCourseBookmark(member.getMemberId(), courseId);
	        if(saveCount == 0) saveCount = 1;
	        course.setSaveCount(saveCount - 1);
	    }
	    courseSvc.updateCourse(course);

	    redirectAttributes.addFlashAttribute("page", page);
	    redirectAttributes.addFlashAttribute("orderBy", orderBy);
	    return "redirect:" + returnUrl;
	}
	
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
System.out.println("review+star" + course.getReviewCount() + course.getStarCount());
		course.setReviewCount(course.getReviewCount() + 1);
		course.setStarCount(course.getStarCount() + 1);
System.out.println("review+star" + course.getReviewCount() + course.getStarCount());
		courseSvc.updateCourse(course);
		
		return "redirect:" + returnUrl;
	}
}
