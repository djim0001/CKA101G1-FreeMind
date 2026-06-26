package com.freemind.course.coupon.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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

import com.freemind.course.coupon.model.CouponService;
import com.freemind.course.coupon.model.CouponVO;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/coupon")
public class CouponController {
	
	@Autowired
	CouponService couponSvc;
	
	@GetMapping("selectCoupon")
	public String selectCoupon(
			@RequestParam(name = "empPageQty", required = false) String empPageQty, 
			ModelMap model) {
		
		Integer currentPage = (empPageQty == null) ? 1 : Integer.parseInt(empPageQty);
		model.addAttribute("empPageQty", currentPage);

		CouponVO couponVO = new CouponVO();
		model.addAttribute("couponVO", couponVO);
		return "back-end/course/coupon/selectCoupon";
	}

	@GetMapping("addCoupon")
	public String addCoupon(ModelMap model) {
		CouponVO couponVO = new CouponVO();
		model.addAttribute("couponVO", couponVO);
		return "back-end/course/coupon/addCoupon";
	}
	@PostMapping("select_one_coupon")
	public String listOneCoupon(@RequestParam("couponId") String couponId, ModelMap model) {
		CouponVO couponVO = couponSvc.getOneCoupon(Integer.valueOf(couponId));
		model.addAttribute("couponVO", couponVO);
		return "back-end/course/coupon/listOneCoupon";
	}
	
	@PostMapping("insert")
	public String insert(@Valid CouponVO couponVO, BindingResult result, ModelMap model){
		
		if(result.hasErrors()) {
			return "back-end/course/coupon/addCoupon";
		}
		
		couponSvc.addCoupon(couponVO);
		model.addAttribute("couponVO", couponVO);
		return "back-end/course/coupon/listOneCoupon";
	}
	
	@PostMapping("update_coupon")
	public String updateCoupon(@RequestParam("couponId") String couponId, ModelMap model) {
		
		CouponVO couponVO = couponSvc.getOneCoupon(Integer.valueOf(couponId));
		
		model.addAttribute("couponVO", couponVO);
		return "back-end/course/coupon/updateCoupon";
	}
	
	@PostMapping("update")
	public String update(@Valid CouponVO couponVO, BindingResult result, ModelMap model) {
		
		if(result.hasErrors()) {
			return "back-end/course/coupon/updateCoupon";
		}
		couponSvc.updateCoupon(couponVO);
		
		model.addAttribute("success", "修改成功");
		couponVO = couponSvc.getOneCoupon(Integer.valueOf(couponVO.getCouponId()));
		model.addAttribute("couponVO", couponVO);
		return "back-end/course/coupon/listOneCoupon";
	}	
	
	@ModelAttribute("couponListAll")
	public List<CouponVO> couponListAll(){
		List<CouponVO> couponListAll = couponSvc.getAll();
		return couponListAll;
	}
	
	
	
	@ExceptionHandler(value = { HandlerMethodValidationException.class, ConstraintViolationException.class })
	public ModelAndView handleError(Exception e, Model model) {
	    
	    StringBuilder strBuilder = new StringBuilder();
	       
	    if (e instanceof HandlerMethodValidationException ex) {
	            
	        ex.getParameterValidationResults().forEach(result -> {
	            result.getResolvableErrors().forEach(error ->
	                strBuilder.append(error.getDefaultMessage()).append("<br>")
	            );
	        });
	    } 	 
	    else if (e instanceof ConstraintViolationException ex) {
	            
	        ex.getConstraintViolations().forEach(violation -> 
	            strBuilder.append(violation.getMessage()).append("<br>")
	        );
	    }

	    return new ModelAndView("back-end/emp/select_page", "errorMessage", "請修正以下錯誤:<br>" + strBuilder.toString());
	}
	
}
