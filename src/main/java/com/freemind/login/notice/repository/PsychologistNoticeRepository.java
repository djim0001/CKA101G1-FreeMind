package com.freemind.login.notice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.freemind.login.notice.entity.PsychologistNotice;

@Repository
public interface PsychologistNoticeRepository extends JpaRepository<PsychologistNotice, Integer>{

	List<PsychologistNotice> findByPsychIdOrderByCreatedAtDesc(Integer psychologistId);

	List<PsychologistNotice> findByPsychIdAndNoticeTypeOrderByCreatedAtDesc(Integer psychId , Byte noticeType);
	
    long countByPsychIdAndIsReadFalse(Integer memberId);

    @Modifying
    @Query("UPDATE PsychologistNotice n SET n.isRead = true WHERE n.psychId = :psychId AND n.isRead = false")
    int markAllRead(@Param("psychId") Integer psychId);
}
