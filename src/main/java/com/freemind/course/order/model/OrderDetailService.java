package com.freemind.course.order.model;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.freemind.course.course.model.CourseRepository;

//@Service
public class OrderDetailService {
	
	@Autowired
	OrderDetailRepository repository;

	@Autowired
	private SessionFactory sessionFactory;
	
	public void addOrderDetail(OrderDetail orderDetail) {
//		repository.save(orderDetail);
	}

}
