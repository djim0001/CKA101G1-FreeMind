package com.freemind.course.coupon.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.course.coupon.model.Coupon;
import com.freemind.course.coupon.model.CouponService;
import com.freemind.login.admin.model.Admin;
import com.freemind.login.admin.model.AdminService;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;
import com.freemind.login.notice.service.NoticeService;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/coupon")
public class CouponController {

	private final CouponService couponSvc;
	private final AdminService adminSvc;
	private final MemberService memberSvc;
	private final NoticeService noticeSvc;

	public CouponController(
			AdminService adminSvc, 
			CouponService couponSvc,
			NoticeService noticeSvc,
			MemberService memberSvc) {
		this.couponSvc = couponSvc;
		this.adminSvc = adminSvc;
		this.memberSvc = memberSvc;
		this.noticeSvc = noticeSvc;
	}

	@ModelAttribute("admin")
	public Admin currentAdmin(Authentication authentication) {
		return adminSvc.findByAccount(authentication.getName());
	}
	
	@GetMapping("/select_coupon")
	public String selectCoupon(
			@RequestParam(defaultValue = "1") Integer page, 
			ModelMap model) {

		if (page < 1)
			page = 1;

		Page<Coupon> couponListAllPages = couponSvc.getCouponPage(page);
		Coupon coupon = new Coupon();
		
		model.addAttribute("coupon", coupon);
		model.addAttribute("currentPage", page);
		model.addAttribute("couponListAllPages", couponListAllPages);
		model.addAttribute("totalPages", couponListAllPages.getTotalPages());

		return "back-end/course/course/selectCoupon";
	}

//	@GetMapping("/add_coupon")
//	public String addCoupon(ModelMap model) {
//		Coupon coupon = new Coupon();
//		model.addAttribute("coupon", coupon);
//		return "back-end/course/course/addCoupon";
//	}
//
//	@PostMapping("/select_one_coupon")
//	public String listOneCoupon(
//			@RequestParam("couponId") String couponId, 
//			ModelMap model) {
//		Coupon coupon = couponSvc.getOneCoupon(Integer.valueOf(couponId));
//		model.addAttribute("coupon", coupon);
//		return "back-end/course/course/listOneCoupon";
//	}

	@PostMapping("/insert_coupon")
	public String insertCoupon(@Valid Coupon coupon, BindingResult result, ModelMap model) {

		if (result.hasErrors()) {
			return "back-end/course/course/selectCourse";
		}

		couponSvc.addCoupon(coupon);
		return "redirect:/admin/coupon/select_coupon";
	}

	@PostMapping("/publish")
	public String publishCoupon(
			@RequestParam Integer couponId, 
			@RequestParam Integer stock,
			@RequestParam Long ttlHours, 
			RedirectAttributes redirectAttributes) {

		try {
			couponSvc.publishCoupon(couponId, stock, ttlHours);

			redirectAttributes.addFlashAttribute("couponMsg", "優惠券發布成功");
			List<Member> allMember = memberSvc.getAll();
			List<Integer> memberIds = allMember.stream()
			        .map(Member::getMemberId)
			        .toList();
			noticeSvc.sendToMembers(memberIds, 0, "有新的優惠券發放了喔~~", (byte)1);

		} catch (IllegalArgumentException e) {

			redirectAttributes.addFlashAttribute("couponMsg", e.getMessage());
		}

		return "redirect:/admin/coupon/select_coupon";
	}

	@ModelAttribute("couponListAll")
	public List<Coupon> couponListAll() {
		return couponSvc.getAllCoupon();
	}

	@ExceptionHandler(value = { HandlerMethodValidationException.class, ConstraintViolationException.class })
	public ModelAndView handleError(Exception e, Model model) {

		StringBuilder strBuilder = new StringBuilder();

		if (e instanceof HandlerMethodValidationException ex) {

			ex.getParameterValidationResults().forEach(result -> {
				result.getResolvableErrors()
						.forEach(error -> strBuilder.append(error.getDefaultMessage()).append("<br>"));
			});
		} else if (e instanceof ConstraintViolationException ex) {

			ex.getConstraintViolations().forEach(violation -> strBuilder.append(violation.getMessage()).append("<br>"));
		}

		return new ModelAndView("back-end/course/coupon/addCoupon", "errorMessage",
				"請修正以下錯誤:<br>" + strBuilder.toString());
	}

}
