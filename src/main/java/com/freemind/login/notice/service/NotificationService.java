package com.freemind.login.notice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.freemind.login.notice.entity.Notification;
import com.freemind.login.notice.repository.NotificationRepository;

@Service("notificationService")
public class NotificationService {

	@Autowired
	private NotificationRepository repository;

	public Notification addNotification(Notification notification) {
		return repository.save(notification);
	}

	public Notification updateNotification(Notification notification) {
		return repository.save(notification);
	}

	public void deleteNotification(Integer noticeId) {
		if (repository.existsById(noticeId)) {
			repository.deleteById(noticeId);
		}
	}

	public Notification getOneNotification(Integer noticeId) {
		return repository.findById(noticeId).orElse(null);
	}

	public List<Notification> getAll() {
		return repository.findAll();
	}

	// 首頁用：取得已發布(顯示)的公告，新到舊排序
	public List<Notification> getPublished() {
		return repository.findByNoticeStatusOrderByCreatedAtDesc((byte) 1);
	}

	// 發布：將公告狀態設為顯示(1)
	public void publish(Integer noticeId) {
		Notification notification = repository.findById(noticeId).orElse(null);
		if (notification != null) {
			notification.setNoticeStatus((byte) 1);
			repository.save(notification);
		}
	}

	// 撤下：將公告狀態設為隱藏(0)
	public void unpublish(Integer noticeId) {
		Notification notification = repository.findById(noticeId).orElse(null);
		if (notification != null) {
			notification.setNoticeStatus((byte) 0);
			repository.save(notification);
		}
	}

}
