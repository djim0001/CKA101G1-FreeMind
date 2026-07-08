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
}
