package com.freemind.login.security.psychologistsecurity;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.freemind.login.psychologist.entity.Psychologist;
import com.freemind.login.psychologist.repository.PsychologistRepository;

@Service
public class PsychUserDetailsService implements UserDetailsService {

	private final PsychologistRepository psychologistRepository;

	public PsychUserDetailsService(PsychologistRepository psychologistRepository) {
		this.psychologistRepository = psychologistRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Psychologist psychologist = psychologistRepository.findByPsychAccount(username);
		if (psychologist == null) {
			throw new UsernameNotFoundException("心理師帳號不存在: " + username);
		}
		return new PsychUserDetails(psychologist);
	}
	
	
}
