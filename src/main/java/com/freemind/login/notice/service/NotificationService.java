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

}
