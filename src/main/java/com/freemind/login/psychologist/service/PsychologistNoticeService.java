package com.freemind.login.psychologist.service;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.freemind.login.psychologist.entity.PsychologistNotice;
import com.freemind.login.psychologist.repository.PsychologistNoticeRepository;


public class PsychologistNoticeService {

	@Autowired
	private final PsychologistNoticeRepository repository;
	
	public PsychologistNoticeService(PsychologistNoticeRepository repository) {
		this.repository=repository;
	}
	
	
	public void addPsychologistNotice(PsychologistNotice psychologistNotice) {
		repository.save(psychologistNotice);
	}
	public void updatePsychologistNotice(PsychologistNotice psychologistNotice) {
		repository.save(psychologistNotice);
	}
	
	public void daletePsychologistNotice(Integer psychologistNotice) {
		if(repository.existsById(psychologistNotice))
			repository.deleteByPsychologistNotice(psychologistNotice);	
	}
	
	public PsychologistNotice getOnePsychologistNotice(Integer psychologistNotice) {
		Optional<PsychologistNotice> optional = repository.findById(psychologistNotice);
		return optional.orElse(null);
	}
	public List<PsychologistNotice> getAll(){
		return repository.findAll();
	}
	
	
	
	
	
}
