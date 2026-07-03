package com.freemind.course.course.model;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CourseCategoriesService {

	@Autowired
	CourseCategoriesRepository repository;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	public void addCourseCategories(CourseCategories courseCategories) {
		repository.save(courseCategories);
	}
	
	public CourseCategories getOneCourseCategories(Integer courseCatId) {
		Optional<CourseCategories> optional = repository.findById(courseCatId);
		return optional.orElse(null);
	}
	
	public List<CourseCategories> getAllCourseCategories(){
		return repository.findAll();
	}
	
}
