package com.freemind.course.course.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.freemind.login.psychologist.model.Psychologist;

public interface CourseRepository extends JpaRepository<Course, Integer> {

	// psych_function
//	Page<Course> findByPsychId(Integer psychId, Pageable pageable);
	Page<Course> findByPsychologistPsychId(Integer psychId, Pageable pageable);
	Page<Course> findByCourseStatus(Byte courseStatus, Pageable pageable);
	Page<Course> findByCourseStatusNot(Byte courseStatus, Pageable pageable);
//	Page<Course> findByPsychologistPsychId(Integer psychId, Pageable pageable);
	
	// admin_function

	
	// member_function

}
