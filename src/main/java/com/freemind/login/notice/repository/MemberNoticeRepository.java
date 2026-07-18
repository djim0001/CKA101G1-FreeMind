package com.freemind.login.notice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.freemind.login.notice.entity.MemberNotice;
@Repository
public interface MemberNoticeRepository extends JpaRepository<MemberNotice, Integer>{
	
	List<MemberNotice> findByMemberIdOrderByCreatedAtDesc(Integer memberId);

	List<MemberNotice> findByMemberIdAndNoticeTypeOrderByCreatedAtDesc(Integer memberId , Byte noticeType);
	
    long countByMemberIdAndIsReadFalse(Integer memberId);

    @Modifying
    @Query("UPDATE MemberNotice n SET n.isRead = true WHERE n.memberId = :memberId AND n.isRead = false")
    int markAllRead(@Param("memberId") Integer memberId);
    
    
}
