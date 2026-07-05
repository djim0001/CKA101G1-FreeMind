package com.freemind.course.course.model;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class CourseCategoriesService {

	private final CourseCategoriesRepository repository;

    public CourseCategoriesService(CourseCategoriesRepository repository) {
        this.repository = repository;
    }
	
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
