package com.freemind.course.order.model;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.freemind.course.order.model.OrderDetail.CompositeOrderDetail;
import com.freemind.login.member.model.Member;

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

	public OrderDetail getOneOrderDetailByPK(CompositeOrderDetail compositeOrderDetail) {
		Optional<OrderDetail> optional = repository.findById(compositeOrderDetail);
		return optional.orElse(null);
	}

	public List<OrderDetail> getAllOrderDetail() {
		return repository.findAll();
	}
	
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

        Pageable pageable = PageRequest.of(page, coursePageSize, sort);

        return repository.findByCourseOrderMemberMemberId(memberId, pageable);
    }
	
	public boolean hasCoursePermission(Integer memberId, Integer courseId) {
	    return repository.existsPermission(memberId, courseId);
	}
	
	public String canCourseToCart(Integer memberId, Integer courseId) {

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
	
	public Page<OrderDetail> getAccessibleOrderDetails(
	        Member member,
	        int page) {
		if(page < 0) page = 0;

	    Pageable pageable = PageRequest.of(
	            page,
	            coursePageSize,
	            Sort.by("course.courseId").descending()
	    );

	    return repository
	            .findAccessibleOrderDetailsByMember(member, pageable);
	}
	
	public OrderDetail getAccessibleOrderDetail(
	        Integer courseId,
	        Member member
	) {
	    return repository
	            .findFirstByCourseCourseIdAndCourseOrderMemberAndCourseOrderPaymentStatusAndCoursePermissionOrderByCourseOrderOrderedAtDesc(
	                    courseId,
	                    member,
	                    (byte) 1,
	                    (byte) 1
	            )
	            .orElse(null);
	}
	
	public List<OrderDetail> getReviewableCoursesByMemberId(Integer memberId) {

	    if (memberId == null) {
	        throw new IllegalArgumentException("會員編號不能為空");
	    }

	    byte paid = 1;
	    byte unlocked = 1;

	    return repository
	            .findByCourseOrderMemberMemberIdAndCourseOrderPaymentStatusAndCoursePermissionAndReviewedAtIsNull(
	                    memberId,
	                    paid,
	                    unlocked
	            );
	}

	public Page<OrderDetail> getReviewCoursesByMemberId(Integer memberId, int page) {
		
		if (memberId == null) {
			throw new IllegalArgumentException("會員編號不能為空");
		}
		if(page < 0) page = 0;
		byte paid = 1;
		byte unlocked = 1;
		
		Pageable pageable = PageRequest.of(
	            page,
	            coursePageSize,
	            Sort.by("reviewedAt").descending()
	    );
		
		return repository
				.findByCourseOrderMemberMemberIdAndCourseOrderPaymentStatusAndCoursePermissionAndReviewedAtNotNull(
						memberId,
						paid,
						unlocked,
						pageable
						);
	}
	
	@Transactional
	public void updatePlaybackPosition(
	        Integer memberId,
	        Integer courseOrderId,
	        Integer courseId,
	        Long playbackSeconds) {

	    if (courseOrderId == null) {
	        throw new IllegalArgumentException(
	                "訂單編號不能為空"
	        );
	    }

	    if (courseId == null) {
	        throw new IllegalArgumentException(
	                "課程編號不能為空"
	        );
	    }

	    if (
	        playbackSeconds == null ||
	        playbackSeconds < 0
	    ) {
	        throw new IllegalArgumentException(
	                "播放時間格式錯誤"
	        );
	    }

	    /*
	     * Java LocalTime 只能表示一天內的時間，
	     * 最大值不能達到 86400 秒。
	     */
	    if (playbackSeconds >= 86400) {
	        throw new IllegalArgumentException(
	                "使用 LocalTime 時，影片時間不能超過 24 小時"
	        );
	    }

	    OrderDetail orderDetail =
	            repository.findAccessibleOrderDetail(
	                    memberId,
	                    courseOrderId,
	                    courseId
	            )
	            .orElseThrow(() ->
	                    new IllegalArgumentException(
	                            "找不到訂單明細或沒有觀看權限"
	                    )
	            );

	    LocalTime playbackPosition =
	            LocalTime.ofSecondOfDay(playbackSeconds);

	    orderDetail.setPlaybackPosition(playbackPosition);
	}
}
