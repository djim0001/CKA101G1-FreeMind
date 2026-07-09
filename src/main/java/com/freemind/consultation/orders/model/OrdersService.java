package com.freemind.consultation.orders.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.freemind.consultation.slots.model.SlotsService;

@Service
public class OrdersService {
	@Autowired
	OrdersRepository repository;
	
	public void addOrders(Orders orders) {
		orders.setCreatedAt(LocalDateTime.now());
		repository.save(orders);
	}
	
	public void updateOrders(Orders orders) {
		repository.save(orders);
	}
	
	public void deleteOrders(Integer orderId) {
		if(repository.existsById(orderId))
			repository.deleteById(orderId);
	}
	public Orders getOneOrders(Integer orderId) {
		Optional<Orders> optional = repository.findById(orderId);
		return optional.orElse(null);
	}
	
	public List<Orders> getAll(){
		return repository.findAll();
	}
	
	public List<Orders> getByMemberId(Integer memberId){
		return repository.findByMemberId(memberId);
	}
	
	public List<Orders> getByPsychId(Integer psychId){
		return repository.findByPsychId(psychId);
	}
	
	public List<Orders> getByOrderStatus(Integer orderStatus){
		return repository.findByOrderStatus(orderStatus);
	}
	
	public List<Orders> getByGovSubsidy(Boolean govSubsidy){
		return repository.findByGovSubsidy(govSubsidy);
	}
	
	public List<Orders> getBySessionType(Integer sessionType){
		return repository.findBySessionType(sessionType);
	}
	
	public List<Orders> getBySlotDate(java.time.LocalDate slotDate){
		return repository.findBySlotDate(slotDate);
	}
	
	public List<Orders> getPendingOrdersByPsychId(Integer psychId) {
		return repository.findByPsychIdAndOrderStatus(psychId, 0);
	}
	
	// 心理師同意：鎖定時段 + 取消同時段其他待確認訂單
	public void approveOrder(Integer orderId, SlotsService slotsSvc) {
		Orders orders = getOneOrders(orderId);
		if (orders != null) {
			// 1. 找出同一時段(slot + 小時)其他待確認的訂單
			List<Orders> competitors = repository.findPendingBySlotAndConsStart(
					orders.getSlot().getTimeslotId(), orders.getConsStart());
			
			// 2. 把這筆訂單設為已確認
			orders.setOrderStatus(1);
			updateOrders(orders);
			
			// 3. 鎖定時段：1 -> 2
			int hour = orders.getConsStart().getHour();
			slotsSvc.updateHourStatus(orders.getSlot().getTimeslotId(), hour, '1', '2');
			
			// 4. 其餘同時段待確認訂單，全部自動取消（排除自己這筆）
			for (Orders competitor : competitors) {
				if (!competitor.getOrderId().equals(orderId)) {
					competitor.setOrderStatus(2); // 已取消
					updateOrders(competitor);
				}
			}
		}
	}
	
	// 心理師拒絕：純取消，不需釋放時段（因為本來就沒鎖定）
	public void rejectOrder(Integer orderId) {
		Orders orders = getOneOrders(orderId);
		if (orders != null) {
			orders.setOrderStatus(2); // 已取消
			updateOrders(orders);
		}
	}
	
	//心理師查看已確認的訂單
	public List<Orders> getConfirmedOrdersByPsychId(Integer psychId) {
		return repository.findConfirmedByPsychId(psychId);
	}

	
	// 心理師查看自己收到的評論（只列出已評論的訂單）
		public List<Orders> getReviewsByPsychId(Integer psychId) {
			return getByPsychId(psychId).stream()
					.filter(o -> o.getRating() != null)
					.collect(Collectors.toList());
		}
		
	
	//會員：填寫心得評論
	public void submitReview(Integer orderId, Integer rating, String reviewContent) {
		Orders orders = getOneOrders(orderId);
		if (orders != null) {
			orders.setRating(rating);
			orders.setReviewContent(reviewContent);
			orders.setReviewedAt(LocalDateTime.now());
			repository.save(orders);
		}
	}
	
	//心理師填寫晤談筆記
	public void completeOrder(Integer orderId, String psychNote) {
		Orders orders = getOneOrders(orderId);
		if (orders != null) {
			orders.setOrderStatus(4);//已完成
			orders.setPsychNote(psychNote);
			updateOrders(orders);
		}
	}
	
	public List<Orders> getCompletedUnratedByMemberId(Integer memberId){
		return repository.findCompletedUnratedByMemberId(memberId);
	}
	
	public List<Orders> getConfirmedOrCompletedByMemberId(Integer memberId){
		return repository.findConfirmedOrCompletedByMemberId(memberId);

	}
}	