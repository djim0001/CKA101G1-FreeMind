package com.freemind.course.order.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.freemind.login.member.model.Member;



@Service
public class CourseOrderService {

	private static final int PAGE_SIZE = 10;
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
    public Page<CourseOrder> getOrdersByMember(
            Member member,
            Integer page) {

        if (page == null || page < 0) {
            page = 0;
        }

        Pageable pageable = PageRequest.of(
                page,
                PAGE_SIZE,
                Sort.by("orderedAt").descending()
        );

        return repository.findByMember(member, pageable);
    }
    
    public void addOrder(CourseOrder courseOrder) {
    		repository.save(courseOrder);
    }
    
    public Page<CourseOrder> getAllOrder(int page, String orderBy) {

        // Thymeleaf 頁碼通常從 1 開始，
        // Spring Data JPA 的 PageRequest 從 0 開始
        if (page < 1) {
            page = 0;
        }

        if (orderBy == null || orderBy.isBlank()) {
            orderBy = "orderedAtDesc";
        }

        Sort sort = switch (orderBy) {
            case "orderIdAsc" ->
                Sort.by("orderId").ascending();

            case "orderIdDesc" ->
                Sort.by("orderId").descending();

            case "orderedAtAsc" ->
                Sort.by("orderedAt").ascending();

            case "orderedAtDesc" ->
                Sort.by("orderedAt").descending();

            case "totalAsc" ->
                Sort.by("total").ascending();

            case "totalDesc" ->
                Sort.by("total").descending();

            case "statusAsc" ->
                Sort.by("orderStatus").ascending();

            case "statusDesc" ->
                Sort.by("orderStatus").descending();

            case "memberIdAsc" ->
                Sort.by("member.memberId").ascending();

            case "memberIdDesc" ->
                Sort.by("member.memberId").descending();

            default ->
                Sort.by("orderedAt").descending();
        };

        Pageable pageable = PageRequest.of(
            page,
            PAGE_SIZE,
            sort
        );

        return repository.findAll(pageable);
    }

	
	
	
	
	
	
	
	
	
	
	
	
}
