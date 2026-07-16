package com.freemind.activity.registration.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.freemind.activity.activity.model.Activity;
import com.freemind.login.member.model.Member;

public interface RegistrationRepository extends JpaRepository<Registration, Integer>{
	// 1.擋下重複報名(確認此會員對某活動是否已存在「有效」報名)
	boolean existsByMemberAndActivityAndRegisStatusIn(Member member, Activity activity, List<Integer> statusList);

	// 2.我的報名活動清單(查詢此會員報名過的活動)
	// JPQL查詢Java物件：查出所有 Registration 物件，順便把每一筆的 activity 屬性填滿（JOIN FETCH），
	// 條件是 member 等於呼叫時傳進來的那個會員，結果按報名時間新到舊排序
	@Query("SELECT r FROM Registration r JOIN FETCH r.activity "
		     + "WHERE r.member = :member ORDER BY r.regisAt DESC")
											//  JPQL 裡的 :member           Java方法的參數
		List<Registration> findByMemberWithActivity(@Param("member") Member member);
	
	
	// 3.發起人審核(查詢此活動報名清單)
	@Query("SELECT r FROM Registration r JOIN FETCH r.member "
		     + "WHERE r.activity = :activity ORDER BY r.regisAt ASC")
		List<Registration> findByActivityWithMember(@Param("activity") Activity activity);
	
	// 4.查詢某活動的所有評論(已留評論的報名紀錄)
	@Query("SELECT r FROM Registration r JOIN FETCH r.member "
	     + "WHERE r.activity = :activity AND r.reviewedAt IS NOT NULL "
	     + "ORDER BY r.reviewedAt DESC")
	List<Registration> findReviewsByActivity(@Param("activity") Activity activity);
	
	
	
}
