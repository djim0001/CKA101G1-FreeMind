package com.freemind.activity.follow.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "activity_follows")
public class ActivityFollow implements Serializable {
    private static final long serialVersionUID = 1L;

    @EmbeddedId   // 主鍵是這個嵌入的物件
    private ActivityFollowId id;

    @Column(name = "followed_at", nullable = false)
    private LocalDateTime followedAt;

	public ActivityFollowId getId() {
		return id;
	}

	public void setId(ActivityFollowId id) {
		this.id = id;
	}

	public LocalDateTime getFollowedAt() {
		return followedAt;
	}

	public void setFollowedAt(LocalDateTime followedAt) {
		this.followedAt = followedAt;
	}

   
}