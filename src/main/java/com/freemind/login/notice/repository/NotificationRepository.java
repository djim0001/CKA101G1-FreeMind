package com.freemind.login.notice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freemind.login.notice.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

	// 依公告狀態查詢，並依建立時間新到舊排序（首頁顯示已發布公告用）
	List<Notification> findByNoticeStatusOrderByCreatedAtDesc(Byte noticeStatus);

}
