package com.freemind.login.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freemind.login.notice.entity.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

}
