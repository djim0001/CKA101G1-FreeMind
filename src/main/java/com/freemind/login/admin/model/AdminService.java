package com.freemind.login.admin.model;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.freemind.login.admin.util.HibernateUtil_CompositeQuery_Admin;


@Service
public class AdminService {

	@Autowired
	private AdminRepository repository;
	
	@Autowired
	private SessionFactory sessionFactory;


	public Admin addAdmin(Admin adminVO) {
		
		return repository.save(adminVO);
	}

	public Admin updateAdmin(Admin adminVO) {
		
		return repository.save(adminVO);
	}

	public Admin getOneAdmin(Integer adminId) {
		Optional<Admin> optional = repository.findById(adminId);
		return repository.findById(adminId).orElse(null);
	}
	
	public List<Admin> getAll() {
		return repository.findAll();
	}
	
	public List<Admin> getAll(Map<String,String[]> map){
		return HibernateUtil_CompositeQuery_Admin.getAllC(map, sessionFactory.openSession());
		
	}
	
	public void deleteAdmin(Integer adminId) {
		repository.deleteById(adminId);
	}
	public Admin findByAccount(String account) {
		return repository.findByAdminAccount(account);
	}

}
