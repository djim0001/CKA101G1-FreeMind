package com.freemind.consultation.reports.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReportsService {

	@Autowired
	ReportsRepository repository;
	
	public void addReports(Reports reports) {
		reports.setReportDate(LocalDateTime.now());
		repository.save(reports);
	}
	
	public void updateReports(Reports reports) {
		repository.save(reports);
	}
	
	public void deleteReports(Integer reportId) {
		if(repository.existsById(reportId)) //確認這筆資料存在, 有存在才刪除
			repository.deleteById(reportId);
	}
	
	public Reports getOneReports(Integer reportId) {
		Optional<Reports> optional = repository.findById(reportId);
		return optional.orElse(null); //findById回傳的是Optional<Reports>, 否的話回傳orElse(null)
	}
	
	public List<Reports> getAll(){
		return repository.findAll();
	}
	
	//我的自訂查詢功能
	public List<Reports> getByReportsStatus(Integer reportStatus){
		return repository.findByReportStatus(reportStatus);
	}
	
	public List<Reports> getByMemberId(Integer memberId) {
	    return repository.findByMemberId(memberId);
	}
}
