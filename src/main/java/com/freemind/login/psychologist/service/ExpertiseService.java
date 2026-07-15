package com.freemind.login.psychologist.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.freemind.login.psychologist.dto.ExpertiseRes;
import com.freemind.login.psychologist.entity.Expertise;
import com.freemind.login.psychologist.repository.ExpertiseRepository;
import com.freemind.login.psychologist.repository.PsychologistExpertiseRepository;

@Service
public class ExpertiseService {

	private final ExpertiseRepository repository;
	private final PsychologistExpertiseRepository peRepository;
	
	
	public ExpertiseService(ExpertiseRepository repository, PsychologistExpertiseRepository peRepository) {
		this.repository = repository;
		this.peRepository = peRepository;
		
	}
	
	
	
	
	public void addExpertise(Expertise expertise) {
		repository.save(expertise);
	}
	
	public void upadteExpertise(Expertise expertise) {
		repository.save(expertise);
	}

	
	public List<String> getAllExpertiseNames() {
	    return repository.findAll().stream()
	            .map(Expertise::getExpertiseName)
	            .toList();
	}
	
	
	public List<ExpertiseRes> getAllExpertise(){
		return repository.findAll().stream()
				.map(e -> new ExpertiseRes(e.getExpertiseId() , e.getExpertiseName()))
				.toList();
	}
	
}
