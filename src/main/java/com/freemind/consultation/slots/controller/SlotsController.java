package com.freemind.consultation.slots.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.freemind.login.psychologist.entity.Psychologist;

@Controller
@RequestMapping("/admin/slots")
public class SlotsController {

	@Autowired
	private com.freemind.login.psychologist.repository.PsychologistRepository psychologistRepository;

	@Autowired
	private com.freemind.consultation.orders.model.OrdersService ordersSvc;

	@GetMapping("listAllSlots")
	public String listAllSlots(@RequestParam(value = "psychId", required = false) String psychId,
			@RequestParam(value = "month", required = false) String month, ModelMap model) {

		model.addAttribute("psychologistsList", psychologistRepository.findAll());
		model.addAttribute("selectedPsychId", psychId);

		int year = LocalDate.now().getYear();
		int monthVal = (month != null && !month.isBlank()) ? Integer.parseInt(month) : LocalDate.now().getMonthValue();
		model.addAttribute("selectedMonth", monthVal);

		if (psychId != null && !psychId.isBlank()) {
			Integer pid = Integer.valueOf(psychId);
			model.addAttribute("psychologist", psychologistRepository.findById(pid).orElse(null));

			LocalDate first = LocalDate.of(year, monthVal, 1);

			// 月初前的空白格（週一開頭）
			List<Integer> blanks = new java.util.ArrayList<>();
			for (int i = 0; i < first.getDayOfWeek().getValue() - 1; i++) {
				blanks.add(i);
			}
			model.addAttribute("blanks", blanks);

			// 該月所有日期
			List<LocalDate> dateList = new java.util.ArrayList<>();
			for (int i = 0; i < first.lengthOfMonth(); i++) {
				dateList.add(first.plusDays(i));
			}
			model.addAttribute("dateList", dateList);

			// 訂單依日期分組（key = yyyy-MM-dd）
			java.util.Map<String, java.util.List<com.freemind.consultation.orders.model.Orders>> ordersByDate =
					new java.util.LinkedHashMap<>();
			for (com.freemind.consultation.orders.model.Orders o : ordersSvc.getByPsychId(pid)) {
				if (o.getConsStart() != null && o.getConsStart().getYear() == year
						&& o.getConsStart().getMonthValue() == monthVal) {
					String key = o.getConsStart().toLocalDate().toString();
					ordersByDate.computeIfAbsent(key, k -> new java.util.ArrayList<>()).add(o);
				}
			}
			model.addAttribute("ordersByDate", ordersByDate);
		}

		return "back-end/consultation/slots/listAllSlots";
	}
}