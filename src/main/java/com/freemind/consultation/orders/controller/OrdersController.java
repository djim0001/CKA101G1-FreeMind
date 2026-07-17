package com.freemind.consultation.orders.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.freemind.consultation.orders.model.Orders;
import com.freemind.consultation.orders.model.OrdersService;

@Controller
@RequestMapping("/admin/orders")
public class OrdersController {

	@Autowired
	private OrdersService ordersSvc;

	@GetMapping("home")
	public String consultationManagementHome(ModelMap model) {
		return "back-end/consultation/consultationManagementHome";
	}

	@GetMapping("listAllOrders")
	public String listAllOrders(ModelMap model) {
		List<Orders> list = ordersSvc.getAll();
		model.addAttribute("ordersListData", list);
		return "back-end/consultation/orders/listAllOrders";
	}

	@PostMapping("viewDetail")
	public String viewDetail(@RequestParam("orderId") String orderId, ModelMap model) {
		Orders orders = ordersSvc.getOneOrders(Integer.valueOf(orderId));
		model.addAttribute("orders", orders);
		return "back-end/consultation/orders/viewOrderDetail";
	}

	@PostMapping("search")
	public String search(@RequestParam(value = "orderId", required = false) String orderId,
			@RequestParam(value = "memberId", required = false) String memberId,
			@RequestParam(value = "psychId", required = false) String psychId,
			@RequestParam(value = "orderStatus", required = false) String orderStatus,
			@RequestParam(value = "govSubsidy", required = false) String govSubsidy,
			@RequestParam(value = "sessionType", required = false) String sessionType,
			@RequestParam(value = "slotDate", required = false) String slotDate, ModelMap model) {

		// 有填訂單編號 → 直接查那一筆
		if (orderId != null && !orderId.isBlank()) {
			Orders one = ordersSvc.getOneOrders(Integer.valueOf(orderId));
			model.addAttribute("ordersListData",
					one != null ? java.util.List.of(one) : new java.util.ArrayList<Orders>());
			return "back-end/consultation/orders/listAllOrders";
		}

		Integer memberIdVal = (memberId != null && !memberId.isBlank()) ? Integer.valueOf(memberId) : null;
		Integer psychIdVal = (psychId != null && !psychId.isBlank()) ? Integer.valueOf(psychId) : null;
		Integer orderStatusVal = (orderStatus != null && !orderStatus.isBlank()) ? Integer.valueOf(orderStatus) : null;
		Boolean govSubsidyVal = (govSubsidy != null && !govSubsidy.isBlank()) ? Boolean.valueOf(govSubsidy) : null;
		Integer sessionTypeVal = (sessionType != null && !sessionType.isBlank()) ? Integer.valueOf(sessionType) : null;
		java.time.LocalDate slotDateVal = (slotDate != null && !slotDate.isBlank())
				? java.time.LocalDate.parse(slotDate) : null;

		// 完全沒填 → 顯示全部
		if (memberIdVal == null && psychIdVal == null && orderStatusVal == null && govSubsidyVal == null
				&& sessionTypeVal == null && slotDateVal == null) {
			model.addAttribute("ordersListData", ordersSvc.getAll());
			return "back-end/consultation/orders/listAllOrders";
		}

		List<Orders> list = ordersSvc.search(memberIdVal, psychIdVal, orderStatusVal, govSubsidyVal, sessionTypeVal,
				slotDateVal);
		model.addAttribute("ordersListData", list);
		return "back-end/consultation/orders/listAllOrders";
	}
}