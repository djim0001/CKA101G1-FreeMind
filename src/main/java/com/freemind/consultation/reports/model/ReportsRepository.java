package com.freemind.consultation.reports.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportsRepository extends JpaRepository<Reports, Integer>{
		
	@Query(value = "from Reports where reportStatus = ?1")//查詢訂單狀態012
	List<Reports> findByReportStatus(Integer reportStatus);
	
	@Query(value = "from Reports where member.memberId = ?1")
	List<Reports> findByMemberId(Integer memberId);

}
