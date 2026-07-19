package com.freemind.course.course.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.course.course.model.Course;
import com.freemind.course.course.model.CourseCategories;
import com.freemind.course.course.model.CourseCategoriesService;
import com.freemind.course.course.model.CourseQaComment;
import com.freemind.course.course.model.CourseQaCommentService;
import com.freemind.course.course.model.CourseService;
import com.freemind.course.dto.PlaybackPositionReq;
import com.freemind.course.order.model.CourseOrder;
import com.freemind.course.order.model.CourseOrderService;
import com.freemind.course.order.model.OrderDetail;
import com.freemind.course.order.model.OrderDetailService;
import com.freemind.course.order.model.ShoppingCartRedisService;
import com.freemind.course.util.CourseSpecification;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;
import com.freemind.login.notice.service.NoticeService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/member/course")
public class CourseForMemberController {

	private final CourseService courseSvc;
	private final MemberService memberSvc;
	private final NoticeService noticeSvc;
	private final OrderDetailService orderDetailSvc;
	private final CourseOrderService courseOrderSvc;
	private final ShoppingCartRedisService shoppingCartSvc;
	private final CourseQaCommentService commentService;
	private final CourseCategoriesService courseCategoriesSvc;
	
	public CourseForMemberController(
			CourseService courseSvc, 
			MemberService memberSvc,
			NoticeService noticeSvc,
			CourseOrderService courseOrderSvc,
			OrderDetailService orderDetailSvc,
			ShoppingCartRedisService shoppingCartSvc,
			CourseQaCommentService commentService,
			CourseCategoriesService courseCategoriesSvc) {
		this.courseSvc = courseSvc;
		this.memberSvc = memberSvc;
		this.noticeSvc = noticeSvc;
		this.courseOrderSvc = courseOrderSvc;
		this.orderDetailSvc = orderDetailSvc;
		this.commentService = commentService;
		this.shoppingCartSvc = shoppingCartSvc;
		this.courseCategoriesSvc = courseCategoriesSvc;
	}
	
//	@ModelAttribute("member")
//    public Member currentMember(Authentication authentication) {
//        // 訪客（未登入或匿名）時不放 member 進 model
//        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
//            return null;
//        }
//        return memberSvc.findByAccount(authentication.getName());
//    }
//	@ModelAttribute("countMemberUnread")
//	public Long memberNotice(ModelMap model) {
//		Member member = (Member)model.getAttribute("member");
//		return (member != null ? noticeSvc.countMemberUnread(member.getMemberId()) : null);
//	}
//	
//	@ModelAttribute("countMemberCartCount")
//	public Long memberShoppingCartCount(ModelMap model) {
//		Member member = (Member)model.getAttribute("member");
//System.out.println(member.getName());
//System.out.println((member != null ? shoppingCartSvc.getCourseCount(member.getMemberId()) : null));
//		return (member != null ? shoppingCartSvc.getCourseCount(member.getMemberId()) : null);
//	}
	
	@ModelAttribute
	public void addMemberAttributes(
	        Authentication authentication,
	        ModelMap model) {

	    // 訪客的預設資料
	    model.addAttribute("member", null);
	    model.addAttribute("countMemberUnread", 0L);
	    model.addAttribute("countMemberCartCount", 0L);

	    // 未登入或匿名使用者
	    if (authentication == null
	            || authentication instanceof AnonymousAuthenticationToken
	            || !authentication.isAuthenticated()) {
	        return;
	    }
	    Member member =
	            memberSvc.findByAccount(authentication.getName());
	    if (member == null) {
	        System.out.println("找不到對應會員資料");
	        return;
	    }
	    Long unreadCount =
	            noticeSvc.countMemberUnread(member.getMemberId());
	    Long cartCount =
	            shoppingCartSvc.getCourseCount(member.getMemberId());
	    model.addAttribute("member", member);
	    model.addAttribute(
	            "countMemberUnread",
	            unreadCount != null ? unreadCount : 0L
	    );
	    model.addAttribute(
	            "countMemberCartCount",
	            cartCount != null ? cartCount : 0L
	    );

//	    System.out.println("會員名稱：" + member.getName());
//	    System.out.println("購物車數量：" + cartCount);
	}
	
    @ModelAttribute("courseCategoriesListAll")
	public List<CourseCategories> courseCategoriesListAll(){
		List<CourseCategories> courseCategoriesListAll = courseCategoriesSvc.getAllCourseCategories();
		return courseCategoriesListAll;
	}
	
    // 全部課程相關
	@GetMapping("/select_course")
	public String memberSelectCourse(
			@RequestParam(defaultValue = "1") Integer page,
			@RequestParam(required = false) String keyword,
			@ModelAttribute("condition") CourseSpecification condition,
			@RequestParam(name = "orderBy", required = false) String orderBy,
			ModelMap model, HttpSession session) {
		Member member = (Member)model.getAttribute("member");
		if (page < 1)  page = 1;
		Integer currentPage = page;
		String sortField = (orderBy == null || orderBy.isBlank()) ? "courseId" : orderBy;
//		Page<Course> courseList = courseSvc.findCourseByCourseStstus((byte)4, currentPage - 1, sortField);
		Page<Course> courseList = courseSvc.searchListedCourses(keyword, currentPage-1, sortField);
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
	
	// 我的課程相關
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
	
	// 某一課程相關
	@GetMapping("/get_one_course/{courseId}")
	public String memberGetOneCourse(
			@PathVariable("courseId") Integer courseId,
			@ModelAttribute("member") Member member,ModelMap model) {
		Course course = courseSvc.getOneCourse(courseId);
		course.setSaved(courseSvc
				.isCourseInBookmark(member.getMemberId(), course.getCourseId()));
		boolean coursePermission = orderDetailSvc.hasCoursePermission(member.getMemberId(), courseId);
		if(coursePermission) {
			OrderDetail item = orderDetailSvc.getAccessibleOrderDetail(courseId, member);
			model.addAttribute("orderDetail", item);
		}
		model.addAttribute("memberId", member.getMemberId());
		model.addAttribute("coursePermission", coursePermission);
		model.addAttribute("course", course);
		return "front-end/member/course/listOneCourse";
	}
	
	@ResponseBody
	@PostMapping("/playback-position")
	public ResponseEntity<Void> updatePlaybackPosition(
	        @RequestBody PlaybackPositionReq request,
	        @ModelAttribute("member") Member member) {

	    System.out.println("已進入播放位置 Controller");
	    System.out.println("收到的 request：" + request);

	    if (member == null || member.getMemberId() == null) {

	        System.out.println("找不到登入會員");

	        return ResponseEntity
	                .status(HttpStatus.UNAUTHORIZED)
	                .build();
	    }

	    Integer memberId = member.getMemberId();

	    System.out.println("會員編號：" + memberId);
	    System.out.println(
	            "訂單編號：" + request.courseOrderId()
	    );
	    System.out.println(
	            "課程編號：" + request.courseId()
	    );
	    System.out.println(
	            "播放秒數：" + request.playbackSeconds()
	    );
	    BigDecimal progress = request.playbackPercentage();

	    if (progress == null) {
	        progress = BigDecimal.ZERO;
	    }

	    progress = progress
	            .max(BigDecimal.ZERO)
	            .min(new BigDecimal("100"))
	            .setScale(2, RoundingMode.HALF_UP);

	    orderDetailSvc.updatePlaybackPosition(
	            memberId,
	            request.courseOrderId(),
	            request.courseId(),
	            request.playbackSeconds(),
	            progress
	    );

	    orderDetailSvc.updatePlaybackPosition(
	            memberId,
	            request.courseOrderId(),
	            request.courseId(),
	            request.playbackSeconds(),
	            progress
	    );

	    return ResponseEntity.noContent().build();
	}
	
	
	@GetMapping("/get_all_qa/{courseId}")
	public String CourseAllQa(
			@PathVariable("courseId") Integer courseId,
			@ModelAttribute("member") Member member,
			RedirectAttributes redirectAttributes) {
		List<CourseQaComment> comments = commentService.getAllCourseQaByCourseId(courseId);
		redirectAttributes.addFlashAttribute("comments", comments);
		redirectAttributes.addFlashAttribute("courseId", courseId);
		return "redirect:/member/course/get_one_course/{courseId}";
	}
	
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
