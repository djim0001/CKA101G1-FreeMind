package com.freemind.consultation.orders.controller;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.freemind.consultation.orders.model.Orders;
import com.freemind.consultation.orders.model.OrdersService;
import com.freemind.consultation.slots.model.Slots;
import com.freemind.consultation.slots.model.SlotsService;
import com.freemind.login.member.model.Member;
import com.freemind.login.psychologist.entity.Psychologist;
import com.freemind.login.psychologist.repository.PsychologistRepository;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/orders")
public class OrdersController {

	@Autowired
	private OrdersService ordersSvc;

	@Autowired
	private SlotsService slotsSvc;

	@Autowired
	private PsychologistRepository psychologistRepository;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		binder.registerCustomEditor(Member.class, "member", new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				if (text == null || text.isBlank()) {
					setValue(null);
				} else {
					Member member = new Member();
					member.setMemberId(Integer.valueOf(text));
					setValue(member);
				}
			}
		});

		binder.registerCustomEditor(Psychologist.class, "psychologist", new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				if (text == null || text.isBlank()) {
					setValue(null);
				} else {
					Psychologist psychologist = new Psychologist();
					psychologist.setPsychId(Integer.valueOf(text));
					setValue(psychologist);
				}
			}
		});
	}

	@GetMapping("listAllOrders")
	public String listAllOrders(ModelMap model) {
		List<Orders> list = ordersSvc.getAll();
		model.addAttribute("ordersListData", list);
		return "back-end/consultation/orders/listAllOrders";
	}

	@GetMapping("addOrders")
	public String addOrders(ModelMap model) {
		Orders orders = new Orders();
		model.addAttribute("orders", orders);
		model.addAttribute("slotsList", slotsSvc.getAll());
		return "back-end/consultation/orders/addOrders";
	}

	@GetMapping("select_Page")
	public String select_Page(ModelMap model) {
		return "back-end/consultation/orders/select_Page";
	}

	@PostMapping("insert")
	public String insert(@Valid Orders orders, BindingResult result, ModelMap model,
			@RequestParam(value = "slot", required = false) Integer timeslotId) {

		// 檢查時段是否有選擇
		if (timeslotId == null) {
			model.addAttribute("slotError", "請選擇時段");
			model.addAttribute("slotsList", slotsSvc.getAll());
			return "back-end/consultation/orders/addOrders";
		}

		if (result.hasErrors()) {
			model.addAttribute("slotsList", slotsSvc.getAll());
			return "back-end/consultation/orders/addOrders";
		}

		Slots slot = slotsSvc.getOneSlots(timeslotId);
		orders.setSlot(slot);
		ordersSvc.addOrders(orders);
		return "redirect:/orders/listAllOrders";
	}

	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("orderId") String orderId, ModelMap model) {

		Orders orders = ordersSvc.getOneOrders(Integer.valueOf(orderId));

		model.addAttribute("orders", orders);
		model.addAttribute("slotsList", slotsSvc.getAll());
		return "back-end/consultation/orders/update_orders_input";
	}

	@PostMapping("update")
	public String update(@Valid Orders orders, BindingResult result, ModelMap model,
			@RequestParam(value = "slot", required = false) Integer timeslotId) {
		if (result.hasErrors()) {
			model.addAttribute("slotsList", slotsSvc.getAll());
			return "back-end/consultation/orders/update_orders_input"; // 如果驗證有錯誤，回到修改表單頁面
		}

		Slots slot = slotsSvc.getOneSlots(timeslotId);
		orders.setSlot(slot);
		ordersSvc.updateOrders(orders);// 呼叫Service修改資料

		model.addAttribute("success", "-(修改成功)");
		Orders updatedOrders = ordersSvc.getOneOrders(orders.getOrderId());
		model.addAttribute("orders", updatedOrders);
		return "back-end/consultation/orders/listOneOrders";
	}

	@PostMapping("delete")
	public String delete(@RequestParam("orderId") String orderId, ModelMap model) {
		ordersSvc.deleteOrders(Integer.valueOf(orderId));

		List<Orders> list = ordersSvc.getAll();
		model.addAttribute("ordersListData", list);
		model.addAttribute("success", "-(刪除成功)");
		return "back-end/consultation/orders/listAllOrders";
	}

	@PostMapping("getOne_For_Display")
	public String getOne_For_Display(@RequestParam("orderId") String orderId, ModelMap model) {
		Orders orders = ordersSvc.getOneOrders(Integer.valueOf(orderId));
		model.addAttribute("orders", orders);
		return "back-end/consultation/orders/select_Page";
	}

	@PostMapping("getByMember")
	public String getByMember(@RequestParam("memberId") String memberId, ModelMap model) {
		List<Orders> list = ordersSvc.getByMemberId(Integer.valueOf(memberId));
		model.addAttribute("ordersListData", list);
		return "back-end/consultation/orders/select_Page";
	}

	@PostMapping("getByPsychId")
	public String getByPsychId(@RequestParam("psychId") String psychId, ModelMap model) {
		List<Orders> list = ordersSvc.getByPsychId(Integer.valueOf(psychId));
		model.addAttribute("ordersListData", list);
		return "back-end/consultation/orders/select_Page";
	}

	@PostMapping("getByOrderStatus")
	public String getByOrderStatus(@RequestParam("orderStatus") String orderStatus, ModelMap model) {
		List<Orders> list = ordersSvc.getByOrderStatus(Integer.valueOf(orderStatus));
		model.addAttribute("ordersListData", list);
		return "back-end/consultation/orders/select_Page";
	}

	@PostMapping("getByGovSubsidy")
	public String getByGovSubsidy(@RequestParam("govSubsidy") String govSubsidy, ModelMap model) {
		List<Orders> list = ordersSvc.getByGovSubsidy(Boolean.valueOf(govSubsidy));
		model.addAttribute("ordersListData", list);
		return "back-end/consultation/orders/select_Page";
	}

	@PostMapping("getBySessionType")
	public String getBySessionType(@RequestParam("sessionType") String sessionType, ModelMap model) {
		List<Orders> list = ordersSvc.getBySessionType(Integer.valueOf(sessionType));
		model.addAttribute("ordersListData", list);
		return "back-end/consultation/orders/select_Page";
	}

	@PostMapping("getBySlotDate")
	public String getBySlotDate(@RequestParam("slotDate") String slotDate, ModelMap model) {
		java.time.LocalDate date = java.time.LocalDate.parse(slotDate);
		List<Orders> list = ordersSvc.getBySlotDate(date);
		model.addAttribute("ordersListData", list);
		return "back-end/consultation/orders/select_Page";
	}

	// ===== 會員：前台選時段下訂單 =====

	@GetMapping("bookForm")
	public String bookForm(ModelMap model) {
		return "front-end/member/consultation/orders/bookForm";
	}

	@PostMapping("bookLookup")
	public String bookLookup(@RequestParam("psychId") String psychId, @RequestParam("slotDate") String slotDateStr,
			ModelMap model) {
		if (psychId == null || psychId.isBlank() || slotDateStr == null || slotDateStr.isBlank()) {
			model.addAttribute("errorMessage", "請輸入心理師編號與日期");
			return "front-end/member/consultation/orders/bookForm";
		}

		Integer pid = Integer.valueOf(psychId);
		LocalDate date = LocalDate.parse(slotDateStr);

		Slots slots = slotsSvc.getOneByPsychAndDate(pid, date);
		if (slots == null) {
			model.addAttribute("errorMessage", "該心理師這天尚未開放預約");
			return "front-end/member/consultation/orders/bookForm";
		}

		List<Integer> availableHours = new java.util.ArrayList<>();
		String status = slots.getConsStatus();
		for (int h = 0; h < 24; h++) {
		    if (status.charAt(h) == '1' || status.charAt(h) == '4') {
		        availableHours.add(h);
		    }
		}

		if (availableHours.isEmpty()) {
			model.addAttribute("errorMessage", "該心理師這天已無可預約時段");
			return "front-end/member/consultation/orders/bookForm";
		}

		Psychologist psychologist = psychologistRepository.findById(pid).orElse(null);
		if (psychologist == null) {
			model.addAttribute("errorMessage", "查無此心理師");
			return "front-end/member/consultation/orders/bookForm";
		}

		model.addAttribute("slots", slots);
		model.addAttribute("availableHours", availableHours);
		model.addAttribute("psychologist", psychologist);
		return "front-end/member/consultation/orders/bookInput";
	}

	@PostMapping("bookSubmit")
	public String bookSubmit(@RequestParam("timeslotId") String timeslotId, @RequestParam("hour") String hourStr,
			@RequestParam("memberId") String memberId, @RequestParam("psychId") String psychId,
			@RequestParam("psychLoc") String psychLoc, @RequestParam("psychFee") String psychFee,
			@RequestParam("visitPurpose") String visitPurpose,
			@RequestParam(value = "visitPurposeNote", required = false) String visitPurposeNote,
			@RequestParam("sessionType") String sessionType, ModelMap model) {

		int hour = Integer.parseInt(hourStr);
		Integer tid = Integer.valueOf(timeslotId);

		// 不再鎖定時段，允許多人對同一時段送出待確認訂單
		Slots slot = slotsSvc.getOneSlots(tid);

		Member member = new Member();
		member.setMemberId(Integer.valueOf(memberId));

		Psychologist psychologist = new Psychologist();
		psychologist.setPsychId(Integer.valueOf(psychId));

		Orders orders = new Orders();
		orders.setSlot(slot);
		orders.setMember(member);
		orders.setPsychologist(psychologist);
		orders.setConsStart(slot.getSlotDate().atTime(hour, 0));
		orders.setConsEnd(slot.getSlotDate().atTime(hour + 1, 0));
		orders.setPsychLoc(psychLoc);
		orders.setPsychFee(Integer.valueOf(psychFee));
		orders.setVisitPurpose(visitPurpose);
		orders.setVisitPurposeNote(visitPurposeNote);
		orders.setSessionType(Integer.valueOf(sessionType));
		orders.setOrderStatus(0); // 待確認
		orders.setGovSubsidy(false);

		ordersSvc.addOrders(orders);

		model.addAttribute("success", "預約成功！等待心理師確認。");
		return "front-end/member/consultation/orders/bookSuccess";
	}

	// 心理師：查看待確認訂單並同意/拒絕 =====

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
		return "redirect:/orders/psychOrders?psychId=" + psychId; // ← 改成這樣
	}

	@PostMapping("reject")
	public String reject(@RequestParam("orderId") String orderId, @RequestParam("psychId") String psychId,
			ModelMap model) {
		ordersSvc.rejectOrder(Integer.valueOf(orderId));
		return "redirect:/orders/psychOrders?psychId=" + psychId; // ← 改成這樣
	}

	// ===== 會員：查看自己的訂單記錄 =====

	@GetMapping("myOrdersForm")
	public String myOrdersForm(ModelMap model) {
		return "front-end/member/consultation/orders/myOrdersForm";
	}

	@PostMapping("myOrders")
	public String myOrders(@RequestParam("memberId") String memberId, ModelMap model) {
		if (memberId == null || memberId.isBlank()) {
			model.addAttribute("errorMessage", "請輸入會員編號");
			return "front-end/member/consultation/orders/myOrdersForm";
		}
		List<Orders> list = ordersSvc.getByMemberId(Integer.valueOf(memberId));
		model.addAttribute("ordersListData", list);
		model.addAttribute("memberId", memberId);
		return "front-end/member/consultation/orders/myOrdersList";
	}

	// ===== 心理師：查看已確認訂單，標記完成 =====

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

	// ===== 會員：填寫心得評論 =====

	@GetMapping("reviewForm")
	public String reviewForm(ModelMap model) {
		return "front-end/member/consultation/orders/reviewForm";
	}

	@PostMapping("reviewLookup")
	public String reviewLookup(@RequestParam("memberId") String memberId, ModelMap model) {
		if (memberId == null || memberId.isBlank()) {
			model.addAttribute("errorMessage", "請輸入會員編號");
			return "front-end/member/consultation/orders/reviewForm";
		}

		List<Orders> list = ordersSvc.getCompletedUnratedByMemberId(Integer.valueOf(memberId));

		if (list.isEmpty()) {
			model.addAttribute("errorMessage", "目前沒有可評論的諮商紀錄。");
			return "front-end/member/consultation/orders/reviewForm";
		}

		model.addAttribute("ordersListData", list);
		model.addAttribute("memberId", memberId);
		return "front-end/member/consultation/orders/reviewList";
	}

	@PostMapping("reviewSelect")
	public String reviewSelect(@RequestParam("orderId") String orderId, ModelMap model) {
		Orders orders = ordersSvc.getOneOrders(Integer.valueOf(orderId));
		model.addAttribute("orders", orders);
		return "front-end/member/consultation/orders/reviewInput";
	}

	@PostMapping("submitReview")
	public String submitReview(@RequestParam("orderId") String orderId, @RequestParam("rating") String rating,
			@RequestParam("reviewContent") String reviewContent, ModelMap model) {
		ordersSvc.submitReview(Integer.valueOf(orderId), Integer.valueOf(rating), reviewContent);
		model.addAttribute("success", "感謝您的評價！");
		return "front-end/member/consultation/orders/reviewSuccess";
	}

	// ===== 心理師：查看評價（公開頁，尚未串登入）=====

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

	// 諮商師填寫晤談筆記
	@PostMapping("complete")
	public String complete(@RequestParam("orderId") String orderId, @RequestParam("psychId") String psychId,
			@RequestParam(value = "psychNote", required = false) String psychNote, ModelMap model) {
		ordersSvc.completeOrder(Integer.valueOf(orderId), psychNote);
		return "redirect:/orders/psychOrders?psychId=" + psychId; // ← 改成這樣
	}

	@PostMapping("viewDetail")
	public String viewDetail(@RequestParam("orderId") String orderId, ModelMap model) {
		Orders orders = ordersSvc.getOneOrders(Integer.valueOf(orderId));
		model.addAttribute("orders", orders);
		return "back-end/consultation/orders/viewOrderDetail";
	}

	// 諮商訂單列表同時查詢一筆以上
	@PostMapping("search")
	public String search(@RequestParam(value = "orderId", required = false) String orderId,
			@RequestParam(value = "memberId", required = false) String memberId,
			@RequestParam(value = "psychId", required = false) String psychId,
			@RequestParam(value = "orderStatus", required = false) String orderStatus,
			@RequestParam(value = "govSubsidy", required = false) String govSubsidy,
			@RequestParam(value = "sessionType", required = false) String sessionType,
			@RequestParam(value = "slotDate", required = false) String slotDate, ModelMap model) {

		// 訂單編號是唯一識別，若有填，直接查單筆並忽略其他條件
		if (orderId != null && !orderId.isBlank()) {
			Orders orders = ordersSvc.getOneOrders(Integer.valueOf(orderId));
			model.addAttribute("orders", orders);
			return "back-end/consultation/orders/select_Page";
		}

		Integer memberIdVal = (memberId != null && !memberId.isBlank()) ? Integer.valueOf(memberId) : null;
		Integer psychIdVal = (psychId != null && !psychId.isBlank()) ? Integer.valueOf(psychId) : null;
		Integer orderStatusVal = (orderStatus != null && !orderStatus.isBlank()) ? Integer.valueOf(orderStatus) : null;
		Boolean govSubsidyVal = (govSubsidy != null && !govSubsidy.isBlank()) ? Boolean.valueOf(govSubsidy) : null;
		Integer sessionTypeVal = (sessionType != null && !sessionType.isBlank()) ? Integer.valueOf(sessionType) : null;
		java.time.LocalDate slotDateVal = (slotDate != null && !slotDate.isBlank())
				? java.time.LocalDate.parse(slotDate)
				: null;

		if (memberIdVal == null && psychIdVal == null && orderStatusVal == null && govSubsidyVal == null
				&& sessionTypeVal == null && slotDateVal == null) {
			model.addAttribute("errorMessage", "請至少填寫一個查詢條件");
			return "back-end/consultation/orders/select_Page";
		}

		List<Orders> list = ordersSvc.search(memberIdVal, psychIdVal, orderStatusVal, govSubsidyVal, sessionTypeVal,
				slotDateVal);
		model.addAttribute("ordersListData", list);
		return "back-end/consultation/orders/select_Page";
	}

	// 心理師：合併查看待確認+已確認訂單

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
		return "redirect:/orders/psychOrders?psychId=" + psychId;
	}
	
	// 心理師：查看已取消的訂單
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
	
	//歷史諮商紀錄
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
	    java.time.LocalDate dateVal = dateEmpty ? null : java.time.LocalDate.parse(slotDate);
	    Integer statusVal = statusEmpty ? null : Integer.valueOf(orderStatus);
	    List<Orders> list = ordersSvc.getHistory(psychIdVal, dateVal, statusVal);
	    model.addAttribute("ordersListData", list);
	    model.addAttribute("psychId", psychId);
	    model.addAttribute("slotDate", slotDate);
	    model.addAttribute("orderStatus", orderStatus);
	    return "front-end/psych/consultation/orders/psychHistoryForm";
	}
}
