package com.freemind.activity.activity.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.freemind.activity.activity.util.HibernateUtil_CompositeQuery_Activity;
import com.freemind.activity.follow.model.ActivityFollowId;
import com.freemind.activity.follow.model.ActivityFollowRepository;

@Service
public class ActivityService {
	
	@Autowired
	private ActivityRepository repository;
	
	@Autowired
	private  SessionFactory sessionFactory;
	
	public void addActivity(Activity activity) {
	    LocalDateTime now = LocalDateTime.now();

	    if (!activity.getRegisStart().isAfter(now)) {
	        throw new RuntimeException("報名開始時間，必須晚於現在時間");
	    }
	    if (!activity.getRegisEnd().isAfter(activity.getRegisStart())) {
	        throw new RuntimeException("報名截止時間，必須晚於報名開始時間");
	    }
	    if (!activity.getActivityStart().isAfter(activity.getRegisEnd())) {
	        throw new RuntimeException("活動開始時間，必須晚於報名截止時間");
	    }
	    if (!activity.getActivityEnd().isAfter(activity.getActivityStart())) {
	        throw new RuntimeException("活動結束時間，必須晚於活動開始時間");
	    }
		
		activity.setActivityStatus(0);  // 活動狀態預設為「待審核」
		activity.setRegisCount(0);      // 報名人數預設為0
		activity.setCreatedAt(LocalDateTime.now());  // 系統自動帶入建立時間
		repository.save(activity);
	}
	
	public void updateActivity(Activity activity) {
		// 用活動ID，回頭去資料庫，把『目前實際存在資料庫裡』的這筆資料，重新查一次」——可知道「修改前」的真實狀態
		Activity existing = repository.findById(activity.getActivityId()).orElse(null);
		    
		// 發起者只能修改活動狀態為0：待審核、3：已退回的活動，其他活動狀態不可修改
	    if (existing == null || (existing.getActivityStatus() != 0 && existing.getActivityStatus() != 3)) {
	        throw new RuntimeException("此活動目前狀態不可修改");
	    }
	    
	    activity.setMember(existing.getMember());   // 找出原本的發起人，設定到即將要存檔的activity身上
	    activity.setRegisCount(existing.getRegisCount()); // 補回目前報名人數，不讓使用者的表單洗掉這個值
	    
	    System.out.println("=== 除錯：Service收到的picture是否為null = " + (activity.getPicture() == null));
	    
	    // 保留原始圖片
	    if (activity.getPicture() == null) { 
	        activity.setPicture(existing.getPicture());
	    }
	    
	    activity.setUpdatedAt(LocalDateTime.now());
	    activity.setActivityStatus(0);  // 修改後，活動狀態重新改回待審核
	    repository.save(activity);
		
	}
	
	public void deleteActivity(Integer activityId) {
		if(repository.existsById(activityId))
			repository.deleteById(activityId);
	}
	
	public Activity getOneActivity(Integer activityId) {
		Optional<Activity> optional = repository.findById(activityId);
		return optional.orElse(null);
	}
	
	public List<Activity> getAll(){
		return repository.findAll();
	}
	
	// 給會員使用的複合查詢
	public List<Activity> getAllForMember(Map<String, String[]> map, Integer currentPage) {
	    return HibernateUtil_CompositeQuery_Activity.getAllC_ForMember(map, currentPage, sessionFactory.openSession());
	}
	
	// 分頁功能，算資料筆數
	public long getTotalCountForMember(Map<String, String[]> map) {
	    return HibernateUtil_CompositeQuery_Activity.getTotalCount_ForMember(map, sessionFactory.openSession());
	}
	
	// 查看自己發起的活動
	public List<Activity> getAllForOwner(Map<String, String[]> map, Integer memberId, Integer currentPage) {
	    return HibernateUtil_CompositeQuery_Activity.getAllC_ForOwner(map, memberId, currentPage, sessionFactory.openSession());
	}
	
	// 分頁功能，算資料筆數
	public long getTotalCountForOwner(Map<String, String[]> map, Integer memberId) {
	    return HibernateUtil_CompositeQuery_Activity.getTotalCount_ForOwner(map, memberId, sessionFactory.openSession());
	}
	
	// 發起者取消活動
	public void cancelActivity(Integer activityId, String cancelNote) {
	    Activity existing = repository.findById(activityId).orElse(null);

	    if (existing == null) {
	        throw new RuntimeException("查無此活動");
	    }

	    Integer status = existing.getActivityStatus();
	    if (status != 0 && status != 1 && status != 2 && status != 5) {
	        throw new RuntimeException("此活動目前狀態不可取消");
	    }
	    
	    if (existing.getActivityEnd() != null && LocalDateTime.now().isAfter(existing.getActivityEnd())) {
	        throw new RuntimeException("活動已經結束，無法取消");
	    }

	    existing.setActivityStatus(4);  // 改成「取消」
	    existing.setCancelNote(cancelNote);  // 記錄取消原因
	    existing.setUpdatedAt(LocalDateTime.now());
	    repository.save(existing);
	}
	
	// 發起者延期已發布的活動
	public void postponeActivity(Integer activityId, String postponeNote) {
	    Activity existing = repository.findById(activityId).orElse(null);

	    if (existing == null || existing.getActivityStatus() != 2) {
	        throw new RuntimeException("此活動目前狀態不可延期");
	    }
	    
	    if (existing.getActivityEnd() != null && LocalDateTime.now().isAfter(existing.getActivityEnd())) {
	        throw new RuntimeException("活動已經結束，無法延期");
	    }

	    existing.setActivityStatus(5);
	    existing.setPostponeNote(postponeNote);
	    existing.setUpdatedAt(LocalDateTime.now());
	    repository.save(existing);
	}
	
	// 發起者變更需延期活動的時間
	public void confirmNewSchedule(Integer activityId, LocalDateTime activityStart, LocalDateTime activityEnd,
            LocalDateTime regisStart, LocalDateTime regisEnd) {
		Activity existing = repository.findById(activityId).orElse(null);
		
		if (existing == null || existing.getActivityStatus() != 5) {
		throw new RuntimeException("此活動目前狀態不可變更時間");
		}
		
		existing.setActivityStart(activityStart);
		existing.setActivityEnd(activityEnd);
		
		if (regisStart != null) {
		existing.setRegisStart(regisStart);
		}
		if (regisEnd != null) {
		existing.setRegisEnd(regisEnd);
		}
		
		existing.setActivityStatus(2);
		existing.setUpdatedAt(LocalDateTime.now());
		repository.save(existing);
	}
	
	// 後台員工手動發布活動
	public void publishActivity(Integer activityId) {
	    Activity existing = repository.findById(activityId).orElse(null);

	    if (existing == null || existing.getActivityStatus() != 1) {
	        throw new RuntimeException("此活動目前狀態不可發布");
	    }

	    existing.setActivityStatus(2);  // 改成「已發布」
	    existing.setPublishedAt(LocalDateTime.now());  // 發布時間，自動寫入當下
	    existing.setUpdatedAt(LocalDateTime.now());
	    repository.save(existing);
	}
	
	// 後台員工查看活動列表
	public List<Activity> getAllForAdmin(Map<String, String[]> map, Integer currentPage) {
	    return HibernateUtil_CompositeQuery_Activity.getAllC_ForAdmin(map, currentPage, sessionFactory.openSession());
	}

	public long getTotalCountForAdmin(Map<String, String[]> map) {
	    return HibernateUtil_CompositeQuery_Activity.getTotalCount_ForAdmin(map, sessionFactory.openSession());
	}
	
	// 後台員工審核活動
	public void approveActivity(Integer activityId) {
	    Activity existing = repository.findById(activityId).orElse(null);

	    if (existing == null || existing.getActivityStatus() != 0) {
	        throw new RuntimeException("此活動目前狀態不可審核");
	    }

	    existing.setActivityStatus(1);  // 改成「已審核」
	    existing.setReviewedAt(LocalDateTime.now());
	    existing.setUpdatedAt(LocalDateTime.now());
	    repository.save(existing);
	}
	
	// 後台員工退回活動
	public void rejectActivity(Integer activityId, Integer rejectReason, String rejectNote) {
	    Activity existing = repository.findById(activityId).orElse(null);

	    if (existing == null || existing.getActivityStatus() != 0) {
	        throw new RuntimeException("此活動目前狀態不可退回");
	    }

	    existing.setActivityStatus(3);  // 改成「已退回」
	    existing.setRejectReason(rejectReason);
	    existing.setRejectNote(rejectNote);
	    existing.setReviewedAt(LocalDateTime.now());
	    existing.setUpdatedAt(LocalDateTime.now());
	    repository.save(existing);
	}
}
