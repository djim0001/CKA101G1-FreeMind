package com.freemind.consultation.orders.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Integer>{
	
	@Query("from Orders where member.memberId = ?1")
	List<Orders> findByMemberId(Integer memberId);
	
	@Query("from Orders where psychologist.psychId = ?1")
	List<Orders> findByPsychId(Integer psychId);

	@Query("from Orders where orderStatus = ?1")
	List<Orders> findByOrderStatus(Integer orderStatus);
	
	@Query("from Orders where govSubsidy = ?1")
	List<Orders> findByGovSubsidy(Boolean govSubsidy);
	
	@Query("from Orders where sessionType = ?1")
	List<Orders> findBySessionType(Integer sessionType);
	
	@Query("from Orders o where o.slot.slotDate = ?1")
	List<Orders> findBySlotDate(java.time.LocalDate slotDate);
	
	@Query("from Orders where psychologist.psychId = ?1 and orderStatus = ?2")
	List<Orders> findByPsychIdAndOrderStatus(Integer psychId, Integer orderStatus);

	@Query("from Orders where slot.timeslotId = ?1 and consStart = ?2 and orderStatus = 0")
	List<Orders> findPendingBySlotAndConsStart(Integer timeslotId, java.time.LocalDateTime consStart);
	
	//心理師查看已確認的訂單
	@Query("from Orders where psychologist.psychId = ?1 and orderStatus = 1")
	List<Orders> findConfirmedByPsychId(Integer psychId);

	@Query("from Orders where member.memberId = ?1 and orderStatus = 4 and rating is null")
	List<Orders> findCompletedUnratedByMemberId(Integer memberId);

	@Query("from Orders where member.memberId = ?1 and (orderStatus = 1 or orderStatus = 4)")
	List<Orders> findConfirmedOrCompletedByMemberId(Integer memberId);
	
	
	
	
	
	
	//心理師更新時間後處理已有預約訂單 
		List<Orders> findByPsychologist_PsychIdAndOrderStatusInAndConsStartGreaterThanEqual(Integer psychId, List<Integer> orderStatuses, LocalDateTime consStart);

}

