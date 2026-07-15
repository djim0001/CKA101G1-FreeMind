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
	
	// 把某天某個小時的狀態改成指定值，要把 3/4 當成 0/1 的等價值來比對，回傳是否成功（該小時原本狀態符合預期才允許）
	public boolean updateHourStatus(Integer timeslotId, int hour, char fromStatus, char toStatus) {
	    Slots slots = getOneSlots(timeslotId);
	    if (slots == null) return false;

	    StringBuilder sb = new StringBuilder(slots.getConsStatus());
	    if (normalize(sb.charAt(hour)) != normalize(fromStatus)) {
	        return false;
	    }
	    sb.setCharAt(hour, toStatus);
	    slots.setConsStatus(sb.toString());
	    updateSlots(slots);
	    return true;
	}

	private char normalize(char c) {
	    if (c == '3') return '0';
	    if (c == '4') return '1';
	    return c;
	}
	
	private static final java.time.LocalDate TEMPLATE_DATE = java.time.LocalDate.of(2000, 1, 1);

	// 心理師設定/改變範本後，套用範本到未來14天（保留已預約的小時，其餘依範本覆蓋；沒有記錄的天數直接新增）
	public void reapplyTemplateToNext14Days(Integer psychId) {
		Slots template = getOneByPsychAndDate(psychId, TEMPLATE_DATE);
		if (template == null) {
			return; // 該心理師還沒設定範本，跳過
		}
		String fixedHours = template.getConsStatus();
		
		java.time.LocalDate today = java.time.LocalDate.now();
		for (int i = 1; i <= 14; i++) {
			java.time.LocalDate targetDate = today.plusDays(i);
			
			Slots existing = getOneByPsychAndDate(psychId, targetDate);
			
			if (existing == null) {
				// 該天還沒有記錄，直接依範本新增
				Slots newSlot = new Slots();
				com.freemind.login.psychologist.entity.Psychologist p = new com.freemind.login.psychologist.entity.Psychologist();
				p.setPsychId(psychId);
				newSlot.setPsychologist(p);
				newSlot.setSlotDate(targetDate);
				newSlot.setConsStatus(fixedHours);
				addSlots(newSlot);
				continue;
			}
			
			// 該天已有記錄：保留已預約的小時(2)，其餘依新範本覆蓋
			StringBuilder sb = new StringBuilder(existing.getConsStatus());
			for (int h = 0; h < 24; h++) {
				if (sb.charAt(h) == '2') continue; // 已預約成立，不可覆蓋
				sb.setCharAt(h, fixedHours.charAt(h));
			}
			existing.setConsStatus(sb.toString());
			updateSlots(existing);
		}
	}
	
	// 對所有有設定範本的心理師，批次執行「套用範本」（供排程器呼叫）
	public void reapplyTemplateToAll() {
		List<Slots> allSlots = getAll();
		java.util.Set<Integer> psychIdsWithTemplate = new java.util.HashSet<>();
		for (Slots s : allSlots) {
			if (s.getSlotDate().equals(TEMPLATE_DATE)) {
				psychIdsWithTemplate.add(s.getPsychologist().getPsychId());
			}
		}
		for (Integer psychId : psychIdsWithTemplate) {
			reapplyTemplateToNext14Days(psychId);
		}
	}

}