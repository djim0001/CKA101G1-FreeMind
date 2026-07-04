package com.freemind.course.order.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.freemind.course.course.model.Course;
import com.freemind.course.course.model.CourseService;
import com.freemind.course.order.model.ShoppingCartDTO;
import com.freemind.course.order.model.ShoppingCartService;

import jakarta.servlet.http.HttpSession;

//@Controller
public class ShoppingCartController {

//	private final ShoppingCartService shoppingCartSvc;
//	private final CourseService courseService;
//	
//	public ShoppingCartController(
//			ShoppingCartService shoppingCartSvc, 
//			CourseService courseService) {
//		this.shoppingCartSvc = shoppingCartSvc;
//		this.courseService = courseService;
//	}
//	
//	
//	@PostMapping("addCart")
//	public String addToCart(
//            @RequestParam Integer courseId,
//            @SessionAttribute(name = "memberId", required = false) Integer memberId, 
//            ModelMap model, HttpSession session) {
//
//	    // 這裡通常會先從 MySQL 查課程資料
//	    Course course = courseService.getOneCourse(courseId);
//	    ShoppingCartDTO cartDTO = 
//	    		new ShoppingCartDTO(
//	    				course.getCourseId(),
//	    		        course.getCourseName(),
//	    		        course.getPsychologist().getName(),
//	    		        course.getPrice(),
//	    		        course.getPsychDiscount(),
//	    		        shoppingCartSvc.calculateTotal(courseId, course.getPsychDiscount()));
//	    
//	    shoppingCartSvc.addCourseToCart(memberId, cartDTO);
//
////	    return "redirect:/course/cart?memberId=" + memberId;
//	    return "front-end/member/course/shoppingCarts";
//	}
//	
//	@GetMapping("/cart")
//	public String showCart(@RequestParam Integer memberId, Model model) {
//
//	    List<ShoppingCartDTO> cartList = shoppingCartSvc.getCartList(memberId);
//
//	    model.addAttribute("cartList", cartList);
//
//	    return "course/cart";
//	}
//	
//	@PostMapping("/cart/remove")
//	public String removeFromCart(@RequestParam Integer memberId,
//	                             @RequestParam Integer courseId) {
//
//	    shoppingCartSvc.removeCourseFromCart(memberId, courseId);
//
//	    return "redirect:/course/cart?memberId=" + memberId;
//	}
//	
//	@PostMapping("/cart/clear")
//	public String clearCart(@RequestParam Integer memberId) {
//
//	    shoppingCartSvc.clearCart(memberId);
//
//	    return "redirect:/course/cart?memberId=" + memberId;
//	}
	
	
}
