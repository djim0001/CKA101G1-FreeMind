package com.freemind.course.course.model;

import java.util.List;
import java.util.Optional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

	@Autowired
	CourseRepository repository;

	@Autowired
	private SessionFactory sessionFactory;

	@Value("${app.course.page-size:5}")
	private int coursePageSize;

	public void addCourse(Course course) {
		repository.save(course);
	}

	public void updateCourse(Course course) {
		repository.save(course);
	}

	public Course getOneCourse(Integer courseId) {
		Optional<Course> optional = repository.findById(courseId);
		return optional.orElse(null);
	}

	public List<Course> getAllCourse() {
		return repository.findAll();
	}
	
	public Page<Course> findCoursesExcludeStatus(Byte courseStatus, int page){
		
		Pageable pageable = PageRequest.of(page, coursePageSize, Sort.by("courseId").ascending());
		return repository.findByCourseStatusNot(courseStatus, pageable);
	}
	
	public Page<Course> findCourseByCourseStstus(Byte courseStatus, int page){
		Pageable pageable = PageRequest.of(page, coursePageSize, Sort.by("courseId").ascending());
		return repository.findByCourseStatus(courseStatus, pageable);
	}

	// psych_function
	public Page<Course> getCoursesByPsychId(Integer psychId, Integer page, String orderBy) {
		
		if(orderBy == null || orderBy.isBlank()) orderBy = "courseId";
		if (page == null || page < 0)  page = 0;
		Sort sort = switch (orderBy) {
		// 依課程分類排序
		case "courseCategories" -> Sort.by("courseCategories.courseCatId").descending();
		// 依課程狀態
		case "courseStatusAsc" -> Sort.by("courseStatus").ascending(); // 小到大
		case "courseStatusDesc" -> Sort.by("courseStatus").descending();
		// 依次數
		case "saveCountAsc" -> Sort.by("saveCount").ascending();
		case "saveCountDesc" -> Sort.by("saveCount").descending();
		case "starCountAsc" -> Sort.by("starCount").ascending();
		case "starCountDesc" -> Sort.by("starCount").descending();
		case "reviewCountAsc" -> Sort.by("reviewCount").ascending();
		case "reviewCountDesc" -> Sort.by("reviewCount").descending();
		case "commentCountAsc" -> Sort.by("commentCount").ascending();
		case "commentCountDesc" -> Sort.by("commentCount").descending();
		// 依價格
		case "priceAsc" -> Sort.by("price").ascending();
		case "priceDesc" -> Sort.by("price").descending();

		default -> Sort.by("courseId").descending();
		};
		Pageable pageable = PageRequest.of(page, coursePageSize, sort);

		return repository.findByPsychologistPsychId(psychId, pageable);
	}

	// admin_function
	
	// member_function
	public void checkAllCourseStatus() {
		List<Course> allCourse = getAllCourse();
		for(Course course : allCourse) {
			if(course.getCourseStatus() == 2) {
				course.setCourseStatus((byte)4);
				repository.save(course);
			}
		}
	}

}
