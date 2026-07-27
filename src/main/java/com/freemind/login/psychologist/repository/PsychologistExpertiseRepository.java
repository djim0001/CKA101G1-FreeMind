package com.freemind.login.psychologist.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.freemind.login.psychologist.entity.PsychologistExpertise;

public interface PsychologistExpertiseRepository extends JpaRepository<PsychologistExpertise, PsychologistExpertise.CompositeExpertiseDetail>{

    // 這位心理師的所有專長(顯示個人頁面)
	@EntityGraph(attributePaths = "expertise")
    List<PsychologistExpertise> findByPsychologistPsychId(Integer psychId);

    // 有某專長的所有心理師(使用者依專長搜尋心理師)
	@EntityGraph(attributePaths = "psychologist")
    List<PsychologistExpertise> findByExpertiseExpertiseId(Integer expertiseId);
	
//	void deleteByCompositeExpertiseDetail_PsychId(Integer psychId);
	
	@Modifying
	@Query("DELETE FROM PsychologistExpertise pe WHERE pe.compositeExpertiseDetail.psychId = :psychId")
	void deleteByPsychId(@Param("psychId") Integer psychId);
}
