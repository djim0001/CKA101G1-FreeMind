package com.freemind.consultation.slots.controller;

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

import com.freemind.consultation.slots.model.Slots;
import com.freemind.consultation.slots.model.SlotsService;
import com.freemind.login.psychologist.entity.Psychologist;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/slots")
public class SlotsController {

	@Autowired
	private SlotsService slotsSvc;

	@InitBinder
	public void initBinder(WebDataBinder binder) {
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
		if (timeslotId == null || timeslotId.isBlank()) {
			model.addAttribute("errorMessage", "請輸入時段編號");
			return "back-end/consultation/slots/select_Page";
		}
		
		Slots slots = slotsSvc.getOneSlots(Integer.valueOf(timeslotId));
		model.addAttribute("slots", slots);
		return "back-end/consultation/slots/select_Page";
	}

	@PostMapping("getByPsychId")
	public String getByPsychId(@RequestParam("psychId") String psychId, ModelMap model) {
		if (psychId == null || psychId.isBlank()) {
			model.addAttribute("errorMessage", "請輸入心理師編號");
			return "back-end/consultation/slots/select_Page";
		}
		List<Slots> list = slotsSvc.getByPsychId(Integer.valueOf(psychId));
		model.addAttribute("slotsListData", list);
		return "back-end/consultation/slots/select_Page";
	}

	@PostMapping("getBySlotDate")
	public String getBySlotDate(@RequestParam("slotDate") String slotDate, ModelMap model) {
		if (slotDate == null || slotDate.isBlank()) {
			model.addAttribute("errorMessage", "請選擇預約日期");
			return "back-end/consultation/slots/select_Page";
		}
		LocalDate date = LocalDate.parse(slotDate);
		List<Slots> list = slotsSvc.getBySlotDate(date);
		model.addAttribute("slotsListData", list);
		return "back-end/consultation/slots/select_Page";
	}

	@PostMapping("getByConsStatus")
	public String getByConsStatus(@RequestParam("consStatus") String consStatus, ModelMap model) {
		if (consStatus == null || consStatus.isBlank()) {
			model.addAttribute("errorMessage", "請輸入預約狀態（24碼）");
			return "back-end/consultation/slots/select_Page";
		}
		List<Slots> list = slotsSvc.getByConsStatus(consStatus);
		model.addAttribute("slotsListData", list);
		return "back-end/consultation/slots/select_Page";
	}
	
	// ===== 心理師：查詢/調整某天的可預約時段 =====
	
		@GetMapping("manageSlotsForm")
		public String manageSlotsForm(ModelMap model) {
			return "front-end/consultation/slots/manageSlotsForm";
		}
		
		@PostMapping("manageLookup")
		public String manageLookup(@RequestParam("psychId") String psychId,
		                            @RequestParam("slotDate") String slotDateStr, ModelMap model) {
			if (psychId == null || psychId.isBlank() || slotDateStr == null || slotDateStr.isBlank()) {
				model.addAttribute("errorMessage", "請輸入心理師編號與日期");
				return "front-end/consultation/slots/manageSlotsForm";
			}
			
			Integer pid = Integer.valueOf(psychId);
			LocalDate date = LocalDate.parse(slotDateStr);
			
			Slots slots = slotsSvc.getOneByPsychAndDate(pid, date);
			if (slots == null) {
				// 這一天還沒有任何記錄，建立一筆全新的（預設全部不可預約）
				slots = new Slots();
				Psychologist p = new Psychologist();
				p.setPsychId(pid);
				slots.setPsychologist(p);
				slots.setSlotDate(date);
				slots.setConsStatus("0".repeat(24));
			}
			
			model.addAttribute("slots", slots);
			return "front-end/consultation/slots/manageSlotsInput";
		}
		
		@PostMapping("manageSubmit")
		public String manageSubmit(@RequestParam("psychId") String psychId,
		                            @RequestParam("slotDate") String slotDateStr,
		                            @RequestParam(value = "openHours", required = false) List<Integer> openHours,
		                            ModelMap model) {
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
			
			for (int h = 0; h < 24; h++) {
				if (sb.charAt(h) == '2') {
					continue; // 已預約成立的時段，不可被調整
				}
				boolean checked = openHours != null && openHours.contains(h);
				sb.setCharAt(h, checked ? '1' : '0');
			}
			slots.setConsStatus(sb.toString());
			
			if (isNew) {
				slotsSvc.addSlots(slots);
			} else {
				slotsSvc.updateSlots(slots);
			}
			
			model.addAttribute("success", "時段設定已更新！");
			return "front-end/consultation/slots/manageSlotsSuccess";
		}
		

}
