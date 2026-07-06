package com.freemind.consultation.slots.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.freemind.consultation.slots.model.Slots;
import com.freemind.consultation.slots.model.SlotsService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/slots")
public class SlotsController {

	@Autowired
	private SlotsService slotsSvc;

	@GetMapping("listAllSlots")
	public String listAllSlots(ModelMap model) {
		List<Slots> list = slotsSvc.getAll();
		model.addAttribute("slotsListData", list);
		return "back-end/consultation/slots/listAllSlots";
	}

	@GetMapping("addSlots")
	public String addSlots(ModelMap model) {
		Slots slots = new Slots();
		model.addAttribute("slots", slots);
		return "back-end/consultation/slots/addSlots";
	}

	@GetMapping("select_Page")
	public String select_Page(ModelMap model) {
		return "back-end/consultation/slots/select_Page";
	}

	@PostMapping("insert")
	public String insert(@Valid Slots slots, BindingResult result, ModelMap model) {
		if(result.hasErrors()) {
			return "back-end/consultation/slots/addSlots";
		}
		slotsSvc.addSlots(slots);
		return "redirect:/slots/listAllSlots";
	}

	@PostMapping("getOne_For_Update")
	public String getOne_For_Update(@RequestParam("timeslotId") String timeslotId, ModelMap model) {
		Slots slots = slotsSvc.getOneSlots(Integer.valueOf(timeslotId));
		model.addAttribute("slots", slots);
		return "back-end/consultation/slots/update_slots_input";
	}

	@PostMapping("update")
	public String update(@Valid Slots slots, BindingResult result, ModelMap model) {
		if(result.hasErrors()) {
			return "back-end/consultation/slots/update_slots_input";
		}
		slotsSvc.updateSlots(slots);

		model.addAttribute("success", "-(修改成功)");
		Slots updatedSlots = slotsSvc.getOneSlots(slots.getTimeslotId());
		model.addAttribute("slots", updatedSlots);
		return "back-end/consultation/slots/listOneSlots";
	}

	@PostMapping("delete")
	public String delete(@RequestParam("timeslotId") String timeslotId, ModelMap model) {
		slotsSvc.deleteSlots(Integer.valueOf(timeslotId));

		List<Slots> list = slotsSvc.getAll();
		model.addAttribute("slotsListData", list);
		model.addAttribute("success", "-(刪除成功)");
		return "back-end/consultation/slots/listAllSlots";
	}

	@PostMapping("getOne_For_Display")
	public String getOne_For_Display(@RequestParam("timeslotId") String timeslotId, ModelMap model) {
		Slots slots = slotsSvc.getOneSlots(Integer.valueOf(timeslotId));
		model.addAttribute("slots", slots);
		return "back-end/consultation/slots/select_Page";
	}

	@PostMapping("getByPsychId")
	public String getByPsychId(@RequestParam("psychId") String psychId, ModelMap model) {
		List<Slots> list = slotsSvc.getByPsychId(Integer.valueOf(psychId));
		model.addAttribute("slotsListData", list);
		return "back-end/consultation/slots/select_Page";
	}

	@PostMapping("getBySlotDate")
	public String getBySlotDate(@RequestParam("slotDate") String slotDate, ModelMap model) {
		LocalDate date = LocalDate.parse(slotDate);
		List<Slots> list = slotsSvc.getBySlotDate(date);
		model.addAttribute("slotsListData", list);
		return "back-end/consultation/slots/select_Page";
	}

	@PostMapping("getByConsStatus")
	public String getByConsStatus(@RequestParam("consStatus") String consStatus, ModelMap model) {
		List<Slots> list = slotsSvc.getByConsStatus(consStatus);
		model.addAttribute("slotsListData", list);
		return "back-end/consultation/slots/select_Page";
	}
		

}
