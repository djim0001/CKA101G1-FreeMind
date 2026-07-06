package com.freemind.login.psychologist.service;

import org.hibernate.SessionFactory;

import com.freemind.login.psychologist.entity.Expertise;
import com.freemind.login.psychologist.repository.ExpertiseRepository;

public class ExpertiseServise {

	ExpertiseRepository repository;
	
	private SessionFactory sessionFactory;
	
	public void addExpertise(Expertise expertise) {
		repository.save(expertise);
	}
	
	public void upadteExpertise(Expertise expertise) {
		repository.save(expertise);
	}
	
	public void deleteExpertise(Integer expertise) {
		if(repository.existsById(expertise))
			repository.deleteByexpertise(expertise);
	}
	
	
}
