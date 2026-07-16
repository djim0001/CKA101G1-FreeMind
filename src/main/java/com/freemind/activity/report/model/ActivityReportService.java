package com.freemind.activity.report.model;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freemind.activity.activity.model.Activity;
import com.freemind.activity.registration.model.RegistrationRepository;
import com.freemind.login.admin.model.Admin;
import com.freemind.login.member.model.Member;

@Service
public class ActivityReportService {

	@Autowired
	private ActivityReportRepository reportRepo;
	
	@Autowired
	private RegistrationRepository regisRepo;


	// 會員送出回報
	@Transactional
	public ActivityReport submitReport(Member member, Activity activity, String reportContent) {
		// 1. 防呆:問題內容不能是空的
		if (reportContent == null || reportContent.trim().isEmpty()) {
			throw new IllegalArgumentException("請填寫問題回報內容");
		}
		
		// 驗證:活動必須已結束才能回報
		if (LocalDateTime.now().isBefore(activity.getActivityEnd())) {
			throw new IllegalStateException("活動尚未結束，無法回報問題");
		}

		// 驗證:必須有成功報名紀錄(regisStatus = 1)
		boolean hasAttended = regisRepo.existsByMemberAndActivityAndRegisStatusIn(member, activity, List.of(1));
		if (!hasAttended) {
			throw new IllegalStateException("尚未成功報名此活動，無法回報問題");
		}
		
		// 驗證:不可重複回報
		boolean alreadyReported = reportRepo.existsByMemberAndActivity(member, activity);
		if (alreadyReported) {
			throw new IllegalStateException("您已回報過此活動的問題，請勿重複回報");
		}
		
		// 2. 建立一個新的回報物件
		ActivityReport report = new ActivityReport();
		// 3. 設定初始值
		report.setMember(member);
		report.setActivity(activity);
		report.setReportContent(reportContent.trim());
		report.setReportStatus(0);
		report.setCreatedAt(LocalDateTime.now());

		return reportRepo.save(report);
	}
	
	// 會員查自己的回報紀錄
	public List<ActivityReport> getMyReports(Member member) {
		return reportRepo.findByMemberWithActivity(member);
	}

	// 管理員接手處理問題(狀態0 → 1) 
	@Transactional
	public ActivityReport takeOverReport(Integer reportId, Admin admin) {
		ActivityReport report = reportRepo.findByIdWithDetails(reportId).orElseThrow(() -> new IllegalArgumentException("問題回報紀錄不存在"));
		
		if (report.getReportStatus() != 0) {
			throw new IllegalStateException("此筆問題回報非為待處理狀態，無法處理");
		}
		report.setAdmin(admin);
		report.setReportStatus(1);
		return report;
	}

	
	// 管理員回覆處理(狀態1 → 2) 
	@Transactional
	public ActivityReport replyReport(Integer reportId, Admin admin, String replyContent) {
		// 撈、驗狀態、驗回覆權限、填內容、狀態→2、repliedAt
		ActivityReport report = reportRepo.findByIdWithDetails(reportId).orElseThrow(() -> new IllegalArgumentException("問題回報紀錄不存在"));
				
		if (report.getReportStatus() != 1) {
			throw new IllegalStateException("此筆問題回報非為處理中狀態，無法回覆");
		}
		
		if (!report.getAdmin().getAdminId().equals(admin.getAdminId())) {
			throw new IllegalStateException("此筆問題回報由其他管理員處理中");
		}
		
		if (replyContent == null || replyContent.trim().isEmpty()) {
			throw new IllegalArgumentException("請填寫回覆內容");
		}
		report.setReplyContent(replyContent.trim());
		report.setReportStatus(2);
		report.setRepliedAt(LocalDateTime.now());
		return report;
	}
	// 後台查全部問題回報
	public List<ActivityReport> getAllReports() {
		return reportRepo.findAllWithDetails();
	}

	// 後台依狀態篩選問題回報(0待處理/1處理中/2已處理)
	public List<ActivityReport> getReportsByStatus(Integer status) {
		return reportRepo.findByStatusWithDetails(status);
	}
	
	// 已回報過的活動(不再出現回報按鈕):此會員已回報過的活動 id 集合
	public Set<Integer> getReportedActivityIds(Member member) {
		List<ActivityReport> reports = reportRepo.findByMemberWithActivity(member);
		Set<Integer> ids = new HashSet<>();
		for (ActivityReport r : reports) {
			ids.add(r.getActivity().getActivityId());
		}
		return ids;
	}
}
