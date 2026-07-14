package com.freemind.login.member.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.freemind.login.member.util.HibernateUtil_CompositeQuery_Member;

@Service
public class MemberService {

	@Autowired
	private MemberRepository repository;

	@Autowired
	private SessionFactory sessionFactory;

	public Member addMember(Member member) {
		return repository.save(member);
	}

	public Member updateMember(Member member) {
		return repository.save(member);
	}

	public Member getOneMember(Integer memberId) {
		Optional<Member> optional = repository.findById(memberId);
		return repository.findById(memberId).orElse(null);
	}
	
	public List<Member> getAll() {
		return repository.findAll();
	}

	public List<Member> getAll(Map<String, String[]> map) {
		return HibernateUtil_CompositeQuery_Member.getAllC(map, sessionFactory.openSession());
	}

	public void deleteMember(Integer memberId) {
		repository.deleteById(memberId);
	}
	
	public Member findByAccount(String account) {
		return repository.findByMemberAccount(account);
	}

}
