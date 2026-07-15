package com.freemind.activity.follow.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.freemind.activity.activity.model.Activity;

@Service
public class ActivityFollowService {

    @Autowired
    private ActivityFollowRepository followRepo;

    // 關注活動
    @Transactional
    public void follow(Integer memberId, Integer activityId) {
        // 1. 組主鑰匙:new ActivityFollowId
    		ActivityFollowId activityFollowId = new ActivityFollowId(memberId, activityId);
    		
    		// 2. 擋重複:followRepo.existsById(鑰匙) 為 true
        if (followRepo.existsById(activityFollowId)) {
        		throw new IllegalStateException("已關注過此活動");
        }
    		// 3. 組實體:new ActivityFollow()
        ActivityFollow follow = new ActivityFollow();
        follow.setId(activityFollowId);
        follow.setFollowedAt(LocalDateTime.now());
        
        followRepo.save(follow);
    }

    // 取消關注
    @Transactional
    public void unfollow(Integer memberId, Integer activityId) {
    		ActivityFollowId activityFollowId = new ActivityFollowId(memberId, activityId);
		
        if (!followRepo.existsById(activityFollowId)) {
        		throw new IllegalStateException("尚未關注此活動");
        }
        
        followRepo.deleteById(activityFollowId);
    }

    // 我的關注清單
    public List<Activity> myFollows(Integer memberId) {
    		return followRepo.findFollowedActivities(memberId);
    }
    
	// 是否關注活動
	public boolean isFollowing(Integer memberId, Integer activityId) {
	    return followRepo.existsById(new ActivityFollowId(memberId, activityId));
	}
}
