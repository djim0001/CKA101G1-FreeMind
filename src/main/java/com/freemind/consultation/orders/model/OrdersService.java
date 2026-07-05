package com.freemind.consultation.orders.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
	
	
	
	
	
	
	
	
	
	
	
	
	
}
