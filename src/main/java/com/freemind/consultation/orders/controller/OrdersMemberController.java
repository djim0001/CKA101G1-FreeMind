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
import com.freemind.consultation.slots.model.Slots;
import com.freemind.consultation.slots.model.SlotsService;
import com.freemind.login.member.model.Member;
import com.freemind.login.psychologist.entity.Psychologist;
import com.freemind.login.psychologist.repository.PsychologistRepository;
import com.freemind.login.security.membersecurity.MemberUserDetails;

@Controller
@RequestMapping("/member/orders")
public class OrdersMemberController {

	@Autowired
	private OrdersService ordersSvc;

	@Autowired
	private SlotsService slotsSvc;

	@Autowired
	private PsychologistRepository psychologistRepository;

	@Autowired
	private com.freemind.consultation.reports.model.ReportsService reportsSvc;

	private List<Psychologist> getActivePsychologists() {
		return psychologistRepository.findAll().stream()
				.filter(p -> p.getAccountStatus() != null && p.getAccountStatus() == 1)
				.collect(java.util.stream.Collectors.toList());
	}

	// 算某天的可預約時段：可預約(1/4) 且 若為今天則排除已過去的小時
	private List<Integer> computeAvailableHours(Slots slot, LocalDate date) {
		int currentHour = date.equals(LocalDate.now()) ? java.time.LocalTime.now().getHour() : -1;
		List<Integer> hours = new java.util.ArrayList<>();
		String status = slot.getConsStatus();
		for (int h = 0; h < 24; h++) {
			boolean open = (status.charAt(h) == '1' || status.charAt(h) == '4');
			if (open && h > currentHour) { // 今天只留現在之後的小時；未來日期全留
				hours.add(h);
			}
		}
		return hours;
	}

	@GetMapping("bookForm")
	public String bookForm(@RequestParam(value = "psychId", required = false) String psychId, ModelMap model) {
		model.addAttribute("todayDate", java.time.LocalDate.now().toString());
		model.addAttribute("psychologistsList", getActivePsychologists());
		model.addAttribute("selectedPsychId", psychId);
		return "front-end/member/consultation/orders/bookForm";
	}

	@GetMapping("bookLookup")
	public String bookLookupFromCalendar(@RequestParam("psychId") String psychId,
			@RequestParam("slotDate") String slotDateStr, ModelMap model) {

		Integer pid = Integer.valueOf(psychId);
		LocalDate date = LocalDate.parse(slotDateStr);

		Psychologist psychologist = psychologistRepository.findById(pid).orElse(null);
		Slots slots = slotsSvc.getOneByPsychAndDate(pid, date);

		List<Integer> availableHours = computeAvailableHours(slots, date);
		if (availableHours.isEmpty()) {
			model.addAttribute("errorMessage", "這一天已經沒有可預約的時段了。");
		}

		model.addAttribute("slots", slots);
		model.addAttribute("availableHours", availableHours);
		model.addAttribute("psychologist", psychologist);
		return "front-end/member/consultation/orders/bookInput";
	}

	@PostMapping("bookLookup")
	public String bookLookup(@RequestParam("psychId") String psychId, @RequestParam("slotDate") String slotDateStr,
			ModelMap model) {
		model.addAttribute("todayDate", java.time.LocalDate.now().toString());
		model.addAttribute("psychologistsList", getActivePsychologists());

		if (psychId == null || psychId.isBlank() || slotDateStr == null || slotDateStr.isBlank()) {
			model.addAttribute("errorMessage", "請輸入心理師編號與日期");
			return "front-end/member/consultation/orders/bookForm";
		}

		Integer pid = Integer.valueOf(psychId);
		LocalDate date = LocalDate.parse(slotDateStr);

		if (date.isBefore(LocalDate.now())) {
			model.addAttribute("errorMessage", "預約日期不能選過去的日期");
			return "front-end/member/consultation/orders/bookForm";
		}

		Psychologist psychologist = psychologistRepository.findById(pid).orElse(null);
		if (psychologist == null) {
			model.addAttribute("errorMessage", "查無此心理師");
			return "front-end/member/consultation/orders/bookForm";
		}

		Slots slots = slotsSvc.getOneByPsychAndDate(pid, date);
		if (slots == null) {
			model.addAttribute("errorMessage", "該心理師這天尚未開放預約");
			return "front-end/member/consultation/orders/bookForm";
		}

		List<Integer> availableHours = computeAvailableHours(slots, date);
		if (availableHours.isEmpty()) {
			model.addAttribute("errorMessage", "該心理師這天已無可預約時段");
			return "front-end/member/consultation/orders/bookForm";
		}

		model.addAttribute("slots", slots);
		model.addAttribute("availableHours", availableHours);
		model.addAttribute("psychologist", psychologist);
		return "front-end/member/consultation/orders/bookInput";
	}

	@PostMapping("bookSubmit")
	public String bookSubmit(@RequestParam("timeslotId") String timeslotId, @RequestParam(value = "hour", required = false) String hourStr,
			@RequestParam("psychId") String psychId,
			@RequestParam("psychLoc") String psychLoc, @RequestParam("psychFee") String psychFee,
			@RequestParam("visitPurpose") String visitPurpose,
			@RequestParam(value = "visitPurposeNote", required = false) String visitPurposeNote,
			@RequestParam("sessionType") String sessionType,
			@AuthenticationPrincipal MemberUserDetails prinUserDetails,
			ModelMap model) {

		if (prinUserDetails == null) {
			return "redirect:/front-end/login";
		}
		Integer memberId = prinUserDetails.getMember().getMemberId();
		
		if (hourStr == null || hourStr.isBlank()) {
			Slots slot0 = slotsSvc.getOneSlots(Integer.valueOf(timeslotId));
			Psychologist psy0 = psychologistRepository.findById(Integer.valueOf(psychId)).orElse(null);
			model.addAttribute("slots", slot0);
			model.addAttribute("availableHours", computeAvailableHours(slot0, slot0.getSlotDate()));
			model.addAttribute("psychologist", psy0);
			model.addAttribute("errorMessage", "請先選擇可預約時段再送出。");
			return "front-end/member/consultation/orders/bookInput";
		}

		int hour = Integer.parseInt(hourStr);
		Integer tid = Integer.valueOf(timeslotId);
		Integer pid = Integer.valueOf(psychId);

		Slots slot = slotsSvc.getOneSlots(tid);
		java.time.LocalDateTime consStart = slot.getSlotDate().atTime(hour, 0);

		// 防線：送出前再確認這個時段還沒過去
		if (consStart.isBefore(java.time.LocalDateTime.now())) {
			Psychologist psychologist = psychologistRepository.findById(pid).orElse(null);
			model.addAttribute("slots", slot);
			model.addAttribute("availableHours", computeAvailableHours(slot, slot.getSlotDate()));
			model.addAttribute("psychologist", psychologist);
			model.addAttribute("errorMessage", "這個時段已經過了，無法預約，請重新選擇。");
			return "front-end/member/consultation/orders/bookInput";
		}

		// 檢查：同一會員是否已經預約過「這位心理師的這個時段」（待確認或已確認）
		boolean duplicate = ordersSvc.getByMemberId(memberId).stream()
				.anyMatch(o -> o.getPsychologist() != null
						&& o.getPsychologist().getPsychId().equals(pid)
						&& o.getConsStart() != null
						&& o.getConsStart().equals(consStart)
						&& (o.getOrderStatus() == 0 || o.getOrderStatus() == 1));

		if (duplicate) {
			Psychologist psychologist = psychologistRepository.findById(pid).orElse(null);
			model.addAttribute("slots", slot);
			model.addAttribute("availableHours", computeAvailableHours(slot, slot.getSlotDate()));
			model.addAttribute("psychologist", psychologist);
			model.addAttribute("errorMessage", "你已經預約過這位心理師的這個時段了，請勿重複預約。");
			return "front-end/member/consultation/orders/bookInput";
		}

		Member member = new Member();
		member.setMemberId(memberId);

		Psychologist psychologist = new Psychologist();
		psychologist.setPsychId(pid);

		Orders orders = new Orders();
		orders.setSlot(slot);
		orders.setMember(member);
		orders.setPsychologist(psychologist);
		orders.setConsStart(consStart);
		orders.setConsEnd(slot.getSlotDate().atTime(hour + 1, 0));
		orders.setPsychLoc(psychLoc);
		orders.setPsychFee(Integer.valueOf(psychFee));
		orders.setVisitPurpose(visitPurpose);
		orders.setVisitPurposeNote(visitPurposeNote);
		orders.setSessionType(Integer.valueOf(sessionType));
		orders.setOrderStatus(0);
		orders.setGovSubsidy(false);

		ordersSvc.addOrders(orders);

		model.addAttribute("success", "預約成功！等待心理師確認。");
		return "front-end/member/consultation/orders/bookSuccess";
	}

	@GetMapping("myOrders")
	public String myOrders(@AuthenticationPrincipal MemberUserDetails prinUserDetails, ModelMap model) {
		if (prinUserDetails == null) {
			return "redirect:/front-end/login";
		}
		Integer memberId = prinUserDetails.getMember().getMemberId();
		List<Orders> list = ordersSvc.getByMemberId(memberId);

		// 問題回報狀態對照（原本就有）
		List<com.freemind.consultation.reports.model.Reports> myReports = reportsSvc.getByMemberId(memberId);
		java.util.Map<Integer, Integer> reportStatusMap = new java.util.HashMap<>();
		for (com.freemind.consultation.reports.model.Reports r : myReports) {
			if (r.getOrders() != null) {
				reportStatusMap.put(r.getOrders().getOrderId(), r.getReportStatus());
			}
		}

		// 心得評論的 3 天視窗
		java.time.LocalDateTime now = java.time.LocalDateTime.now();
		java.util.Set<Integer> canWriteReview = new java.util.HashSet<>();
		java.util.Set<Integer> canEditReview = new java.util.HashSet<>();
		for (Orders o : list) {
			// 可撰寫：已完成(4)、未評論、且諮商結束後 3 天內
			if (o.getOrderStatus() != null && o.getOrderStatus() == 4 && o.getReviewContent() == null
					&& o.getConsEnd() != null && now.isBefore(o.getConsEnd().plusDays(3))) {
				canWriteReview.add(o.getOrderId());
			}
			// 可修改：已評論、且送出評論後 3 天內
			if (o.getReviewContent() != null && o.getReviewedAt() != null
					&& now.isBefore(o.getReviewedAt().plusDays(3))) {
				canEditReview.add(o.getOrderId());
			}
		}

		model.addAttribute("ordersListData", list);
		model.addAttribute("reportStatusMap", reportStatusMap);
		model.addAttribute("canWriteReview", canWriteReview);
		model.addAttribute("canEditReview", canEditReview);
		model.addAttribute("memberId", memberId);
		return "front-end/member/consultation/orders/myOrdersList";
	}

	@GetMapping("reviewForm")
	public String reviewForm(@AuthenticationPrincipal MemberUserDetails prinUserDetails, ModelMap model) {
		if (prinUserDetails == null) {
			return "redirect:/front-end/login";
		}
		Integer memberId = prinUserDetails.getMember().getMemberId();
		List<Orders> list = ordersSvc.getCompletedUnratedByMemberId(memberId);
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
}