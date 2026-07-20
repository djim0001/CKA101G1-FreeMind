package com.freemind.activity.activity.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Integer>{
	// 查詢某個活動狀態且排程時間已到的活動
	List<Activity> findByActivityStatusAndScheduledPublishAtLessThanEqual(Integer activityStatus, LocalDateTime now);
	
	// 精選活動：查詢已發布且尚未結束的活動
	List<Activity> findByActivityStatusAndActivityEndAfter(Integer activityStatus, LocalDateTime now);
		
	// 後台卡片：計算各狀態數量用	
	long countByActivityStatus(Integer activityStatus);

}

