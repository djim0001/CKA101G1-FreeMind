package com.freemind.activity.follow.model;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class ActivityFollowId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "member_id")
    private Integer memberId;

    @Column(name = "activity_id")
    private Integer activityId;

    // 必備:無參數建構子、equals、hashCode
    public ActivityFollowId() {}

    public ActivityFollowId(Integer memberId, Integer activityId) {
        this.memberId = memberId;
        this.activityId = activityId;
    }
 
    public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Integer getActivityId() {
		return activityId;
	}

	public void setActivityId(Integer activityId) {
		this.activityId = activityId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(activityId, memberId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActivityFollowId other = (ActivityFollowId) obj;
		return Objects.equals(activityId, other.activityId) && Objects.equals(memberId, other.memberId);
	}
	
}