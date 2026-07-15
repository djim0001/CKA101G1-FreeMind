package com.freemind.course.coupon.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.course.coupon.model.Coupon;
import com.freemind.course.coupon.model.CouponService;
import com.freemind.course.coupon.model.MemberCoupon;
import com.freemind.course.coupon.model.MemberCouponService;
import com.freemind.course.dto.CartItemDTO;
import com.freemind.course.dto.CouponClaimResult;
import com.freemind.course.order.model.ShoppingCartRedisService;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/member/coupon")
public class MemberCouponController {
	
private final MemberCouponService memCouponSvc;
private final CouponService couponSvc;
private final MemberService memberSvc;
private final ShoppingCartRedisService ShoppingCartRedisSvc;
	
	public MemberCouponController(
			ShoppingCartRedisService ShoppingCartRedisSvc,
			MemberService memberSvc,
			CouponService couponSvc,
			MemberCouponService memCouponSvc) {
		this.ShoppingCartRedisSvc = ShoppingCartRedisSvc;
		this.memCouponSvc = memCouponSvc;
		this.memberSvc = memberSvc;
		this.couponSvc = couponSvc;
	}

	@ModelAttribute("member")
    public Member currentMember(Authentication authentication) {
        return memberSvc.findByAccount(authentication.getName());
    }
	
	@GetMapping("/my_coupon")
	public String myCoupon(
			@RequestParam(defaultValue = "1") Integer page,
			@ModelAttribute("member") Member member,
			ModelMap model, HttpSession session) {
		
		if (page < 1)  page = 1;
		Integer currentPage = page;
		Page<MemberCoupon> myCoupons = memCouponSvc.getMyCoupons(member, currentPage - 1);
		List<Coupon> coupons = memCouponSvc.getAvailableCoupons();
		
		model.addAttribute("currentPage", currentPage);
		model.addAttribute("myCoupons", myCoupons);
		model.addAttribute("coupons", coupons);
		model.addAttribute("totalPages", myCoupons.getTotalPages());
		
		return "front-end/member/course/myCoupon";
	}
	
	 @PostMapping("/claim")
	    public String claimCoupon(
	            @RequestParam Integer couponId,
	            @ModelAttribute("member") Member member,
	            RedirectAttributes redirectAttributes) {

	        Integer memberId = member.getMemberId();

	        CouponClaimResult result =
	        		memCouponSvc.claimCoupon(
	                memberId,
	                couponId
	            );

	        switch (result) {
	            case SUCCESS ->
	                redirectAttributes.addFlashAttribute(
	                    "couponMsg",
	                    "領取成功"
	                );

	            case SOLD_OUT ->
	                redirectAttributes.addFlashAttribute(
	                    "couponMsg",
	                    "優惠券已領完"
	                );

	            case ALREADY_CLAIMED ->
	                redirectAttributes.addFlashAttribute(
	                    "couponMsg",
	                    "你已經領取過這張優惠券"
	                );

	            case NOT_PUBLISHED ->
	                redirectAttributes.addFlashAttribute(
	                    "couponMsg",
	                    "優惠券尚未發布或已過期"
	                );
	        }

	        return "redirect:/member/coupon/my_coupon";
	    }
	 
	@PostMapping("/coupon_model_box")
	public String couponModelBox(ModelMap model, HttpSession session,
			@RequestParam(value = "returnUrl", required = false) String returnUrl,
			RedirectAttributes redirectAttributes,
			@ModelAttribute("member") Member member) {
		
		List<MemberCoupon> memCoupons = memCouponSvc.getAllMyValidCoupon(member);
		MemberCoupon orderCoupon = (MemberCoupon) session.getAttribute("orderCoupon");
		List<CartItemDTO> cartList = ShoppingCartRedisSvc.getCartCartItemDTOs(member.getMemberId());
		Integer cartTotal = null;
		if(!cartList.isEmpty())
			cartTotal = ShoppingCartRedisSvc.calculateCartTotal(cartList);
	    
		redirectAttributes.addFlashAttribute("memCoupons", memCoupons);
		redirectAttributes.addFlashAttribute("chooseCouponMsg", "show");
//		model.addAttribute("memCoupons", memCoupons);
//		model.addAttribute("chooseCouponMsg", "show");
		model.addAttribute("orderCoupon", orderCoupon);
		model.addAttribute("cartList", cartList);
		model.addAttribute("cartTotal", cartTotal);
//		return "front-end/member/course/shoppingCartCheckOut";
		return "redirect:" + returnUrl;
	}
	
	@PostMapping("/choose_coupon")
	public String chooseCoupon(
	        @RequestParam("couponSerialNo") Integer couponSerialNo,
	        @ModelAttribute("member") Member member,
	        HttpSession session,
	        RedirectAttributes redirectAttributes) {
	    MemberCoupon memberCoupon =
	            memCouponSvc.getOneByPK(couponSerialNo);

	    if (memberCoupon == null) {
	        redirectAttributes.addFlashAttribute("couponError", "找不到此優惠券");
	        return "redirect:/member/course/goto_checkout";
	    }
	    if (!(memberCoupon.getMember().getMemberId() == member.getMemberId())) {
	        redirectAttributes.addFlashAttribute("couponError", "此優惠券不屬於目前會員");
	        return "redirect:/member/course/goto_checkout";
	    }

	    session.setAttribute("orderCoupon", memberCoupon);

	    redirectAttributes.addFlashAttribute("successMsg", "已選擇優惠券");

	    return "redirect:/member/course/goto_checkout";
	}
	@PostMapping("/remove_coupon")
	public String removeCoupon(
	        HttpSession session,
	        RedirectAttributes redirectAttributes) {

	    session.removeAttribute("orderCoupon");

	    redirectAttributes.addFlashAttribute("successMsg", "已取消優惠券");

	    return "redirect:/member/course/goto_checkout";
	}
	
}
