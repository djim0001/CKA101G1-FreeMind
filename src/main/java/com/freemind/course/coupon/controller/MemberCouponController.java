package com.freemind.course.coupon.controller;

import java.util.List;

import org.springframework.data.domain.Page;
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
import com.freemind.course.order.model.CartItemDTO;
import com.freemind.course.order.model.ShoppingCartRedisService;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/coupon/member")
public class MemberCouponController {
	
private final MemberCouponService memCouponSvc;
private final MemberService memberSvc;
private final ShoppingCartRedisService ShoppingCartRedisSvc;
	
	public MemberCouponController(
			ShoppingCartRedisService ShoppingCartRedisSvc,
			MemberService memberSvc,
			MemberCouponService memCouponSvc) {
		this.ShoppingCartRedisSvc = ShoppingCartRedisSvc;
		this.memCouponSvc = memCouponSvc;
		this.memberSvc = memberSvc;
	}

	@GetMapping("/my_coupon")
	public String myCoupon(
			@RequestParam(defaultValue = "1") Integer page,
			@SessionAttribute(name = "memberId", required = false) Integer memberId,
			ModelMap model, HttpSession session) {
		
		if (page < 1)  page = 1;
		Integer currentPage = page;
		Member member = memberSvc.getOneMember(memberId);
		Page<MemberCoupon> myCoupons = memCouponSvc.getMyCoupons(member, currentPage - 1);
		
		
		
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("myCoupons", myCoupons);
		model.addAttribute("totalPages", myCoupons.getTotalPages());
		
		return "front-end/member/course/myCoupon";
	}
	
	@PostMapping("/coupon_model_box")
	public String couponModelBox(ModelMap model, HttpSession session,
			@SessionAttribute(name = "memberId", required = false) Integer memberId) {
		if(memberId == null) {
			model.addAttribute("mError", "請先登入");
			return "front-end/member/course/selectCourse";
		}
		Member member = memberSvc.getOneMember(memberId);
		List<MemberCoupon> memCoupons = memCouponSvc.getAllMyValidCoupon(member);
		MemberCoupon orderCoupon = (MemberCoupon) session.getAttribute("orderCoupon");
		List<CartItemDTO> cartList = ShoppingCartRedisSvc.getCartCartItemDTOs(memberId);
		Integer cartTotal = null;
		if(!cartList.isEmpty())
			cartTotal = ShoppingCartRedisSvc.calculateCartTotal(cartList);
	    
		model.addAttribute("memCoupons", memCoupons);
		model.addAttribute("chooseCouponMsg", "show");
		model.addAttribute("orderCoupon", orderCoupon);
		model.addAttribute("cartList", cartList);
		model.addAttribute("cartTotal", cartTotal);
		return "front-end/member/course/shoppingCartCheckOut";
	}
	
	@PostMapping("/choose_coupon")
	public String chooseCoupon(
	        @RequestParam("couponSerialNo") Integer couponSerialNo,
	        @SessionAttribute(name = "memberId", required = false) Integer memberId,
	        HttpSession session,
	        RedirectAttributes redirectAttributes) {

	    if (memberId == null) {
	        redirectAttributes.addFlashAttribute("mError", "請先登入");
	        return "redirect:/course/member/select_course";
	    }

	    MemberCoupon memberCoupon =
	            memCouponSvc.getOneByPK(couponSerialNo);

	    if (memberCoupon == null) {
	        redirectAttributes.addFlashAttribute("mError", "找不到此優惠券");
	        return "redirect:/course/member/goto_checkout";
	    }

	    if (!memberCoupon.getMember().getName().equals(memberId)) {
	        redirectAttributes.addFlashAttribute("mError", "此優惠券不屬於目前會員");
	        return "redirect:/course/member/goto_checkout";
	    }

	    session.setAttribute("orderCoupon", memberCoupon);

	    redirectAttributes.addFlashAttribute("successMsg", "已選擇優惠券");

	    return "redirect:/course/member/goto_checkout";
	}
	@PostMapping("/remove_coupon")
	public String removeCoupon(
	        HttpSession session,
	        RedirectAttributes redirectAttributes) {

	    session.removeAttribute("orderCoupon");

	    redirectAttributes.addFlashAttribute("successMsg", "已取消優惠券");

	    return "redirect:/course/member/goto_checkout";
	}
	
}
