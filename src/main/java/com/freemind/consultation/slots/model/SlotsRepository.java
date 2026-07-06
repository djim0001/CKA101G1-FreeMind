package com.freemind.consultation.slots.model;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SlotsRepository extends JpaRepository<Slots, Integer>{

	@Query("from Slots where psychId = ?1")
	List<Slots> findByPsychId(Integer psychId);
	
	@Query("from Slots where slotDate = ?1")
	List<Slots> findBySlotDate(LocalDate slotDate);
	
	@Query("from Slots where consStatus = ?1")
	List<Slots> findByConsStatus(String consStatus);

}
