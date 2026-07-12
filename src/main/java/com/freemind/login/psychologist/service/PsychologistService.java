package com.freemind.login.psychologist.service;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.freemind.login.psychologist.entity.Psychologist;
import com.freemind.login.psychologist.repository.PsychologistRepository;

@Service
public class PsychologistService {
	
	private final PsychologistRepository repository;
	
	public PsychologistService(PsychologistRepository repository) {
		this.repository = repository;
	}

	public void addPsychologist(Psychologist psychologist) {
		repository.save(psychologist);
	}
	
	public void updatePsychologist(Psychologist psychologist) {
		repository.save(psychologist);
	}

	
	public Psychologist getOnePsychologist(Integer psychologist) {
		Optional<Psychologist> optional = repository.findById(psychologist);
		return optional.orElse(null);
	}
	
	public List<Psychologist> getAll(){
		return repository.findAll();
	}
	
	
}
