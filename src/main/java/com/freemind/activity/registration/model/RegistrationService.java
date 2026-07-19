package com.freemind.activity.registration.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freemind.activity.activity.model.Activity;
import com.freemind.activity.websocket.ActivityWebSocketHandler;
import com.freemind.login.member.model.Member;
import com.freemind.login.notice.service.NoticeService;

@Service
public class RegistrationService {
	
	@Autowired
	private RegistrationRepository regisRepo;
	
	@Autowired
	private NoticeService noticeService;
	
	@Autowired
	private ActivityWebSocketHandler activityWebSocketHandler;
	
	@Transactional
	public Registration register(Member member, Activity activity,  String motivation) {
		if (activity.getMember().getMemberId().equals(member.getMemberId())) {
			throw new IllegalStateException("無法報名自己發起的活動");
		}
		
	    if (motivation == null || motivation.trim().isEmpty()) {
	        throw new IllegalArgumentException("報名動機: 請勿空白");
	    }
	    // 1. 查:這個會員對這個活動有沒有有效報名?
		boolean hasActive = regisRepo.existsByMemberAndActivityAndRegisStatusIn(member, activity, List.of(0, 1, 4));
		
		
	    // 2. 擋:有 → 丟例外
		if (hasActive) {
			 throw new IllegalStateException("已報名過此活動，請勿重複報名");
		}
				
	    // 3. 擋:總量已滿(未審核+正取+備取 >= 正取上限+備取上限)
	    long activeCount = regisRepo.countByActivityAndRegisStatusIn(activity, List.of(0, 1, 4));
	    int totalCapacity = activity.getCapacity() + activity.getWaitlistCapacity();
	    if (activeCount >= totalCapacity) {
	        throw new IllegalStateException("報名已額滿（含候補），無法再申請");
	    }

	    // 4. 組:new 一個 Registration,填 member、activity、regisStatus=0、regisAt=現在
		Registration regis = new Registration();
		regis.setMember(member);   
		regis.setActivity(activity);   
		regis.setRegisStatus(0);   // 報名狀態預設為「待審核」
		regis.setRegisAt(LocalDateTime.now()); 
		regis.setMotivation(motivation);
		
		// 通知團主有新報名待審核
		noticeService.sendToMember(activity.getMember().getMemberId(), null,
			    "您的活動「" + activity.getActivityName() + "」有新的報名申請待審核。", (byte) 2);
		
		Registration saved = regisRepo.save(regis);
		activityWebSocketHandler.broadcastSlotUpdate(activity.getActivityId(), buildSlotJson(activity));
		return saved;
	}
	
	@Transactional
	public Registration approve(Integer regisId, Member currentMember) {
	    // 1. 撈:findById + orElseThrow(自訂訊息版,lambda語法)
		Registration regis = regisRepo.findById(regisId).orElseThrow(() -> new IllegalArgumentException("報名紀錄不存在"));
	    
		Activity activity = regis.getActivity();
		if (!activity.getMember().getMemberId().equals(currentMember.getMemberId())) {
	        throw new IllegalStateException("無權審核此報名");
	    }
		
		// 2. 驗:狀態不是 0 → throw
		if (regis.getRegisStatus() != 0) {
			 throw new IllegalStateException("此筆申請非為待審核狀態，無法審核");
		}
		 // 3. 判:正取未滿 → 進正取；正取滿、備取未滿 → 進備取；都滿 → 判失敗(理論上不該發生,防線)
	    if (activity.getRegisCount() < activity.getCapacity()) {
	        regis.setRegisStatus(1);
	        activity.setRegisCount(activity.getRegisCount() + 1);
	        // 通知會員報名成功(正取)
	        noticeService.sendToMember(regis.getMember().getMemberId(), null,
	                "您報名的活動「" + activity.getActivityName() + "」已審核通過（正取）。", (byte) 2);
	        activityWebSocketHandler.broadcastSlotUpdate(activity.getActivityId(), buildSlotJson(activity));
	    } else if (activity.getWaitlistCount() < activity.getWaitlistCapacity()) {
	        regis.setRegisStatus(4);
	        activity.setWaitlistCount(activity.getWaitlistCount() + 1);
	        // 通知會員候補成功(備取)
	        noticeService.sendToMember(regis.getMember().getMemberId(), null,
	                "您報名的活動「" + activity.getActivityName() + "」目前為候補狀態。", (byte) 2);
	        activityWebSocketHandler.broadcastSlotUpdate(activity.getActivityId(), buildSlotJson(activity));
	    } else {
	        regis.setRegisStatus(2);
	        // 通知會員報名失敗
	        noticeService.sendToMember(regis.getMember().getMemberId(), null,
	                "很抱歉，您報名的活動「" + activity.getActivityName() + "」名額不足，報名失敗。", (byte) 2);
	        activityWebSocketHandler.broadcastSlotUpdate(activity.getActivityId(), buildSlotJson(activity));
	    }
		return regis;
	}
	
	@Transactional
	public Registration reject(Integer regisId, Integer rejectReason, String rejectNote, Member currentMember) {
	    // 1. 撈:findById + orElseThrow
	    Registration regis = regisRepo.findById(regisId).orElseThrow(() -> new IllegalArgumentException("報名紀錄不存在"));

	    Activity activity = regis.getActivity();
	    if (!activity.getMember().getMemberId().equals(currentMember.getMemberId())) {
	        throw new IllegalStateException("無權審核此報名");
	    }

	    // 2. 驗:狀態不是 0 → throw
	    if (regis.getRegisStatus() != 0) {
	        throw new IllegalStateException("此筆申請非為待審核狀態，無法審核");
	    }

	    // 3. 拒絕:狀態改為2,記錄理由
	    regis.setRegisStatus(2);
	    regis.setRejectReason(rejectReason);
	    regis.setRejectNote(rejectNote);
	    // 通知會員報名遭拒絕
	    noticeService.sendToMember(regis.getMember().getMemberId(), null,
	    	    "很抱歉，您報名的活動「" + activity.getActivityName() + "」未通過審核。", (byte) 2);
	    activityWebSocketHandler.broadcastSlotUpdate(activity.getActivityId(), buildSlotJson(activity));
	    return regis;
	}
	
	
	
	@Transactional
	public Registration cancel(Integer regisId, Integer cancelReason, String cancelNote, Member currentMember) {
		// 1. 撈:findById + orElseThrow
		Registration regis = regisRepo.findById(regisId).orElseThrow(() -> new IllegalArgumentException("報名紀錄不存在"));
		
		if (!regis.getMember().getMemberId().equals(currentMember.getMemberId())) {
		        throw new IllegalStateException("無權操作此報名");
		    }
		// 2. 驗:狀態不是 0,1,4 
		if (regis.getRegisStatus() != 0 && regis.getRegisStatus() != 1 && regis.getRegisStatus() != 4) {
			 throw new IllegalStateException("此筆報名申請無法取消");
		}
		
		if (LocalDateTime.now().isAfter(regis.getActivity().getActivityStart())) {
		    throw new IllegalStateException("活動已開始，無法取消報名");
		}
		
		if (regis.getActivity().getActivityStatus() == 4) {
		    throw new IllegalStateException("活動已取消，無須取消報名");
		}
		// 3. 依原本狀態扣掉對應的名額計數
	    Activity activity = regis.getActivity();
	    if (regis.getRegisStatus() == 1) {
	        activity.setRegisCount(activity.getRegisCount() - 1);
	        noticeService.sendToMember(activity.getMember().getMemberId(), null,
	            "您的活動「" + activity.getActivityName() + "」有一位正取成員取消了報名，名額已釋出。", (byte) 2);
	    } else if (regis.getRegisStatus() == 4) {
	        activity.setWaitlistCount(activity.getWaitlistCount() - 1);
	        noticeService.sendToMember(activity.getMember().getMemberId(), null,
	            "您的活動「" + activity.getActivityName() + "」有一位候補成員取消了報名。", (byte) 2);
	    }

		
		regis.setRegisStatus(3); // 狀態改為3已取消報名
		regis.setCancelledAt(LocalDateTime.now()); 
		regis.setCancelReason(cancelReason);
		regis.setCancelNote(cancelNote);
		activityWebSocketHandler.broadcastSlotUpdate(activity.getActivityId(), buildSlotJson(activity));
		return regis;
	}
	
	@Transactional
	public Registration review(Integer regisId, Integer rating, String reviewContent, Member currentMember) {
		// 1. 撈:findById + orElseThrow
		Registration regis = regisRepo.findById(regisId).orElseThrow(() -> new IllegalArgumentException("報名紀錄不存在"));
		
		if (!regis.getMember().getMemberId().equals(currentMember.getMemberId())) {
	        throw new IllegalStateException("無權評論此報名");
	    }
		// 2. 驗:報名狀態為1，且活動已結束
		Activity activity = regis.getActivity();
		if (regis.getRegisStatus() != 1 || LocalDateTime.now().isBefore(activity.getActivityEnd())) {
			 throw new IllegalStateException("此筆報名目前無法填寫活動評論");
		}
		
		// 3. 擋重複評論
		if (regis.getReviewedAt() != null) {
			throw new IllegalStateException("此筆報名已填寫過活動評論");
		}
		
		// 4.評分必填
		if (rating == null) {
			throw new IllegalArgumentException("請填寫評分");
		}
		
		// 5. 填寫
		regis.setReviewedAt(LocalDateTime.now()); 
		regis.setRating(rating);
		regis.setReviewContent(reviewContent);
		
		return regis;
	}
	
	public List<Registration> getMyRegistrations(Member member) {
	    return regisRepo.findByMemberWithActivity(member);
	}

	public List<Registration> getRegistrationsByActivity(Activity activity, Member currentMember) {
		if (!activity.getMember().getMemberId().equals(currentMember.getMemberId())) {
	        throw new IllegalStateException("無權查看此活動的報名名單");
	    }
	    return regisRepo.findByActivityWithMember(activity);
	}
	
	// 查詢某活動的評論(公開資訊)
	public List<Registration> getReviewsByActivity(Activity activity) {
	    return regisRepo.findReviewsByActivity(activity);
	}
	// 計算未審核人數
	public long countPendingByActivity(Activity activity) {
	    return regisRepo.countByActivityAndRegisStatusIn(activity, List.of(0));
	}
	
	// 查詢各活動的未審核人數
	public Map<Integer, Long> getPendingCountMap(List<Activity> activities) {
	    Map<Integer, Long> map = new HashMap<>();
	    List<Object[]> results = regisRepo.countPendingGroupByActivity(activities);
	    for (Object[] row : results) {
	        Integer activityId = (Integer) row[0];
	        Long count = (Long) row[1];
	        map.put(activityId, count);
	    }
	    return map;
	}
	
	@Transactional
	public Registration promoteToConfirmed(Integer regisId, Member currentMember) {
	    // 1. 撈:findById + orElseThrow
	    Registration regis = regisRepo.findById(regisId).orElseThrow(() -> new IllegalArgumentException("報名紀錄不存在"));

	    Activity activity = regis.getActivity();
	    if (!activity.getMember().getMemberId().equals(currentMember.getMemberId())) {
	        throw new IllegalStateException("無權操作此報名");
	    }

	    // 2. 驗:狀態必須是備取(4)
	    if (regis.getRegisStatus() != 4) {
	        throw new IllegalStateException("此筆報名非為備取狀態，無法遞補");
	    }

	    // 3. 驗:正取要有空位
	    if (activity.getRegisCount() >= activity.getCapacity()) {
	        throw new IllegalStateException("正取名額已滿，無法遞補");
	    }

	    // 4. 轉正取:狀態改1,計數互換
	    regis.setRegisStatus(1);
	    activity.setRegisCount(activity.getRegisCount() + 1);
	    activity.setWaitlistCount(activity.getWaitlistCount() - 1);
	    // 通知會員已從候補遞補為正取
	    noticeService.sendToMember(regis.getMember().getMemberId(), null,
	    	    "您候補的活動「" + activity.getActivityName() + "」已遞補為正取。", (byte) 2);
	    activityWebSocketHandler.broadcastSlotUpdate(activity.getActivityId(), buildSlotJson(activity));
	    return regis;
	}
	
	// 組出目前活動的正取/備取/未審核人數,給WebSocket廣播用
	private String buildSlotJson(Activity activity) {
	    long pendingCount = regisRepo.countByActivityAndRegisStatusIn(activity, List.of(0));
	    return "{\"正取\":" + activity.getRegisCount()
	         + ",\"備取\":" + activity.getWaitlistCount()
	         + ",\"未審核\":" + pendingCount + "}";
	}
}
