package com.freemind.login.member.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Integer> {
	Member findByMemberAccount(String memberAccount);

	Member findByEmail(String email);
}
