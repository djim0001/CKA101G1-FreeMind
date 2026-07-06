package com.freemind.course.order.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freemind.login.member.model.Member;

public interface CourseOrderRepository 
extends JpaRepository<CourseOrder, Integer> {

	
	 List<CourseOrder> findByMember(Member member);
	
	
	
}
