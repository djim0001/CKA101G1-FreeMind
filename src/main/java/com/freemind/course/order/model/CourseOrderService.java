package com.freemind.course.order.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.freemind.course.coupon.model.MemberCoupon;
import com.freemind.course.dto.CourseOrderSearchCondition;
import com.freemind.course.util.CourseOrderSpecification;
import com.freemind.login.member.model.Member;

import jakarta.transaction.Transactional;

@Service
public class CourseOrderService {

	private static final int PAGE_SIZE = 5;
	@Autowired
	private CourseOrderRepository repository;
	@Autowired
	private OrderDetailRepository orderDetailRepository;

	/**
	 * 功能一：用「訂單編號」查詢特定訂單
	 * 
	 * @param courseOrderId 訂單 ID
	 * @return 訂單物件（若找不到回傳 null）
	 */
	public CourseOrder getOrderById(Integer courseOrderId) {
		Optional<CourseOrder> orderOpt = repository.findById(courseOrderId);
		return orderOpt.orElse(null); // 找到就回傳，找不到就回傳 null
	}

	/**
	 * 功能二：用「會員」查詢他擁有哪些訂單 說明：查出來的每一個 CourseOrder 物件內，都已經包含您剛寫好的 paymentStatus（付款狀態）
	 * 
	 * @param member 會員物件
	 * @return 該會員的訂單列表
	 */
	public List<CourseOrder> getOrdersByMember(Member member) {
		return repository.findByMember(member);
	}

	public Page<CourseOrder> getOrdersByMember(Member member, Integer page) {

		if (page == null || page < 0) {
			page = 0;
		}

		Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by("orderedAt").descending());

		return repository.findByMember(member, pageable);
	}

	public void addOrder(CourseOrder courseOrder) {
		repository.save(courseOrder);
	}

	@Transactional
	public MemberCoupon getMemberCouponByOrderId(
	        Integer courseOrderId) {

	    CourseOrder order = repository
	            .findById(courseOrderId)
	            .orElseThrow(() ->
	                    new IllegalArgumentException("訂單不存在")
	            );

	    return order.getMemberCoupon();
	}

	public void payOrder(Integer courseOrderId) {
		CourseOrder order = getOrderById(courseOrderId);

		if (order.getPaymentStatus() == 0) {
			order.setPaymentStatus(1);
			repository.save(order);
		}
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
		case "orderIdAsc" -> Sort.by("orderId").ascending();

		case "orderIdDesc" -> Sort.by("orderId").descending();

		case "orderedAtAsc" -> Sort.by("orderedAt").ascending();

		case "orderedAtDesc" -> Sort.by("orderedAt").descending();

		case "totalAsc" -> Sort.by("total").ascending();

		case "totalDesc" -> Sort.by("total").descending();

		case "statusAsc" -> Sort.by("orderStatus").ascending();

		case "statusDesc" -> Sort.by("orderStatus").descending();

		case "memberIdAsc" -> Sort.by("member.memberId").ascending();

		case "memberIdDesc" -> Sort.by("member.memberId").descending();

		default -> Sort.by("orderedAt").descending();
		};

		Pageable pageable = PageRequest.of(page, PAGE_SIZE, sort);

		return repository.findAll(pageable);
	}
	
	@Transactional
    public void paymentSuccess(Integer courseOrderId) {

        CourseOrder order = repository.findById(courseOrderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("找不到訂單：" + courseOrderId));

        Integer oldPaymentStatus = order.getPaymentStatus();
        // 只允許從 0 轉成 1
        if (oldPaymentStatus != 0) {
        	throw new IllegalStateException(
        			"目前訂單狀態不允許付款，狀態為：" + (oldPaymentStatus==2?"已取消":"已付款"));
        }
        // 已付款就不重複處理
        if (oldPaymentStatus == 1) {
        	return;
        }
        // 修改付款狀態
        order.setPaymentStatus(1);
        // 開通此訂單的所有課程權限
        int updatedCount =
                orderDetailRepository.enableCoursePermission(courseOrderId);
        if (updatedCount == 0) {
            throw new IllegalStateException("訂單沒有任何訂單明細");
        }

        // 有 @Transactional 時，order 由 Persistence Context 管理，
        // 通常不必手動呼叫 save()
    }
	
	@Transactional
	public void cancelOrder(Integer courseOrderId) {
		CourseOrder order = repository.findById(courseOrderId)
                .orElseThrow(() ->
                        new IllegalArgumentException("找不到訂單：" + courseOrderId));

        Integer oldPaymentStatus = order.getPaymentStatus();
        
        if (oldPaymentStatus != 0) {
        	throw new IllegalStateException(
        			"目前訂單狀態無法取消，狀態為：" + (oldPaymentStatus==2?"已取消":"已付款"));
        }
     // 已付款就不重複處理
        if (oldPaymentStatus == 2) {
        	return;
        }
     // 修改付款狀態
        order.setPaymentStatus(2);
	}
	@Transactional
    public Page<CourseOrder> searchOrders(
            CourseOrderSearchCondition condition,
            Integer page) {

        if (condition == null) {
            condition = new CourseOrderSearchCondition();
        }

        // 前端頁碼從 1 開始
        if (page == null || page < 1) {
            page = 1;
        }

        Specification<CourseOrder> specification =
                CourseOrderSpecification
                        .keywordContains(condition.getKeyword())
                        .and(
                            CourseOrderSpecification.paymentStatusEquals(
                                condition.getPaymentStatus()
                            )
                        )
                        .and(
                            CourseOrderSpecification.orderedDateEquals(
                                condition.getOrderedDate()
                            )
                        );

        Pageable pageable = PageRequest.of(
                page - 1,
                PAGE_SIZE,
                Sort.by("orderedAt").descending()
        );

        return repository.findAll(specification, pageable);
    }

}
