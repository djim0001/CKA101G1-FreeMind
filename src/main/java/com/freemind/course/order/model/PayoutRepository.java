package com.freemind.course.order.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PayoutRepository extends JpaRepository<Payout, Integer> {

    List<Payout> findByPsychologistPsychId(Integer psychId);

    List<Payout> findByBillingMonth(String billingMonth);
    
    int countByPayoutStatus(Integer payoutStatus);

    int countByPsychologist_PsychIdAndPayoutStatus(
            Integer psychId,
            Integer payoutStatus
    );
    
    boolean existsByPsychologistPsychIdAndBillingMonth(
	        Integer psychId,
	        String billingMonth
	    );
}
