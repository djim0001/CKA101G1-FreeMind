package com.freemind.login.psychologist.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freemind.login.psychologist.entity.PsychologistExpertise;

public interface PsychologistExpertiseRepository extends JpaRepository<PsychologistExpertise, PsychologistExpertise.CompositeExpertiseDetail>{

}
