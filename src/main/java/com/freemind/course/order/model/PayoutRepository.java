package com.freemind.course.order.model;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PayoutRepository 
		extends JpaRepository<Payout, Integer> {
   }
