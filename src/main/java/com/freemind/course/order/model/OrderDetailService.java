package com.freemind.course.order.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.freemind.course.order.model.OrderDetail.CompositeOrderDetail;

import jakarta.transaction.Transactional;

@Service
public class OrderDetailService {

	@Value("${app.course.page-size:5}")
	private int coursePageSize;

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
	
	@Transactional
    public List<OrderDetail> getOrderDetailsByCourseOrderId(
            Integer courseOrderId) {

        return repository.findByCourseOrderCourseOrderId(courseOrderId);
    }

	public Page<OrderDetail> getMyCourses(Integer memberId, Integer page, String orderBy) {

        if (page == null || page < 0) {
            page = 0;
        }

        if (orderBy == null || orderBy.isBlank()) {
            orderBy = "courseIdDesc";
        }

        Sort sort = switch (orderBy) {
            case "courseIdAsc" -> Sort.by("course.courseId").ascending();
            case "courseIdDesc" -> Sort.by("course.courseId").descending();
            case "priceAsc" -> Sort.by("price").ascending();
            case "priceDesc" -> Sort.by("price").descending();
            case "courseProgressAsc" -> Sort.by("courseProgress").ascending();
            case "courseProgressDesc" -> Sort.by("courseProgress").descending();
            default -> Sort.by("course.courseId").descending();
        };

        Pageable pageable = PageRequest.of(page, 10, sort);

        return repository.findByCourseOrderMemberMemberId(memberId, pageable);
    }
	
	public boolean hasCoursePermission(Integer memberId, Integer courseId) {
	    return repository.existsPermission(memberId, courseId);
	}
	
	public String addCourseToCart(Integer memberId, Integer courseId) {

		String addCourseToCart = "";
	    // 1. 檢查是否已購買
	    if (repository.existsPaidCourse(memberId, courseId)) {
	     	addCourseToCart = "您已經購買過這門課程";
	    }

	    // 2. 檢查是否有未付款訂單
	    if (repository.existsPendingCourse(memberId, courseId)) {
	    		addCourseToCart = "這門課程已有尚未完成的訂單";
	    }

	    	return addCourseToCart;
	}

}
