package com.freemind.consultation.orders.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Integer>{
	
	@Query("from Orders where member.memberId = ?1")
	List<Orders> findByMemberId(Integer memberId);
	
	@Query("from Orders where psychId = ?1")
	List<Orders> findByPsychId(Integer psychId);

	@Query("from Orders where orderStatus = ?1")
	List<Orders> findByOrderStatus(Integer orderStatus);
	
	@Query("from Orders where govSubsidy = ?1")
	List<Orders> findByGovSubsidy(Boolean govSubsidy);
	
	@Query("from Orders where sessionType = ?1")
	List<Orders> findBySessionType(Integer sessionType);
	
	@Query("from Orders o where o.slot.slotDate = ?1")
	List<Orders> findBySlotDate(java.time.LocalDate slotDate);

}

