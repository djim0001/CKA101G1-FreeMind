package com.freemind.login.psychologist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.freemind.login.psychologist.entity.PsychologistNotice;

import jakarta.transaction.Transactional;

public interface  PsychologistNoticeRepository extends JpaRepository<PsychologistNotice, Integer> {
	
	@Transactional
	@Modifying
	@Query(value = "delete from psychologist where psych_id = ?1" ,nativeQuery = true)
	void deleteByPsychologistNotice(int psych_notice_id);
}