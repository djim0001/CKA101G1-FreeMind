package com.freemind.consultation.slots.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SlotsService {

	@Autowired
	SlotsRepository repository;
	
	public void addSlots(Slots slots) {
		repository.save(slots);
	}
	
	public void updateSlots(Slots slots) {
		repository.save(slots);
	}
	
	public void deleteSlots(Integer timeslotId) {
		if(repository.existsById(timeslotId))
			repository.deleteById(timeslotId);
	}
	
	public Slots getOneSlots(Integer timeslotId) {
		Optional<Slots> optional = repository.findById(timeslotId);
		return optional.orElse(null);
	}
	
	public List<Slots> getAll(){
		return repository.findAll();
	}
	
	public List<Slots> getByPsychId(Integer psychId){
		return repository.findByPsychId(psychId);
	}
	
	public List<Slots> getBySlotDate(LocalDate slotDate){
		return repository.findBySlotDate(slotDate);
	}
	
	public List<Slots> getByConsStatus(String consStatus){
		return repository.findByConsStatus(consStatus);
	}
	
	// 新增：依心理師+日期查詢單一天的時段記錄
	public Slots getOneByPsychAndDate(Integer psychId, LocalDate slotDate) {
		return repository.findByPsychIdAndSlotDate(psychId, slotDate).orElse(null);
	}
	
	// 把某天某個小時的狀態改成指定值(0/1/2)，回傳是否成功（該小時原本狀態符合預期才允許）
	public boolean updateHourStatus(Integer timeslotId, int hour, char fromStatus, char toStatus) {
		Slots slots = getOneSlots(timeslotId);
		if (slots == null) return false;
		
		StringBuilder sb = new StringBuilder(slots.getConsStatus());
		if (sb.charAt(hour) != fromStatus) {
			return false; // 狀態不符（可能已經被別人搶走了）
		}
		sb.setCharAt(hour, toStatus);
		slots.setConsStatus(sb.toString());
		updateSlots(slots);
		return true;
	}
	
	private static final java.time.LocalDate TEMPLATE_DATE = java.time.LocalDate.of(2000, 1, 1);

	// 依範本，幫指定心理師產生未來14天的時段（如果那天已有記錄就跳過，不覆蓋）
	public void generateNext14DaysForPsych(Integer psychId) {
		Slots template = getOneByPsychAndDate(psychId, TEMPLATE_DATE);
		if (template == null) {
			return; // 該心理師還沒設定範本，跳過
		}
		String fixedHours = template.getConsStatus();
		
		java.time.LocalDate today = java.time.LocalDate.now();
		for (int i = 1; i <= 14; i++) {
			java.time.LocalDate targetDate = today.plusDays(i);
			
			Slots existing = getOneByPsychAndDate(psychId, targetDate);
			if (existing != null) {
				continue; // 該天已經有記錄（可能是心理師手動調整過），不覆蓋
			}
			
			Slots newSlot = new Slots();
			com.freemind.login.psychologist.entity.Psychologist p = new com.freemind.login.psychologist.entity.Psychologist();
			p.setPsychId(psychId);
			newSlot.setPsychologist(p);
			newSlot.setSlotDate(targetDate);
			newSlot.setConsStatus(fixedHours);
			addSlots(newSlot);
		}
	}
	
	// 對所有有設定範本的心理師，批次執行
	public void generateNext14DaysForAll() {
		List<Slots> allSlots = getAll();
		java.util.Set<Integer> psychIdsWithTemplate = new java.util.HashSet<>();
		for (Slots s : allSlots) {
			if (s.getSlotDate().equals(TEMPLATE_DATE)) {
				psychIdsWithTemplate.add(s.getPsychologist().getPsychId());
			}
		}
		for (Integer psychId : psychIdsWithTemplate) {
			generateNext14DaysForPsych(psychId);
		}
	}
}
