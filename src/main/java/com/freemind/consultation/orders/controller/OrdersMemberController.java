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

	private List<Psychologist> getActivePsychologists() {
		return psychologistRepository.findAll().stream()
				.filter(p -> p.getAccountStatus() != null && p.getAccountStatus() == 1)
				.collect(java.util.stream.Collectors.toList());
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

		List<Integer> availableHours = new java.util.ArrayList<>();
		String status = slots.getConsStatus();
		for (int h = 0; h < 24; h++) {
			if (status.charAt(h) == '1' || status.charAt(h) == '4') {
				availableHours.add(h);
			}
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

		model.addAttribute("slots", slots);
		model.addAttribute("availableHours", availableHours);
		model.addAttribute("psychologist", psychologist);
		return "front-end/member/consultation/orders/bookInput";
	}

	@PostMapping("bookSubmit")
	public String bookSubmit(@RequestParam("timeslotId") String timeslotId, @RequestParam("hour") String hourStr,
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

		int hour = Integer.parseInt(hourStr);
		Integer tid = Integer.valueOf(timeslotId);

		Slots slot = slotsSvc.getOneSlots(tid);

		Member member = new Member();
		member.setMemberId(memberId);

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
		model.addAttribute("ordersListData", list);
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