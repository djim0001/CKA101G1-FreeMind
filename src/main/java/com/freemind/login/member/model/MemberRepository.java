package com.freemind.login.member.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Integer> {
	Member findByMemberAccount(String memberAccount);

	Member findByEmail(String email);
	
	List<Member> findByMemberAccountOrNameContaining(String account , String name);
}
