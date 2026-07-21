package com.freemind.course.order.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.course.coupon.model.MemberCoupon;
import com.freemind.course.coupon.model.MemberCouponService;
import com.freemind.course.course.model.CourseService;
import com.freemind.course.dto.CartItemDTO;
import com.freemind.course.order.model.CourseOrder;
import com.freemind.course.order.model.CourseOrderService;
import com.freemind.course.order.model.OrderDetail;
import com.freemind.course.order.model.OrderDetail.CompositeOrderDetail;
import com.freemind.course.order.model.OrderDetailService;
import com.freemind.course.order.model.ShoppingCartRedisService;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;
import com.freemind.login.notice.service.NoticeService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/member/course")
public class ShoppingCartController {
	
	private final ShoppingCartRedisService ShoppingCartRedisSvc;
	private final MemberService memberSvc;
	private final CourseService courseSvc;
	private final CourseOrderService courseOrderSvc;
	private final OrderDetailService orderDetailSvc;
	private final MemberCouponService memberCouponSvc;
	private final NoticeService noticeSvc;
	
	public ShoppingCartController(
			ShoppingCartRedisService ShoppingCartRedisSvc, 
			MemberCouponService memberCouponSvc,
			CourseOrderService courseOrderSvc,
			OrderDetailService orderDetailSvc,
			NoticeService noticeSvc,
			CourseService courseSvc,
			MemberService memberSvc) {
		this.ShoppingCartRedisSvc = ShoppingCartRedisSvc;
		this.memberCouponSvc = memberCouponSvc;
		this.courseOrderSvc = courseOrderSvc;
		this.orderDetailSvc = orderDetailSvc;
		this.noticeSvc = noticeSvc;
		this.courseSvc = courseSvc;
		this.memberSvc = memberSvc;
	}
	
	@ModelAttribute("member")
    public Member currentMember(Authentication authentication) {
        return memberSvc.findByAccount(authentication.getName());
    }
	
	@ModelAttribute("countMemberUnread")
	public Long memberNotice(ModelMap model) {
		Member member = (Member)model.getAttribute("member");
		return (member != null ? noticeSvc.countMemberUnread(member.getMemberId()) : null);
	}
	
	@ModelAttribute("countMemberCartCount")
	public Long memberShoppingCartCount(ModelMap model) {
		Member member = (Member)model.getAttribute("member");
		return (member != null ? ShoppingCartRedisSvc.getCourseCount(member.getMemberId()) : null);
	}
	
	@GetMapping("/shopping_cart")
	public String shoppingCart(ModelMap model, 
			@ModelAttribute("member") Member member
			) {
			
		List<CartItemDTO> cartList = ShoppingCartRedisSvc.getCartCartItemDTOs(member.getMemberId());
		Integer cartTotal = ShoppingCartRedisSvc.calculateCartTotal(cartList);
		model.addAttribute("cartList", cartList);
		model.addAttribute("cartTotal", cartTotal);
		
		return "front-end/member/course/shoppingCart";
	}
	
	@GetMapping("/goto_checkout")
	public String goToCheckout(ModelMap model, RedirectAttributes redirectAttributes,
			@ModelAttribute("member") Member member,
			HttpSession session) {
		
		List<CartItemDTO> cartList = ShoppingCartRedisSvc.getCartCartItemDTOs(member.getMemberId());
		if(cartList.isEmpty()){
			redirectAttributes.addFlashAttribute("cartMsg", "請先加入課程至購物車");
			return "redirect:/member/course/shopping_cart";
		}
		Integer cartTotal = ShoppingCartRedisSvc.calculateCartTotal(cartList);
		MemberCoupon orderCoupon = (MemberCoupon) session.getAttribute("orderCoupon");
		if (orderCoupon != null
				&& orderCoupon.getMember().getMemberId() == member.getMemberId()) {
			model.addAttribute("couponName", orderCoupon.getCoupon().getCouponName());
			cartTotal -= (Integer)session.getAttribute("discountLimitTotal");
//			cartTotal = BigDecimal.valueOf(cartTotal)
//						.multiply(orderCoupon.getCoupon().getDiscount())
//						.intValue();
			model.addAttribute("orderCoupon", orderCoupon);
		}else {
			session.removeAttribute("orderCoupon");
		}
		if(cartList.isEmpty()){
			redirectAttributes.addFlashAttribute("mError", "請先加入課程至購物車");
			return "redirect:/member/course/select_course";
		}
		model.addAttribute("cartList", cartList);
		model.addAttribute("cartTotal", cartTotal);
		
		return "front-end/member/course/shoppingCartCheckOut";
	}
	
	
	
	@PostMapping("/add_cart")
	public String addCart(
			@RequestParam("courseId") Integer courseId, 
			@ModelAttribute("member") Member member,
	        @RequestParam(value = "returnUrl", required = false) String returnUrl,
			RedirectAttributes redirectAttributes) {
		Integer memberId = member.getMemberId();
		String cartMsg = orderDetailSvc.canCourseToCart(memberId, courseId);
		boolean cart = ShoppingCartRedisSvc.isCourseInCart(memberId, courseId);
		if (cartMsg == "") 
			cartMsg = cart ? "此課程已在購物車中" : "成功加入購物車!";
		if(cartMsg == "成功加入購物車!") 
			ShoppingCartRedisSvc.addCourse(memberId, courseId);
		
		 redirectAttributes.addFlashAttribute("cartMsg", cartMsg);
		 redirectAttributes.addFlashAttribute("courseId", courseId);

		 return "redirect:" + returnUrl;
	}
	
	@PostMapping("/cart_remove")
	public String cartRemove(
			@RequestParam("courseId") Integer courseId, 
			@ModelAttribute("member") Member member,
			@RequestParam(value = "returnUrl", required = false) String returnUrl,
			RedirectAttributes redirectAttributes) {
		ShoppingCartRedisSvc.removeCourse(member.getMemberId(), courseId);
		
//		return "redirect:/member/course/shopping_cart";
		return "redirect:" + returnUrl;
	}
	
	@PostMapping("/cart_clear")
	public String cartClear(
			@ModelAttribute("member") Member member,
			RedirectAttributes redirectAttributes) {
		ShoppingCartRedisSvc.clearCart(member.getMemberId());
		
		return "redirect:/member/course/shopping_cart";
	}
	
	@PostMapping("/checkout")
	public String checkout(HttpSession session,
			@RequestParam(name = "paymentMethod", required = false) Integer paymentMethod,
			@ModelAttribute("member") Member member,
			RedirectAttributes redirectAttributes) {
		if(paymentMethod == null) {
			System.out.print("無支付方式");
			redirectAttributes.addFlashAttribute("paymentMethodMsg", "請選擇支付方式");
			return "redirect:/member/course/goto_checkout";
		}
		CourseOrder courseOrder = new CourseOrder();
		List<CartItemDTO> cartList = ShoppingCartRedisSvc.getCartCartItemDTOs(member.getMemberId());
		if(cartList.isEmpty()){
			redirectAttributes.addFlashAttribute("mError", "請先加入課程至購物車");
			return "redirect:/member/course/select_course";
		}
		Integer cartTotal = ShoppingCartRedisSvc.calculateCartTotal(cartList);
		
		MemberCoupon orderCoupon = (MemberCoupon) session.getAttribute("orderCoupon");
		Integer total = cartTotal;
		
		if(total <= 0) {
			return "redirect:/member/course/select_course";
		}
		
		Integer discountAmount = 0;
		if (orderCoupon != null) {
			courseOrder.setMemberCoupon(orderCoupon);
			Integer discountLimit = orderCoupon.getCoupon().getDiscountLimit();
			total = BigDecimal.valueOf(cartTotal)
					.multiply(orderCoupon.getCoupon().getDiscount())
					.intValue();
			discountAmount = discountLimit > (cartTotal - total) 
								? (cartTotal - total) : discountLimit;
			orderCoupon.setCouponStatus((byte)1);
			memberCouponSvc.updateCoupon(orderCoupon);
			session.removeAttribute("orderCoupon");
		}
		courseOrder.setMember(member);
		courseOrder.setOrderTotal(cartTotal);
		courseOrder.setDiscountAmount(discountAmount);
		courseOrder.setNetAmount(cartTotal - discountAmount);
		courseOrder.setPaymentMethod(paymentMethod);
		courseOrder.setOrderedAt(LocalDateTime.now());
		courseOrderSvc.addOrder(courseOrder);
		
		for(CartItemDTO item : cartList) {
			OrderDetail orderDetail = new OrderDetail();
			CompositeOrderDetail orderDetailId = new CompositeOrderDetail(
							courseOrder.getCourseOrderId(), item.getCourseId());
			orderDetail.setCompositeOrderDetail(orderDetailId);
			orderDetail.setCourse(courseSvc.getOneCourse(item.getCourseId()));
			orderDetail.setCourseOrder(courseOrder);
			Integer price = item.getPrice(),
					discountedPrice = BigDecimal.valueOf(price)
					.multiply(item.getPsychDiscount())
					.intValue();
			orderDetail.setPrice(price);
			orderDetail.setDiscountedPrice(discountedPrice);
			orderDetailSvc.addOrderDetail(orderDetail);
		}
		
		ShoppingCartRedisSvc.clearCart(member.getMemberId());
		redirectAttributes.addFlashAttribute("cartMsg", "結帳成功");
		
		return "redirect:/member/course/select_course";
	}
	
	private String safeRedirectUrl(String returnUrl) {
	    if (returnUrl == null || returnUrl.isBlank()) {
	        return "/course/memberSelectCourse";
	    }

	    // 只允許站內路徑，例如 /course/memberSelectCourse?page=2
	    if (!returnUrl.startsWith("/")) {
	        return "/course/member/select_course";
	    }

	    // 避免 //evil.com 這種瀏覽器可能解讀成外部網址的寫法
	    if (returnUrl.startsWith("//")) {
	        return "/course/memberselect_course";
	    }
	    
	    if (returnUrl.startsWith("/course/memberSelectCourse")
	            || returnUrl.startsWith("/course/member/get_one_course")) {
	        return returnUrl;
	    }

	    return "/course/member/select_course";
	}
}
