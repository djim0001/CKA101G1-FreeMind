package com.freemind.course;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.freemind.course.coupon.model.CouponRepository;

// @SpringBootApplication
public class Test_Application implements CommandLineRunner{
	
	@Autowired
	CouponRepository repository ;
	
	@Autowired
    private SessionFactory sessionFactory;
	
	
	public static void main(String[] args) {
		SpringApplication.run(Test_Application.class, args);
	}


	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	
}
