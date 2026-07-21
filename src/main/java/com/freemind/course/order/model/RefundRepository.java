package com.freemind.course.order.model;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freemind.course.order.model.Refund.CompositeRefund;
import com.freemind.login.member.model.Member;

@Repository
public interface RefundRepository extends JpaRepository<Refund, CompositeRefund> {
	List<Refund> findByMember(Member member);

	List<Refund> findByRefundStatus(Integer refundStatus);
	
	
}






