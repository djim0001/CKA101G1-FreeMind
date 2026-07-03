package com.freemind.consultation.slots.model;

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
}
