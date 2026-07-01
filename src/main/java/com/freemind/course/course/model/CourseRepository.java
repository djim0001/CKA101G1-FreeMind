package com.freemind.course.course.model;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Integer> {

	// psych_function
	Page<Course> findByPsychId(Integer psychId, Pageable pageable);
//	Page<Course> findByPsychologistPsychId(Integer psychId, Pageable pageable);
	
	// admin_function

	
	// member_function

}
