package com.freemind.activity.activity.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.freemind.activity.activity.util.HibernateUtil_CompositeQuery_Activity;

@Service
public class ActivityService {
	
	@Autowired
	ActivityRepository repository;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	public void addActivity(Activity activity) {
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
	public List<Activity> getAllForMember(Map<String, String[]> map) {
		return HibernateUtil_CompositeQuery_Activity.getAllC_ForMember(map, sessionFactory.openSession());
	}
	
	// 查看自己發起的活動
	public List<Activity> getAllForOwner(Map<String, String[]> map, Integer memberId) {
	    return HibernateUtil_CompositeQuery_Activity.getAllC_ForOwner(map, memberId, sessionFactory.openSession());
	}
}
