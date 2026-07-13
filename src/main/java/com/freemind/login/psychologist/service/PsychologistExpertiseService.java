package com.freemind.login.psychologist.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.freemind.login.psychologist.entity.Psychologist;
import com.freemind.login.psychologist.entity.PsychologistExpertise;
import com.freemind.login.psychologist.repository.PsychologistExpertiseRepository;
@Service
public class PsychologistExpertiseService {
	
	private final PsychologistExpertiseRepository repository;
	
	public PsychologistExpertiseService(PsychologistExpertiseRepository repository) {
		this.repository = repository;
	}
	
	public void addPsychologistExpertise(PsychologistExpertise psychologistExpertise) {
		repository.save(psychologistExpertise);
	}
	
	public void updatePsychologistExpertise(PsychologistExpertise psychologistExpertise) {
		repository.save(psychologistExpertise);
	}
	
    // 這位心理師的所有專長(顯示個人頁面)
    public List<PsychologistExpertise> getExpertisesByPsychId(Integer psychId) {
        return repository.findByPsychologistPsychId(psychId);
    }

    // 有某專長的所有心理師(使用者依專長搜尋心理師)
    public List<PsychologistExpertise> getPsychologistsByExpertiseId(Integer expertiseId) {
        return repository.findByExpertiseExpertiseId(expertiseId);
    }
	
	
}
