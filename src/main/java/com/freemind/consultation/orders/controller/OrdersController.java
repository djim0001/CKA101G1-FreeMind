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

	@Autowired
	private com.freemind.login.psychologist.repository.PsychologistRepository psychologistRepository;

	@GetMapping("home")
	public String consultationManagementHome(ModelMap model) {
		return "back-end/consultation/consultationManagementHome";
	}

	@GetMapping("listAllOrders")
	public String listAllOrders(ModelMap model) {
		List<Orders> list = ordersSvc.getAll();
		model.addAttribute("ordersListData", list);
		model.addAttribute("psychologistsList", psychologistRepository.findAll());
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

		// 心理師下拉清單（查詢後畫面也要有）
		model.addAttribute("psychologistsList", psychologistRepository.findAll());

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
		java.time.LocalDate dateVal = (slotDate != null && !slotDate.isBlank())
				? java.time.LocalDate.parse(slotDate) : null;

		// 除了日期以外的條件都沒填 → 拿全部；有填才用 search
		List<Orders> list;
		if (memberIdVal == null && psychIdVal == null && orderStatusVal == null
				&& govSubsidyVal == null && sessionTypeVal == null) {
			list = ordersSvc.getAll();
		} else {
			list = ordersSvc.search(memberIdVal, psychIdVal, orderStatusVal, govSubsidyVal, sessionTypeVal, null);
		}

		// 時段日期改用「開始時間(consStart)的日期」來比對，跟畫面顯示一致
		if (dateVal != null) {
			list = list.stream()
					.filter(o -> o.getConsStart() != null && o.getConsStart().toLocalDate().equals(dateVal))
					.collect(java.util.stream.Collectors.toList());
		}

		model.addAttribute("ordersListData", list);
		return "back-end/consultation/orders/listAllOrders";
	}
}