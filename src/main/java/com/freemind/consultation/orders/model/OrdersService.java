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
}
