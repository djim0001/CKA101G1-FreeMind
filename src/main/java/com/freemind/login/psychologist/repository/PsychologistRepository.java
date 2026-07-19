package com.freemind.login.psychologist.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.freemind.login.member.model.Member;
import com.freemind.login.psychologist.dto.PsychologistAdminRes;
import com.freemind.login.psychologist.entity.Psychologist;

public interface  PsychologistRepository extends JpaRepository<Psychologist, Integer> ,JpaSpecificationExecutor<Psychologist>{
	
	@Transactional
	@Modifying
	@Query(value = "delete from psychologist where psych_id = ?1" ,nativeQuery = true)
	void deleteByPsychologist(int psych_id);
	
	Psychologist findByPsychAccount(String PsychAccount);
	
	Psychologist findByEmail(String email);
	
	List<Psychologist> findByPsychAccountContainingOrNameContaining(String account , String name);
}
