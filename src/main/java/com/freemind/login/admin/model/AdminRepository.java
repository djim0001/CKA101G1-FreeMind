package com.freemind.login.admin.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
	Admin findByAdminAccount(String adminAccount);
}
