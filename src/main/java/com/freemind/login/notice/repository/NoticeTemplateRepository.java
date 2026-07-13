package com.freemind.login.notice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.freemind.login.notice.entity.NoticeTemplate;

@Repository
public interface NoticeTemplateRepository extends JpaRepository<NoticeTemplate, Integer> {

}
