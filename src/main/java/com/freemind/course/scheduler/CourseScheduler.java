package com.freemind.course.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.freemind.course.course.model.CourseService;

@Component
public class CourseScheduler {

	private final CourseService courseService;

    public CourseScheduler(CourseService courseService) {
        this.courseService = courseService;
    }

    // 每天凌晨 3 點檢查課程狀態
    @Scheduled(cron = "${course.scheduler.cron}")
//    @Scheduled(fixedDelay = 5000)
    public void checkCourseStatus() {
    	System.out.println("開始確認課程狀態");
        courseService.checkAllCourseStatus();
    }
}
