package com.freemind.consultation.slots.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.freemind.consultation.orders.model.OrdersService;
import com.freemind.consultation.slots.model.Slots;
import com.freemind.consultation.slots.model.SlotsService;
import com.freemind.login.psychologist.entity.Psychologist;

@Controller
@RequestMapping("/psych/slots")
public class SlotsPsychController {

	@Autowired
	private SlotsService slotsSvc;

	@Autowired
	private OrdersService ordersSvc;

	private static final LocalDate TEMPLATE_DATE = LocalDate.of(2000, 1, 1);

	@GetMapping("manageSlotsForm")
	public String manageSlotsForm(ModelMap model) {
		return "front-end/psych/consultation/slots/manageSlotsForm";
	}

	@PostMapping("manageLookup")
	public String manageLookup(@RequestParam("psychId") String psychId, @RequestParam("slotDate") String slotDateStr,
			ModelMap model) {
		if (psychId == null || psychId.isBlank() || slotDateStr == null || slotDateStr.isBlank()) {
			model.addAttribute("errorMessage", "請輸入心理師編號與日期");
			return "front-end/psych/consultation/slots/manageSlotsForm";
		}
		Integer pid = Integer.valueOf(psychId);
		LocalDate date = LocalDate.parse(slotDateStr);
		Slots slots = slotsSvc.getOneByPsychAndDate(pid, date);
		if (slots == null) {
			slots = new Slots();
			Psychologist p = new Psychologist();
			p.setPsychId(pid);
			slots.setPsychologist(p);
			slots.setSlotDate(date);
			slots.setConsStatus("0".repeat(24));
		}
		model.addAttribute("slots", slots);
		return "front-end/psych/consultation/slots/manageSlotsInput";
	}

	@PostMapping("manageSubmit")
	public String manageSubmit(@RequestParam("psychId") String psychId, @RequestParam("slotDate") String slotDateStr,
			@RequestParam(value = "openHours", required = false) List<Integer> openHours, ModelMap model) {
		Integer pid = Integer.valueOf(psychId);
		LocalDate date = LocalDate.parse(slotDateStr);
		Slots slots = slotsSvc.getOneByPsychAndDate(pid, date);
		boolean isNew = (slots == null);
		if (isNew) {
			slots = new Slots();
			Psychologist p = new Psychologist();
			p.setPsychId(pid);
			slots.setPsychologist(p);
			slots.setSlotDate(date);
		}
		String currentStatus = isNew ? "0".repeat(24) : slots.getConsStatus();
		StringBuilder sb = new StringBuilder(currentStatus);
		List<Integer> blockedHours = new java.util.ArrayList<>();
		for (int h = 0; h < 24; h++) {
			if (sb.charAt(h) == '2') {
				continue;
			}
			boolean checked = openHours != null && openHours.contains(h);
			if (!checked && (sb.charAt(h) == '1' || sb.charAt(h) == '4') && !isNew
					&& ordersSvc.hasPendingOrder(slots.getTimeslotId(), date.atTime(h, 0))) {
				blockedHours.add(h);
				continue;
			}
			sb.setCharAt(h, checked ? '4' : '3');
		}
		slots.setConsStatus(sb.toString());
		if (isNew) {
			slotsSvc.addSlots(slots);
		} else {
			slotsSvc.updateSlots(slots);
		}
		if (!blockedHours.isEmpty()) {
			model.addAttribute("warningMessage", "以下小時有待確認的訂單，暫時無法關閉：" + blockedHours);
		}
		model.addAttribute("success", "時段設定已更新！");
		return "front-end/psych/consultation/slots/manageSlotsSuccess";
	}

	@GetMapping("templateForm")
	public String templateForm(ModelMap model) {
		return "front-end/psych/consultation/slots/templateForm";
	}

	@PostMapping("templateLookup")
	public String templateLookup(@RequestParam("psychId") String psychId, ModelMap model) {
		if (psychId == null || psychId.isBlank()) {
			model.addAttribute("errorMessage", "請輸入心理師編號");
			return "front-end/psych/consultation/slots/templateForm";
		}

		Integer pid = Integer.valueOf(psychId);

		Slots template = slotsSvc.getOneByPsychAndDate(pid, TEMPLATE_DATE);
		if (template == null) {
			template = new Slots();
			Psychologist p = new Psychologist();
			p.setPsychId(pid);
			template.setPsychologist(p);
			template.setSlotDate(TEMPLATE_DATE);
			template.setConsStatus("0".repeat(24));
		}

		model.addAttribute("slots", template);
		return "front-end/psych/consultation/slots/templateInput";
	}

	@PostMapping("templateSubmit")
	public String templateSubmit(@RequestParam("psychId") String psychId,
			@RequestParam(value = "openHours", required = false) List<Integer> openHours,
			ModelMap model) {
		Integer pid = Integer.valueOf(psychId);

		Slots template = slotsSvc.getOneByPsychAndDate(pid, TEMPLATE_DATE);
		boolean isNew = (template == null);

		if (isNew) {
			template = new Slots();
			Psychologist p = new Psychologist();
			p.setPsychId(pid);
			template.setPsychologist(p);
			template.setSlotDate(TEMPLATE_DATE);
		}

		StringBuilder sb = new StringBuilder("0".repeat(24));
		for (int h = 0; h < 24; h++) {
			boolean checked = openHours != null && openHours.contains(h);
			sb.setCharAt(h, checked ? '1' : '0');
		}
		template.setConsStatus(sb.toString());

		if (isNew) {
			slotsSvc.addSlots(template);
		} else {
			slotsSvc.updateSlots(template);
		}

		model.addAttribute("success", "固定範本已更新！之後排程會依此規則自動產生時段。");
		return "front-end/psych/consultation/slots/templateSuccess";
	}

	@PostMapping("reapplyTemplate")
	public String reapplyTemplate(@RequestParam("psychId") String psychId, ModelMap model) {
		slotsSvc.reapplyTemplateToNext14Days(Integer.valueOf(psychId));
		model.addAttribute("success", "已將新範本套用到未來14天！已預約的時段不受影響。");
		return "front-end/psych/consultation/slots/templateSuccess";
	}
}