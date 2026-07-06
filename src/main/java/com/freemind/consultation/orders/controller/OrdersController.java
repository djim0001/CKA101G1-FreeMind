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

import jakarta.validation.Valid;

@Controller
@RequestMapping("/orders")
public class OrdersController {

	@Autowired
	private OrdersService ordersSvc;

	@Autowired
	private SlotsService slotsSvc;
	
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
	    if(timeslotId == null) {
	        model.addAttribute("slotError", "請選擇時段");
	        model.addAttribute("slotsList", slotsSvc.getAll());
	        return "back-end/consultation/orders/addOrders";
	    }
	    
	    if(result.hasErrors()) {
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
		if(result.hasErrors()) {
			model.addAttribute("slotsList", slotsSvc.getAll());
			return "back-end/consultation/orders/update_orders_input"; //如果驗證有錯誤，回到修改表單頁面
		}
		
		Slots slot = slotsSvc.getOneSlots(timeslotId);
		orders.setSlot(slot);
		ordersSvc.updateOrders(orders);//呼叫Service修改資料
		
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
			return "front-end/consultation/orders/bookForm";
		}
		
		@PostMapping("bookLookup")
		public String bookLookup(@RequestParam("psychId") String psychId,
		                          @RequestParam("slotDate") String slotDateStr, ModelMap model) {
			if (psychId == null || psychId.isBlank() || slotDateStr == null || slotDateStr.isBlank()) {
				model.addAttribute("errorMessage", "請輸入心理師編號與日期");
				return "front-end/consultation/orders/bookForm";
			}
			
			Integer pid = Integer.valueOf(psychId);
			LocalDate date = LocalDate.parse(slotDateStr);
			
			Slots slots = slotsSvc.getOneByPsychAndDate(pid, date);
			if (slots == null) {
				model.addAttribute("errorMessage", "該心理師這天尚未開放預約");
				return "front-end/consultation/orders/bookForm";
			}
			
			List<Integer> availableHours = new java.util.ArrayList<>();
			String status = slots.getConsStatus();
			for (int h = 0; h < 24; h++) {
				if (status.charAt(h) == '1') {
					availableHours.add(h);
				}
			}
			
			if (availableHours.isEmpty()) {
				model.addAttribute("errorMessage", "該心理師這天已無可預約時段");
				return "front-end/consultation/orders/bookForm";
			}
			
			model.addAttribute("slots", slots);
			model.addAttribute("availableHours", availableHours);
			return "front-end/consultation/orders/bookInput";
		}
		
		@PostMapping("bookSubmit")
		public String bookSubmit(@RequestParam("timeslotId") String timeslotId,
		                          @RequestParam("hour") String hourStr,
		                          @RequestParam("memberId") String memberId,
		                          @RequestParam("psychId") String psychId,
		                          @RequestParam("psychLoc") String psychLoc,
		                          @RequestParam("psychFee") String psychFee,
		                          @RequestParam("visitPurpose") String visitPurpose,
		                          @RequestParam(value = "visitPurposeNote", required = false) String visitPurposeNote,
		                          @RequestParam("sessionType") String sessionType,
		                          ModelMap model) {
			
			int hour = Integer.parseInt(hourStr);
			Integer tid = Integer.valueOf(timeslotId);
			
			// 先嘗試鎖定時段：1 -> 2
			boolean locked = slotsSvc.updateHourStatus(tid, hour, '1', '2');
			if (!locked) {
				model.addAttribute("errorMessage", "很抱歉，這個時段剛被其他人預約走了，請重新選擇。");
				return "redirect:/orders/bookForm";
			}
			
			// 鎖定成功，建立訂單
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
			return "front-end/consultation/orders/bookSuccess";
		}
}
