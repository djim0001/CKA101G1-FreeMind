package com.freemind.login.psychologist.service;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freemind.login.psychologist.entity.Expertise;
import com.freemind.login.psychologist.repository.ExpertiseRepository;
import com.freemind.login.psychologist.repository.PsychologistExpertiseRepository;

@Service
public class ExpertiseServise {

	@Autowired
	private ExpertiseRepository repository;
	
	@Autowired
	private PsychologistExpertiseRepository peRepository;
	
	public void addExpertise(Expertise expertise) {
		repository.save(expertise);
	}
	
	public void upadteExpertise(Expertise expertise) {
		repository.save(expertise);
	}

	
	public List<Expertise> getAll(){
		return repository.findAll();
	}
	
	
}
