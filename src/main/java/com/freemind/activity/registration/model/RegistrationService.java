package com.freemind.activity.registration.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freemind.activity.activity.model.Activity;
import com.freemind.login.member.model.Member;

@Service
public class RegistrationService {
	
	@Autowired
	private RegistrationRepository regisRepo;
	
	@Transactional
	public Registration register(Member member, Activity activity) {
	    // 1. 查:這個會員對這個活動有沒有有效報名?
		boolean hasActive = regisRepo.existsByMemberAndActivityAndRegisStatusIn(member, activity, List.of(0, 1));
		
	    // 2. 擋:有 → 丟例外
		if (hasActive) {
			 throw new IllegalStateException("已報名過此活動，請勿重複報名");
		}
	    // 3. 組:new 一個 Registration,填 member、activity、regisStatus=0、regisAt=現在
		Registration regis = new Registration();
		regis.setMember(member);   
		regis.setActivity(activity);   
		regis.setRegisStatus(0);   // 報名狀態預設為「待審核」
		regis.setRegisAt(LocalDateTime.now());   
	    // 4. 存:save 進資料庫
		return regisRepo.save(regis);
	    // TODO: 通知團主有新報名待審核(等組員的通知模組)
	}
	
	@Transactional
	public Registration approve(Integer regisId) {
	    // 1. 撈:findById + orElseThrow(自訂訊息版,lambda語法)
		Registration regis = regisRepo.findById(regisId).orElseThrow(() -> new IllegalArgumentException("報名紀錄不存在"));
	    // 2. 驗:狀態不是 0 → throw new IllegalStateException("...")
		if (regis.getRegisStatus() != 0) {
			 throw new IllegalStateException("此筆申請非為待審核狀態，無法審核");
		}
	    // 3. 判:拿出 activity,比較 regisCount 和 capacity
		Activity activity = regis.getActivity();   // 先把活動拿出來

		if (activity.getRegisCount() < activity.getCapacity() ) {
			regis.setRegisStatus(1);
			activity.setRegisCount(activity.getRegisCount() + 1);  
		    // TODO: 通知會員報名成功
		} else {
			regis.setRegisStatus(2);
		    // TODO: 通知會員報名失敗(名額不足)
		}
		return regis;
	}
	
	
	@Transactional
	public Registration cancel(Integer regisId, Integer cancelReason, String cancelNote) {
		// 1. 撈:findById + orElseThrow
		Registration regis = regisRepo.findById(regisId).orElseThrow(() -> new IllegalArgumentException("報名紀錄不存在"));
	    // 2. 驗:狀態不是 0也不是1 
		if (regis.getRegisStatus() != 0 && regis.getRegisStatus() != 1) {
			 throw new IllegalStateException("此筆報名申請無法取消");
		}
		// 3. 如果狀態是1, regisCount要減1
		Activity activity = regis.getActivity(); 
		if (regis.getRegisStatus() == 1) {
			activity.setRegisCount(activity.getRegisCount() - 1);
		}
		
		regis.setRegisStatus(3); // 狀態改為3已取消報名
		regis.setCancelledAt(LocalDateTime.now()); 
		regis.setCancelReason(cancelReason);
		regis.setCancelNote(cancelNote);
		return regis;
	}
	
	@Transactional
	public Registration review(Integer regisId, Integer rating, String reviewContent) {
		// 1. 撈:findById + orElseThrow
		Registration regis = regisRepo.findById(regisId).orElseThrow(() -> new IllegalArgumentException("報名紀錄不存在"));
		
		
		// 2. 驗:報名狀態為1，且活動已結束
		Activity activity = regis.getActivity();
		if (regis.getRegisStatus() != 1 || LocalDateTime.now().isBefore(activity.getActivityEnd())) {
			 throw new IllegalStateException("此筆報名目前無法填寫活動評論");
		}
		
		// 3. 擋重複評論
		if (regis.getReviewedAt() != null) {
			throw new IllegalStateException("此筆報名已填寫過活動評論");
		}
		
		// 4. 填寫
		regis.setReviewedAt(LocalDateTime.now()); 
		regis.setRating(rating);
		regis.setReviewContent(reviewContent);
		
		// 5.評分必填
		if (rating == null) {
		    throw new IllegalArgumentException("請填寫評分");
		}
		
		return regis;
	}
}
