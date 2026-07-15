package com.freemind.consultation.slots.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SlotsRepository extends JpaRepository<Slots, Integer>{

	@Query("from Slots where psychologist.psychId = ?1")
	List<Slots> findByPsychId(Integer psychId);
	
	@Query("from Slots where slotDate = ?1")
	List<Slots> findBySlotDate(LocalDate slotDate);
	
	@Query("from Slots where consStatus = ?1")
	List<Slots> findByConsStatus(String consStatus);

	@Query("from Slots where psychologist.psychId = ?1 and slotDate = ?2")
	Optional<Slots> findByPsychIdAndSlotDate(Integer psychId, LocalDate slotDate);
	
	
	
	//心理師搜尋 日期時用 
	@Query("from Slots where psychologist.psychId = ?1 and slotDate between ?2 and ?3")
	List<Slots> findByPsychIdAndSlotDateBetween(Integer psychId, LocalDate start, LocalDate end);
}
