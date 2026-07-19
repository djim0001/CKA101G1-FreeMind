package com.freemind.activity.activity.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.freemind.activity.activity.model.ActivityService;

@Component
public class ActivityScheduler {

	private final ActivityService activityService;

	public ActivityScheduler(ActivityService activityService) {
		this.activityService = activityService;
	}

	// 定時檢查是否有排程時間已到、待發布的活動
	@Scheduled(cron = "${activity.scheduler.cron}")
//  @Scheduled(fixedDelay = 5000)
	public void checkScheduledPublish() {
		System.out.println("開始檢查活動排程發布");
		activityService.publishScheduledActivities();
	}
}
