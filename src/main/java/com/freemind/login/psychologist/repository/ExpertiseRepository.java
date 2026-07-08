package com.freemind.login.psychologist.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.freemind.login.psychologist.entity.Expertise;

import jakarta.transaction.Transactional;

public interface ExpertiseRepository extends JpaRepository<Expertise, Integer>{

	@Transactional
	@Modifying
	@Query(value = "delete from expertise where expertise_id = ?1" , nativeQuery = true)
	void deleteByexpertise(int expertise_id);
}
