package com.freemind.consultation.slots.scheduler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.freemind.consultation.slots.model.SlotsService;

@Component
public class SlotsScheduler {

	@Autowired
	private SlotsService slotsSvc;

	@Scheduled(fixedRate = 1209600000) //14天 × 24小時 × 60分 × 60秒 × 1000毫秒
	public void autoGenerateSlots() {
		slotsSvc.generateNext14DaysForAll();
	}
}
