package com.freemind.login.notice.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.freemind.login.notice.entity.NoticeTemplate;
import com.freemind.login.notice.repository.NoticeTemplateRepository;

@Service("noticeTemplateService")
public class NoticeTemplateService {

	@Autowired
	private NoticeTemplateRepository repository;

	public NoticeTemplate addNoticeTemplate(NoticeTemplate noticeTemplate) {
		return repository.save(noticeTemplate);
	}

	public NoticeTemplate updateNoticeTemplate(NoticeTemplate noticeTemplate) {
		return repository.save(noticeTemplate);
	}

	public void deleteNoticeTemplate(Integer templateId) {
		if (repository.existsById(templateId)) {
			repository.deleteById(templateId);
		}
	}

	public NoticeTemplate getOneNoticeTemplate(Integer templateId) {
		return repository.findById(templateId).orElse(null);
	}

	public List<NoticeTemplate> getAll() {
		return repository.findAll();
	}

}
