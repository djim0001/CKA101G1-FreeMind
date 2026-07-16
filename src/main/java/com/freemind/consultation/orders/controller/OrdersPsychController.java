package com.freemind.consultation.orders.controller;

import java.time.LocalDate;
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
import com.freemind.consultation.slots.model.SlotsService;

@Controller
@RequestMapping("/psych/orders")
public class OrdersPsychController {

	@Autowired
	private OrdersService ordersSvc;

	@Autowired
	private SlotsService slotsSvc;

	@GetMapping("psychPendingForm")
	public String psychPendingForm(ModelMap model) {
		return "front-end/psych/consultation/orders/psychPendingForm";
	}

	@PostMapping("psychPending")
	public String psychPending(@RequestParam("psychId") String psychId, ModelMap model) {
		if (psychId == null || psychId.isBlank()) {
			model.addAttribute("errorMessage", "請輸入心理師編號");
			return "front-end/psych/consultation/orders/psychPendingForm";
		}
		List<Orders> list = ordersSvc.getPendingOrdersByPsychId(Integer.valueOf(psychId));
		model.addAttribute("ordersListData", list);
		model.addAttribute("psychId", psychId);
		return "front-end/psych/consultation/orders/psychPendingList";
	}

	@GetMapping("psychPending")
	public String psychPendingRedirect(@RequestParam("psychId") String psychId, ModelMap model) {
		List<Orders> list = ordersSvc.getPendingOrdersByPsychId(Integer.valueOf(psychId));
		model.addAttribute("ordersListData", list);
		model.addAttribute("psychId", psychId);
		return "front-end/psych/consultation/orders/psychPendingList";
	}

	@PostMapping("approve")
	public String approve(@RequestParam("orderId") String orderId, @RequestParam("psychId") String psychId,
			ModelMap model) {
		ordersSvc.approveOrder(Integer.valueOf(orderId), slotsSvc);
		return "redirect:/psych/orders/psychOrders?psychId=" + psychId;
	}

	@PostMapping("reject")
	public String reject(@RequestParam("orderId") String orderId, @RequestParam("psychId") String psychId,
			ModelMap model) {
		ordersSvc.rejectOrder(Integer.valueOf(orderId));
		return "redirect:/psych/orders/psychOrders?psychId=" + psychId;
	}

	@GetMapping("psychConfirmedForm")
	public String psychConfirmedForm(ModelMap model) {
		return "front-end/psych/consultation/orders/psychConfirmedForm";
	}

	@PostMapping("psychConfirmed")
	public String psychConfirmed(@RequestParam("psychId") String psychId, ModelMap model) {
		if (psychId == null || psychId.isBlank()) {
			model.addAttribute("errorMessage", "請輸入心理師編號");
			return "front-end/psych/consultation/orders/psychConfirmedForm";
		}
		List<Orders> list = ordersSvc.getConfirmedOrdersByPsychId(Integer.valueOf(psychId));
		model.addAttribute("ordersListData", list);
		model.addAttribute("psychId", psychId);
		return "front-end/psych/consultation/orders/psychConfirmedList";
	}

	@GetMapping("psychConfirmed")
	public String psychConfirmedRedirect(@RequestParam("psychId") String psychId, ModelMap model) {
		List<Orders> list = ordersSvc.getConfirmedOrdersByPsychId(Integer.valueOf(psychId));
		model.addAttribute("ordersListData", list);
		model.addAttribute("psychId", psychId);
		return "front-end/psych/consultation/orders/psychConfirmedList";
	}

	@GetMapping("psychReviews")
	public String psychReviewSearch(ModelMap model) {
		return "front-end/psych/consultation/orders/psychReviewSearch";
	}

	@PostMapping("psychReviews")
	public String psychReviews(@RequestParam("psychId") String psychId, ModelMap model) {
		if (psychId == null || psychId.isBlank()) {
			model.addAttribute("errorMessage", "請輸入心理師編號");
			return "front-end/psych/consultation/orders/psychReviewSearch";
		}
		List<Orders> list = ordersSvc.getReviewsByPsychId(Integer.valueOf(psychId));
		model.addAttribute("ordersListData", list);
		model.addAttribute("psychId", psychId);
		return "front-end/psych/consultation/orders/psychReviewSearch";
	}

	@PostMapping("complete")
	public String complete(@RequestParam("orderId") String orderId, @RequestParam("psychId") String psychId,
			@RequestParam(value = "psychNote", required = false) String psychNote, ModelMap model) {
		ordersSvc.completeOrder(Integer.valueOf(orderId), psychNote);
		return "redirect:/psych/orders/psychOrders?psychId=" + psychId;
	}

	@GetMapping("psychOrdersForm")
	public String psychOrdersForm(ModelMap model) {
		return "front-end/psych/consultation/orders/psychOrdersForm";
	}

	@PostMapping("psychOrders")
	public String psychOrders(@RequestParam("psychId") String psychId, ModelMap model) {
		if (psychId == null || psychId.isBlank()) {
			model.addAttribute("errorMessage", "請輸入心理師編號");
			return "front-end/psych/consultation/orders/psychOrdersForm";
		}
		List<Orders> list = ordersSvc.getPendingAndConfirmedByPsychId(Integer.valueOf(psychId));
		model.addAttribute("ordersListData", list);
		model.addAttribute("psychId", psychId);
		return "front-end/psych/consultation/orders/psychOrdersForm";
	}

	@GetMapping("psychOrders")
	public String psychOrdersRedirect(@RequestParam("psychId") String psychId, ModelMap model) {
		List<Orders> list = ordersSvc.getPendingAndConfirmedByPsychId(Integer.valueOf(psychId));
		model.addAttribute("ordersListData", list);
		model.addAttribute("psychId", psychId);
		return "front-end/psych/consultation/orders/psychOrdersForm";
	}

	@PostMapping("noShow")
	public String noShow(@RequestParam("orderId") String orderId, @RequestParam("psychId") String psychId,
			ModelMap model) {
		ordersSvc.noShowOrder(Integer.valueOf(orderId));
		return "redirect:/psych/orders/psychOrders?psychId=" + psychId;
	}

	@GetMapping("psychCancelledForm")
	public String psychCancelledForm(ModelMap model) {
		return "front-end/psych/consultation/orders/psychCancelledForm";
	}

	@PostMapping("psychCancelledForm")
	public String psychCancelled(@RequestParam("psychId") String psychId, ModelMap model) {
		if (psychId == null || psychId.isBlank()) {
			model.addAttribute("errorMessage", "請輸入心理師編號");
			return "front-end/psych/consultation/orders/psychCancelledForm";
		}
		List<Orders> list = ordersSvc.getCancelledOrdersByPsychId(Integer.valueOf(psychId));
		model.addAttribute("ordersListData", list);
		model.addAttribute("psychId", psychId);
		return "front-end/psych/consultation/orders/psychCancelledForm";
	}

	@GetMapping("psychHistoryForm")
	public String psychHistoryForm(ModelMap model) {
		return "front-end/psych/consultation/orders/psychHistoryForm";
	}

	@PostMapping("psychHistoryForm")
	public String psychHistory(@RequestParam(value = "psychId", required = false) String psychId,
			@RequestParam(value = "slotDate", required = false) String slotDate,
			@RequestParam(value = "orderStatus", required = false) String orderStatus,
			ModelMap model) {
		boolean psychEmpty = (psychId == null || psychId.isBlank());
		boolean dateEmpty = (slotDate == null || slotDate.isBlank());
		boolean statusEmpty = (orderStatus == null || orderStatus.isBlank());
		if (psychEmpty && dateEmpty && statusEmpty) {
			model.addAttribute("errorMessage", "請至少輸入心理師編號、諮商日期、狀態其中一項");
			return "front-end/psych/consultation/orders/psychHistoryForm";
		}
		Integer psychIdVal = psychEmpty ? null : Integer.valueOf(psychId);
		LocalDate dateVal = dateEmpty ? null : LocalDate.parse(slotDate);
		Integer statusVal = statusEmpty ? null : Integer.valueOf(orderStatus);
		List<Orders> list = ordersSvc.getHistory(psychIdVal, dateVal, statusVal);
		model.addAttribute("ordersListData", list);
		model.addAttribute("psychId", psychId);
		model.addAttribute("slotDate", slotDate);
		model.addAttribute("orderStatus", orderStatus);
		return "front-end/psych/consultation/orders/psychHistoryForm";
	}
}