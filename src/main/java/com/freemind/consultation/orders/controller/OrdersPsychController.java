package com.freemind.consultation.orders.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.freemind.consultation.orders.model.Orders;
import com.freemind.consultation.orders.model.OrdersService;
import com.freemind.consultation.slots.model.SlotsService;
import com.freemind.login.security.psychologistsecurity.PsychUserDetails;

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

	@GetMapping("home")
	public String psychHome(ModelMap model) {
		return "front-end/psych/consultation/orders/psychHome";
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
		return "redirect:/psych/orders/psychOrders";
	}

	@PostMapping("reject")
	public String reject(@RequestParam("orderId") String orderId, @RequestParam("psychId") String psychId,
			ModelMap model) {
		ordersSvc.rejectOrder(Integer.valueOf(orderId));
		return "redirect:/psych/orders/psychOrders";
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
	public String psychReviewSearch(@AuthenticationPrincipal PsychUserDetails prinUserDetails, ModelMap model) {
		if (prinUserDetails == null) {
			return "redirect:/front-end/login";
		}
		Integer psychId = prinUserDetails.getPsychologist().getPsychId();
		List<Orders> list = ordersSvc.getReviewsByPsychId(psychId);
		model.addAttribute("ordersListData", list);
		model.addAttribute("psychId", psychId);
		return "front-end/psych/consultation/orders/psychReviewSearch";
	}


	@PostMapping("complete")
	public String complete(@RequestParam("orderId") String orderId, @RequestParam("psychId") String psychId,
			@RequestParam(value = "psychNote", required = false) String psychNote, ModelMap model) {
		// 沒寫晤談筆記就不允許標記完成
		if (psychNote == null || psychNote.isBlank()) {
			return "redirect:/psych/orders/psychOrders";
		}
		ordersSvc.completeOrder(Integer.valueOf(orderId), psychNote);
		return "redirect:/psych/orders/psychOrders";
	}


	@PostMapping("noShow")
	public String noShow(@RequestParam("orderId") String orderId, @RequestParam("psychId") String psychId,
			ModelMap model) {
		ordersSvc.noShowOrder(Integer.valueOf(orderId));
		return "redirect:/psych/orders/psychOrders";
	}
	
	@GetMapping("psychOrders")
	public String psychOrders(@AuthenticationPrincipal PsychUserDetails prinUserDetails, ModelMap model) {
		if (prinUserDetails == null) {
			return "redirect:/front-end/login";
		}
		Integer psychId = prinUserDetails.getPsychologist().getPsychId();
		List<Orders> list = ordersSvc.getPendingAndConfirmedByPsychId(psychId);

		java.time.LocalDateTime now = java.time.LocalDateTime.now();
		java.util.Set<Integer> overdueOrders = new java.util.HashSet<>();

		// 統計每個時段有幾筆「待確認」訂單
		java.util.Map<java.time.LocalDateTime, Long> slotCount = new java.util.HashMap<>();
		for (Orders o : list) {
			if (o.getOrderStatus() != null && o.getOrderStatus() == 1
					&& o.getConsEnd() != null && now.isAfter(o.getConsEnd().plusDays(3))) {
				overdueOrders.add(o.getOrderId());
			}
			if (o.getOrderStatus() != null && o.getOrderStatus() == 0 && o.getConsStart() != null) {
				slotCount.merge(o.getConsStart(), 1L, Long::sum);
			}
		}

		// 同一時段 >1 筆待確認 → 標記為競爭，記下人數
		java.util.Map<Integer, Long> competitionMap = new java.util.HashMap<>();
		for (Orders o : list) {
			if (o.getOrderStatus() != null && o.getOrderStatus() == 0 && o.getConsStart() != null) {
				Long c = slotCount.get(o.getConsStart());
				if (c != null && c > 1) {
					competitionMap.put(o.getOrderId(), c);
				}
			}
		}

		model.addAttribute("overdueOrders", overdueOrders);
		model.addAttribute("competitionMap", competitionMap);
		model.addAttribute("ordersListData", list);
		model.addAttribute("psychId", psychId);
		return "front-end/psych/consultation/orders/psychOrdersForm";
	}

	@GetMapping("psychCancelledForm")
	public String psychCancelledForm(@AuthenticationPrincipal PsychUserDetails prinUserDetails, ModelMap model) {
		if (prinUserDetails == null) {
			return "redirect:/front-end/login";
		}
		Integer psychId = prinUserDetails.getPsychologist().getPsychId();
		List<Orders> list = ordersSvc.getCancelledOrdersByPsychId(psychId);
		model.addAttribute("ordersListData", list);
		model.addAttribute("psychId", psychId);
		return "front-end/psych/consultation/orders/psychCancelledForm";
	}

	@GetMapping("psychHistoryForm")
	public String psychHistoryForm(@AuthenticationPrincipal PsychUserDetails prinUserDetails, ModelMap model) {
		if (prinUserDetails == null) {
			return "redirect:/front-end/login";
		}
		Integer psychId = prinUserDetails.getPsychologist().getPsychId();
		List<Orders> list = ordersSvc.getHistory(psychId, null, null);
		model.addAttribute("ordersListData", list);
		model.addAttribute("psychId", psychId);
		return "front-end/psych/consultation/orders/psychHistoryForm";
	}

	@PostMapping("psychHistoryForm")
	public String psychHistory(@AuthenticationPrincipal PsychUserDetails prinUserDetails,
			@RequestParam(value = "slotDate", required = false) String slotDate,
			@RequestParam(value = "orderStatus", required = false) String orderStatus,
			ModelMap model) {
		if (prinUserDetails == null) {
			return "redirect:/front-end/login";
		}
		Integer psychId = prinUserDetails.getPsychologist().getPsychId();
		LocalDate dateVal = (slotDate == null || slotDate.isBlank()) ? null : LocalDate.parse(slotDate);
		Integer statusVal = (orderStatus == null || orderStatus.isBlank()) ? null : Integer.valueOf(orderStatus);
		List<Orders> list = ordersSvc.getHistory(psychId, dateVal, statusVal);
		model.addAttribute("ordersListData", list);
		model.addAttribute("psychId", psychId);
		model.addAttribute("slotDate", slotDate);
		model.addAttribute("orderStatus", orderStatus);
		return "front-end/psych/consultation/orders/psychHistoryForm";
	}
}