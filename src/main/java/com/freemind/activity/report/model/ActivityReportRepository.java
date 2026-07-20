package com.freemind.activity.report.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.freemind.activity.activity.model.Activity;
import com.freemind.login.member.model.Member;

public interface ActivityReportRepository extends JpaRepository<ActivityReport, Integer> {
	 
	// 會員:查自己的回報紀錄
	@Query("SELECT ar FROM ActivityReport ar "
			+ "JOIN FETCH ar.activity "
			+ "WHERE ar.member = :member "
			+ "ORDER BY ar.createdAt DESC")
	List<ActivityReport> findByMemberWithActivity(@Param("member") Member member);
	
	// 後台人員:全部回報列表(LEFT JOIN admin, 因為後台未處理時為 null)
	@Query("SELECT ar FROM ActivityReport ar "
			+ "JOIN FETCH ar.member "
			+ "JOIN FETCH ar.activity "
			+ "LEFT JOIN FETCH ar.admin "
			+ "ORDER BY ar.reportStatus ASC, ar.createdAt DESC")
	List<ActivityReport> findAllWithDetails();
 
	// 後台人員:依狀態篩選(0待處理/1處理中/2已處理)
	@Query("SELECT ar FROM ActivityReport ar "
			+ "JOIN FETCH ar.member "
			+ "JOIN FETCH ar.activity "
			+ "LEFT JOIN FETCH ar.admin "
			+ "WHERE ar.reportStatus = :status "
			+ "ORDER BY ar.createdAt DESC")
	List<ActivityReport> findByStatusWithDetails(@Param("status") Integer status);
 
	// 單筆(回覆前先撈出會員/活動/後台相關資料)
	@Query("SELECT ar FROM ActivityReport ar "
			+ "JOIN FETCH ar.member "
			+ "JOIN FETCH ar.activity "
			+ "LEFT JOIN FETCH ar.admin "
			+ "WHERE ar.reportId = :reportId")
	Optional<ActivityReport> findByIdWithDetails(@Param("reportId") Integer reportId);

	boolean existsByMemberAndActivity(Member member, Activity activity);

	long countByReportStatus(Integer reportStatus);

}
 
