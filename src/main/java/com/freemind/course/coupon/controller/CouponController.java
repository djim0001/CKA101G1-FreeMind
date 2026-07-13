package com.freemind.course.coupon.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.course.coupon.model.Coupon;
import com.freemind.course.coupon.model.CouponService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin/coupon")
public class CouponController {

	private final CouponService couponSvc;

	public CouponController(CouponService couponSvc) {
		this.couponSvc = couponSvc;
	}

	@GetMapping("/select_coupon")
	public String selectCoupon(@RequestParam(defaultValue = "1") Integer page, ModelMap model, HttpSession session) {

		if (page < 1)
			page = 1;
		Integer currentPage = page;

		Page<Coupon> couponListAllPages = couponSvc.getCouponPage(currentPage - 1);

		model.addAttribute("currentPage", currentPage);
		model.addAttribute("couponListAllPages", couponListAllPages);
		model.addAttribute("totalPages", couponListAllPages.getTotalPages());

		return "back-end/course/coupon/selectCoupon";
	}

	@GetMapping("/add_coupon")
	public String addCoupon(ModelMap model) {
		Coupon coupon = new Coupon();
		model.addAttribute("coupon", coupon);
		return "back-end/course/coupon/addCoupon";
	}

	@PostMapping("/select_one_coupon")
	public String listOneCoupon(@RequestParam("couponId") String couponId, ModelMap model) {
		Coupon coupon = couponSvc.getOneCoupon(Integer.valueOf(couponId));
		model.addAttribute("coupon", coupon);
		return "back-end/course/coupon/listOneCoupon";
	}

	@PostMapping("/insert_coupon")
	public String insertCoupon(@Valid Coupon coupon, BindingResult result, ModelMap model) {

		if (result.hasErrors()) {
			return "back-end/course/coupon/addCoupon";
		}

		couponSvc.addCoupon(coupon);
		model.addAttribute("coupon", coupon);
		return "back-end/course/coupon/listOneCoupon";
	}

	@PostMapping("/update_coupon")
	public String updateCoupon(@RequestParam("couponId") String couponId, ModelMap model) {

		Coupon coupon = couponSvc.getOneCoupon(Integer.valueOf(couponId));

		model.addAttribute("coupon", coupon);
		return "back-end/course/coupon/updateCoupon";
	}

	@PostMapping("/update")
	public String update(@Valid Coupon coupon, BindingResult result, ModelMap model) {

		if (result.hasErrors()) {
			return "back-end/course/coupon/updateCoupon";
		}
		couponSvc.updateCoupon(coupon);

		model.addAttribute("success", "修改成功");
		coupon = couponSvc.getOneCoupon(Integer.valueOf(coupon.getCouponId()));
		model.addAttribute("coupon", coupon);
		return "back-end/course/coupon/listOneCoupon";
	}

	@PostMapping("/publish")
	public String publishCoupon(@RequestParam Integer couponId, @RequestParam Integer stock,
			@RequestParam Long ttlHours, RedirectAttributes redirectAttributes) {

		try {
			couponSvc.publishCoupon(couponId, stock, ttlHours);

			redirectAttributes.addFlashAttribute("couponMsg", "優惠券發布成功");

		} catch (IllegalArgumentException e) {

			redirectAttributes.addFlashAttribute("couponMsg", e.getMessage());
		}

		return "redirect:/admin/coupon/select_coupon";
	}

	@ModelAttribute("couponListAll")
	public List<Coupon> couponListAll() {
//		List<Coupon> couponListAll = couponSvc.getAllCoupon();
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
