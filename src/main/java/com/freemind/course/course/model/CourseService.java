package com.freemind.course.course.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.freemind.course.util.CourseSortUtil;

@Service
public class CourseService {

	private final CourseRepository repository;
	private final StringRedisTemplate stringRedisTemplate;
	
	@Value("${app.course.page-size:5}")
	private int coursePageSize;
	
	public CourseService(
			CourseRepository repository, 
			StringRedisTemplate stringRedisTemplate) {
		this.repository = repository;
		this.stringRedisTemplate = stringRedisTemplate;
	}



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
	
	
	public Page<Course> findCoursesExcludeStatus(Byte courseStatus, int page, String orderBy){
		Pageable pageable = PageRequest.of(
	            page,
	            coursePageSize,
	            CourseSortUtil.getCourseSort(orderBy)
	    );		
		return repository.findByCourseStatusNot(courseStatus, pageable);
	}

	public Page<Course> findCourseByCourseStstus(Byte courseStatus, int page, String orderBy){
		Pageable pageable = PageRequest.of(
	            page,
	            coursePageSize,
	            CourseSortUtil.getCourseSort(orderBy)
	    );	
		return repository.findByCourseStatus(courseStatus, pageable);
	}
	
	// psych_function
	public Page<Course> getCoursesByPsychId(Integer psychId, Integer page, String orderBy) {

	    if (psychId == null) {
	        throw new IllegalArgumentException("psychId 不可為 null");
	    }

	    if (page == null || page < 0) {
	        page = 0;
	    }

	    Pageable pageable = PageRequest.of(
	            page,
	            coursePageSize,
	            CourseSortUtil.getCourseSort(orderBy)
	    );

	    return repository.findByPsychologistPsychId(psychId, pageable);
	}
	

	// admin_function
	
	// member_function
	public void checkAllCourseStatus() {
		List<Course> allCourse = getAllCourse();
		for(Course course : allCourse) {
			if(course.getCourseStatus() == 2) {
				course.setCourseStatus((byte)4);
				course.setListedAt(LocalDateTime.now());
				repository.save(course);
			}
		}
	}
	// 收藏課程
	public void addCourseBookmark(Integer memberId, Integer courseId) {
		String key = "bookmark:member:" + memberId;
		stringRedisTemplate.opsForSet().add(key, String.valueOf(courseId));
	}
	public void removeCourseBookmark(Integer memberId, Integer courseId) {
		String key = "bookmark:member:" + memberId;
		stringRedisTemplate.opsForSet().remove(key, String.valueOf(courseId));
	}
	public boolean isCourseInBookmark(Integer memberId, Integer courseId) {
		String key = "bookmark:member:" + memberId;
		Boolean result = stringRedisTemplate.opsForSet().isMember(key, String.valueOf(courseId));
		return Boolean.TRUE.equals(result);
	}
	public Page<Course> getBookmarkCourses(Integer memberId, Integer page, String orderBy) {

	    if (page == null || page < 1) {
	        page = 0;
	    }
	    String key = "bookmark:member:" + memberId;

	    Set<String> courseIdSet = stringRedisTemplate
	            .opsForSet()
	            .members(key);
	    if (courseIdSet == null || courseIdSet.isEmpty()) {
	        return Page.empty();
	    }
	    List<Integer> courseIds = courseIdSet.stream()
	            .map(Integer::valueOf)
	            .toList();
	    Pageable pageable = PageRequest.of(page, coursePageSize, CourseSortUtil.getCourseSort(orderBy));
	    return repository.findByCourseIdIn(courseIds, pageable);
	}

}
