package com.freemind.course.order.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.course.order.model.CourseOrder;
import com.freemind.course.order.model.CourseOrderService;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;

@Controller
@RequestMapping("/member/course/order")
public class CourseOrderController {

	@Autowired
	private CourseOrderService courseOrderService;
	@Autowired
	private MemberService memberSvc;

	@ModelAttribute("member")
	public Member currentMember(Authentication authentication) {
		return memberSvc.findByAccount(authentication.getName());
	}

	@GetMapping("acb")
	public String getOrderById(ModelMap model, @ModelAttribute("member") Member member) {

		// 1. 從 Session 撈出登入時存進去的會員物件 (請確保跟您登入功能存的 Key 一致，這裡假設叫 "member")
//        Member loginMember = (Member) session.getAttribute("member"); 

		Member mem = memberSvc.getOneMember(member.getMemberId());
		// 安全檢查：如果沒登入，強制踢回登入頁
		if (mem == null) {
			return "redirect:/login";
		}

		// 2. 動態撈出「這個登入的人」的所有訂單
		List<CourseOrder> orders = courseOrderService.getOrdersByMember(mem);

		// 3. 把訂單清單交給 Thymeleaf
		model.addAttribute("orders", orders);

		return "front-end/member/course/CourseOrder";
	}

//用「會員編號」查詢該會員的所有訂單，並跳轉到我的訂單網頁

	@GetMapping("/member/memberId")
	public String getOrdersByMemberId(@PathVariable Integer memberId, org.springframework.ui.Model model) {

		Member member = new Member();
		member.setMemberId(memberId);

		// 呼叫 Service 撈出該會員的所有訂單列表
		List<CourseOrder> orders = courseOrderService.getOrdersByMember(member);

		// 將訂單列表塞入 model
		model.addAttribute("orders", orders);
		return "front-end/course/CourseOrder";
	}

	@PostMapping("/cancelOrder")
	public String cancelOrder(
			@ModelAttribute("member") Member member,
			@RequestParam("courseOrderId") Integer courseOrderId,
			@RequestParam(value = "returnUrl", required = false) String returnUrl,
			RedirectAttributes redirectAttributes,
			ModelMap model) {

		CourseOrder order = courseOrderService.getOrderById(courseOrderId);
		order.setPaymentStatus(2);
		return "redirect:" + returnUrl;
	}

}
