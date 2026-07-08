package com.freemind.consultation.orders.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.freemind.consultation.slots.model.SlotsService;

@Service
public class OrdersService {

	@Autowired
	OrdersRepository repository;
	
	public void addOrders(Orders orders) {
		orders.setCreatedAt(LocalDateTime.now()); // 建立時間自動帶入
		// consStart 和 consEnd 要根據選擇的時段帶入
	    // 暫時先用 LocalDateTime.now() 測試
		orders.setConsStart(LocalDateTime.now());
		orders.setConsEnd(LocalDateTime.now().plusHours(1));
		repository.save(orders);
	}
	
	public void updateOrders(Orders orders) {
		repository.save(orders);
	}
	
	public void deleteOrders(Integer orderId) {
		if(repository.existsById(orderId))
			repository.deleteById(orderId);
	}

	public Orders getOneOrders(Integer orderId) {
		Optional<Orders> optional = repository.findById(orderId);
		return optional.orElse(null);
	}
	
	public List<Orders> getAll(){
		return repository.findAll();
	}
	
	public List<Orders> getByMemberId(Integer memberId){
		return repository.findByMemberId(memberId);
	}
	
	public List<Orders> getByPsychId(Integer psychId){
		return repository.findByPsychId(psychId);
	}
	
	public List<Orders> getByOrderStatus(Integer orderStatus){
		return repository.findByOrderStatus(orderStatus);
	}
	
	public List<Orders> getByGovSubsidy(Boolean govSubsidy){
		return repository.findByGovSubsidy(govSubsidy);
	}
	
	public List<Orders> getBySessionType(Integer sessionType){
		return repository.findBySessionType(sessionType);
	}
	
	public List<Orders> getBySlotDate(java.time.LocalDate slotDate){
		return repository.findBySlotDate(slotDate);
	}
	
	public List<Orders> getPendingOrdersByPsychId(Integer psychId) {
		return repository.findByPsychIdAndOrderStatus(psychId, 0); // 0 = 待確認
	}
	
	public void approveOrder(Integer orderId) {
		Orders orders = getOneOrders(orderId);
		if (orders != null) {
			orders.setOrderStatus(1); // 已確認
			updateOrders(orders);
		}
	}
	
	public void rejectOrder(Integer orderId, SlotsService slotsSvc) {
		Orders orders = getOneOrders(orderId);
		if (orders != null) {
			orders.setOrderStatus(2); // 已取消
			updateOrders(orders);
			
			// 釋放時段：把該小時從 2(已預約) 改回 1(可預約)
			int hour = orders.getConsStart().getHour();
			slotsSvc.updateHourStatus(orders.getSlot().getTimeslotId(), hour, '2', '1');
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
