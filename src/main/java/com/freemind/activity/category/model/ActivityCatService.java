package com.freemind.activity.category.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ActivityCatService {

	@Autowired
	ActivityCatRepository repository;
	
	public void addActivityCat(ActivityCat activityCat) {
		repository.save(activityCat);
	}
	
	public void updateActivityCat(ActivityCat activityCat) {
		repository.save(activityCat);
	}
	
	public void deleteActivityCat(Integer activityCatId) {
		if(repository.existsById(activityCatId))
		repository.deleteById(activityCatId);
	}
	
	public ActivityCat getOneActivityCat(Integer activityCatId) {
		Optional<ActivityCat> optional = repository.findById(activityCatId);
		return optional.orElse(null);
	}
	
	public List<ActivityCat> getAll(){
		return repository.findAll();
	}
	
	// 模糊搜尋
	public List<ActivityCat> getByNameLike(String keyword) {
		String pattern = "%" + keyword + "%";
		return repository.findByActivityCatNameLike(pattern);
	}
}
