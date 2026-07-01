package com.freemind.login.psychologist.model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface  PsychologistRepository extends JpaRepository<Psychologist, Integer> {
	
	@Transactional
	@Modifying
	@Query(value = "delete from psychologist where psych_id = ?1" ,nativeQuery = true)
	void deleteByPsychologist(int psych_id);
}
