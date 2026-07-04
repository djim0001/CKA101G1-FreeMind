package com.freemind.consultation.orders.controller;

import java.beans.PropertyEditorSupport;
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
	
}
