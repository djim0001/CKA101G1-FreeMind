package com.freemind.course.order.model;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.freemind.course.course.model.Course;
import com.freemind.course.order.model.OrderDetail.CompositeOrderDetail;

@Service
public class OrderDetailService {
	
	private final OrderDetailRepository repository;
	
	public OrderDetailService(OrderDetailRepository repository) {
		this.repository = repository;
	}
	
	public void addOrderDetail(OrderDetail orderDetail) {
		repository.save(orderDetail);
	}
	public void updateOrderDetail(OrderDetail orderDetail) {
		repository.save(orderDetail);
	}
	public OrderDetail getOneOrderDetail(CompositeOrderDetail compositeOrderDetail) {
		Optional<OrderDetail> optional = repository.findById(compositeOrderDetail);
		return optional.orElse(null);
	}
	public List<OrderDetail> getAllOrderDetail() {
		return repository.findAll();
	}

}
