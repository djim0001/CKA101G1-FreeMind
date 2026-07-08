package com.freemind.course.order.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.freemind.login.member.model.Member;



@Service
public class CourseOrderService {

	
	@Autowired
	private CourseOrderRepository repository;
	

    /**
     * 功能一：用「訂單編號」查詢特定訂單
     * @param courseOrderId 訂單 ID
     * @return 訂單物件（若找不到回傳 null）
     */
    public CourseOrder getOrderById(Integer courseOrderId) {
    	Optional<CourseOrder> orderOpt = repository.findById(courseOrderId);
        return orderOpt.orElse(null); // 找到就回傳，找不到就回傳 null
    }

    /**
     * 功能二：用「會員」查詢他擁有哪些訂單
     * 說明：查出來的每一個 CourseOrder 物件內，都已經包含您剛寫好的 paymentStatus（付款狀態）
     * @param member 會員物件
     * @return 該會員的訂單列表
     */
    public List<CourseOrder> getOrdersByMember(Member member) {
    	return repository.findByMember(member);
    }
    
    public void addOrder(CourseOrder courseOrder) {
    		repository.save(courseOrder);
    }

	
	
	
	
	
	
	
	
	
	
	
	
}
