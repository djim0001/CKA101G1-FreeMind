package com.freemind.activity.follow.model;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.freemind.activity.activity.model.Activity;

public interface ActivityFollowRepository extends JpaRepository<ActivityFollow, ActivityFollowId> {

	// 我的關注活動清單(先查關注哪些活動編號，再去撈完整活動)
	@Query("SELECT a FROM Activity a WHERE a.activityId IN "
		     + "(SELECT f.id.activityId FROM ActivityFollow f WHERE f.id.memberId = :memberId)")
		List<Activity> findFollowedActivities(@Param("memberId") Integer memberId);


}
