package com.freemind.course.order.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.course.order.model.CartItemDTO;
import com.freemind.course.order.model.ShoppingCartRedisService;

@Controller
@RequestMapping("/course")
public class ShoppingCartController {
	
	@Autowired
	ShoppingCartRedisService ShoppingCartRedisSvc;
	
	@GetMapping("shoppingCart")
	public String shoppingCart(ModelMap model, RedirectAttributes redirectAttributes,
			@SessionAttribute(name = "memberId", required = false) Integer memberId
			) {
		if(memberId == null) {
			redirectAttributes.addFlashAttribute("mError", "請先登入");
			return "redirect:/course/memberSelectCourse";
		}
			
		List<CartItemDTO> cartList = ShoppingCartRedisSvc.getCartCartItemDTOs(memberId);
		Integer cartTotal = ShoppingCartRedisSvc.calculateCartTotal(cartList);
		model.addAttribute("cartList", cartList);
		model.addAttribute("cartTotal", cartTotal);
		
		return "front-end/member/course/shoppingCart";
	}
	
	
	@PostMapping("addCart")
	public String addCart(
			@RequestParam("courseId") Integer courseId, 
			@SessionAttribute(name = "memberId") Integer memberId, 
	        @RequestParam(value = "returnUrl", required = false) String returnUrl,
			RedirectAttributes redirectAttributes) {
		
		System.out.println("returnUrl = " + returnUrl);
		 if (ShoppingCartRedisSvc.isCourseInCart(memberId, courseId)) {
		        redirectAttributes.addFlashAttribute("cartMsg", "此課程已在購物車中");
		    } else {
		    		ShoppingCartRedisSvc.addCourse(memberId, courseId);
		        redirectAttributes.addFlashAttribute("cartMsg", "成功加入購物車!");
		    }
		 redirectAttributes.addFlashAttribute("courseId", courseId);

		 return "redirect:" + safeRedirectUrl(returnUrl);
	}
	
	@PostMapping("cart_remove")
	public String cartRemove(
			@RequestParam("courseId") Integer courseId, 
			@SessionAttribute(name = "memberId") Integer memberId, 
			RedirectAttributes redirectAttributes) {
		ShoppingCartRedisSvc.removeCourse(memberId, courseId);
		
		return "redirect:/course/shoppingCart";
	}
	
	@PostMapping("cart_clear")
	public String cartClear(
			@SessionAttribute(name = "memberId") Integer memberId, 
			RedirectAttributes redirectAttributes) {
		ShoppingCartRedisSvc.clearCart(memberId);
		
		return "redirect:/course/shoppingCart";
	}
	
	
	private String safeRedirectUrl(String returnUrl) {
	    if (returnUrl == null || returnUrl.isBlank()) {
	        return "/course/memberSelectCourse";
	    }

	    // 只允許站內路徑，例如 /course/memberSelectCourse?page=2
	    if (!returnUrl.startsWith("/")) {
	        return "/course/memberSelectCourse";
	    }

	    // 避免 //evil.com 這種瀏覽器可能解讀成外部網址的寫法
	    if (returnUrl.startsWith("//")) {
	        return "/course/memberSelectCourse";
	    }
	    
	    if (returnUrl.startsWith("/course/memberSelectCourse")
	            || returnUrl.startsWith("/course/memberGetOneCourse")) {
	        return returnUrl;
	    }

	    return "/course/memberSelectCourse";
	}
}
