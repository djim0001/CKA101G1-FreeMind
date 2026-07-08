package com.freemind.course.order.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.course.coupon.model.MemberCoupon;
import com.freemind.course.coupon.model.MemberCouponService;
import com.freemind.course.course.model.CourseService;
import com.freemind.course.order.model.CartItemDTO;
import com.freemind.course.order.model.CourseOrder;
import com.freemind.course.order.model.CourseOrderService;
import com.freemind.course.order.model.OrderDetail;
import com.freemind.course.order.model.OrderDetail.CompositeOrderDetail;
import com.freemind.course.order.model.OrderDetailService;
import com.freemind.course.order.model.ShoppingCartRedisService;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/course/member")
public class ShoppingCartController {
	
	private final ShoppingCartRedisService ShoppingCartRedisSvc;
	private final MemberService memberSvc;
	private final CourseService courseSvc;
	private final CourseOrderService courseOrderSvc;
	private final OrderDetailService OrderDetailSvc;
	private final MemberCouponService memberCouponSvc;
	
	public ShoppingCartController(
			ShoppingCartRedisService ShoppingCartRedisSvc, 
			MemberCouponService memberCouponSvc,
			CourseOrderService courseOrderSvc,
			OrderDetailService OrderDetailSvc,
			CourseService courseSvc,
			MemberService memberSvc) {
		this.ShoppingCartRedisSvc = ShoppingCartRedisSvc;
		this.memberCouponSvc = memberCouponSvc;
		this.courseOrderSvc = courseOrderSvc;
		this.OrderDetailSvc = OrderDetailSvc;
		this.courseSvc = courseSvc;
		this.memberSvc = memberSvc;
	}
	
	
	@GetMapping("/shopping_cart")
	public String shoppingCart(ModelMap model, RedirectAttributes redirectAttributes,
			@SessionAttribute(name = "memberId", required = false) Integer memberId
			) {
		if(memberId == null) {
			redirectAttributes.addFlashAttribute("mError", "請先登入，方可查看購物車");
			return "redirect:/course/memberSelectCourse";
		}
			
		List<CartItemDTO> cartList = ShoppingCartRedisSvc.getCartCartItemDTOs(memberId);
		Integer cartTotal = ShoppingCartRedisSvc.calculateCartTotal(cartList);
		model.addAttribute("cartList", cartList);
		model.addAttribute("cartTotal", cartTotal);
		
		return "front-end/member/course/shoppingCart";
	}
	
	@GetMapping("/goto_checkout")
	public String goToCheckout(ModelMap model, RedirectAttributes redirectAttributes,
			@SessionAttribute(name = "memberId", required = false) Integer memberId,
			HttpSession session) {
		if(memberId == null) {
			redirectAttributes.addFlashAttribute("mError", "請先登入，方可前往結帳");
			return "redirect:/course/member/select_course";
		}
		
		List<CartItemDTO> cartList = ShoppingCartRedisSvc.getCartCartItemDTOs(memberId);
		if(cartList.isEmpty()){
			redirectAttributes.addFlashAttribute("mError", "請先加入課程至購物車");
			return "redirect:/course/member/select_course";
		}
		Integer cartTotal = ShoppingCartRedisSvc.calculateCartTotal(cartList);
		MemberCoupon orderCoupon = (MemberCoupon) session.getAttribute("orderCoupon");
		if (orderCoupon != null) {
			model.addAttribute("couponName", orderCoupon.getCoupon().getCouponName());
			cartTotal = BigDecimal.valueOf(cartTotal)
						.multiply(orderCoupon.getCoupon().getDiscount())
						.intValue();
		}
		model.addAttribute("cartList", cartList);
		model.addAttribute("cartTotal", cartTotal);
	    model.addAttribute("orderCoupon", orderCoupon);
		
		return "front-end/member/course/shoppingCartCheckOut";
	}
	
	
	
	@PostMapping("/add_cart")
	public String addCart(
			@RequestParam("courseId") Integer courseId, 
			@SessionAttribute(name = "memberId", required = false) Integer memberId, 
	        @RequestParam(value = "returnUrl", required = false) String returnUrl,
			RedirectAttributes redirectAttributes) {
		
		if(memberId == null) {
			redirectAttributes.addFlashAttribute("mError", "先登入方可加入購物車");
			return "redirect:/course/member/select_course";
		}
		
//		System.out.println("returnUrl = " + returnUrl);
		if (ShoppingCartRedisSvc.isCourseInCart(memberId, courseId)) {
		        redirectAttributes.addFlashAttribute("cartMsg", "此課程已在購物車中");
		} else {
			ShoppingCartRedisSvc.addCourse(memberId, courseId);
		    redirectAttributes.addFlashAttribute("cartMsg", "成功加入購物車!");
		}
		 redirectAttributes.addFlashAttribute("courseId", courseId);

		 return "redirect:" + returnUrl;
	}
	
	@PostMapping("/cart_remove")
	public String cartRemove(
			@RequestParam("courseId") Integer courseId, 
			@SessionAttribute(name = "memberId", required = false) Integer memberId, 
			RedirectAttributes redirectAttributes) {
		ShoppingCartRedisSvc.removeCourse(memberId, courseId);
		
		return "redirect:/course/member/shopping_cart";
	}
	
	@PostMapping("/cart_clear")
	public String cartClear(
			@SessionAttribute(name = "memberId", required = false) Integer memberId, 
			RedirectAttributes redirectAttributes) {
		ShoppingCartRedisSvc.clearCart(memberId);
		
		return "redirect:/course/member/shopping_cart";
	}
	
	@PostMapping("/checkout")
	public String checkout(HttpSession session,
			@RequestParam(name = "paymentMethod", required = false) Integer paymentMethod,
			@SessionAttribute(name = "memberId", required = false) Integer memberId, 
			RedirectAttributes redirectAttributes) {
		if(memberId == null) {
			redirectAttributes.addFlashAttribute("mError", "先登入方可加入購物車");
			return "redirect:/course/member/select_sourse";
		}
		if(paymentMethod == null) {
			System.out.print("無支付方式");
			redirectAttributes.addFlashAttribute("paymentMethodMsg", "請選擇支付方式");
			return "redirect:/course/member/goto_checkout";
		}
		Member member = memberSvc.getOneMember(memberId);
		CourseOrder courseOrder = new CourseOrder();
		List<CartItemDTO> cartList = ShoppingCartRedisSvc.getCartCartItemDTOs(memberId);
		if(cartList.isEmpty()){
			redirectAttributes.addFlashAttribute("mError", "請先加入課程至購物車");
			return "redirect:/course/member/select_course";
		}
		Integer cartTotal = ShoppingCartRedisSvc.calculateCartTotal(cartList);
		
		MemberCoupon orderCoupon = (MemberCoupon) session.getAttribute("orderCoupon");
		Integer total = cartTotal;
		if (orderCoupon != null) {
			courseOrder.setMemberCoupon(orderCoupon);
			total = BigDecimal.valueOf(cartTotal)
					.multiply(orderCoupon.getCoupon().getDiscount())
					.intValue();
			orderCoupon.setCouponStatus((byte)1);
			memberCouponSvc.updateCoupon(orderCoupon);
			session.removeAttribute("orderCoupon");
		}
		courseOrder.setMember(member);
		courseOrder.setOrderTotal(cartTotal);
		courseOrder.setDiscountAmount(cartTotal - total);
		courseOrder.setNetAmount(total);
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
			OrderDetailSvc.addOrderDetail(orderDetail);
		}
		
		ShoppingCartRedisSvc.clearCart(memberId);
		redirectAttributes.addFlashAttribute("cartMsg", "結帳成功");
		
		return "redirect:/course/member/select_course";
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
