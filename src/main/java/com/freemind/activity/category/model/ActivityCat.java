package com.freemind.activity.category.model;

import java.io.Serializable;
import java.util.Set;

import com.freemind.activity.activity.model.Activity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="activity_categories")
public class ActivityCat implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="activity_cat_id")
	private Integer activityCatId;
	
	@OneToMany(mappedBy = "activityCat")
	private Set<Activity> activities;
	
	@Column(name="activity_cat_name", length=50)
	@NotEmpty(message="活動分類名稱：請勿空白")
	@Size(max=50, message="活動分類名稱：長度不能超過{max}")
	private String activityCatName;
	
	public ActivityCat() {
	}
	
	public ActivityCat(Integer activityCatId, String activityCatName) {
        this.activityCatId = activityCatId;
        this.activityCatName = activityCatName;
    }
	
	public Integer getActivityCatId() {
		return activityCatId;
	}
	public void setActivityCatId(Integer activityCatId) {
		this.activityCatId = activityCatId;
	}
	public String getActivityCatName() {
		return activityCatName;
	}
	public void setActivityCatName(String activityCatName) {
		this.activityCatName = activityCatName;
	}

	public Set<Activity> getActivities() {
		return activities;
	}

	public void setActivities(Set<Activity> activities) {
		this.activities = activities;
	}
}
