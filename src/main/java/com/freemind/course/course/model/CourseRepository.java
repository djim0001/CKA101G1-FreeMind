package com.freemind.course.course.model;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Integer>, JpaSpecificationExecutor<Course> {

	// psych_function
	Page<Course> findByPsychologistPsychId(Integer psychId, Pageable pageable);

	Page<Course> findByCourseStatus(Byte courseStatus, Pageable pageable);

	Page<Course> findByCourseStatusNot(Byte courseStatus, Pageable pageable);

	Page<Course> findByCourseIdIn(List<Integer> courseIds, Pageable pageable);

	long countByPsychologist_PsychId(Integer psychId);
	
	@Query("""
		    SELECT DISTINCT c
		    FROM Course c
		    LEFT JOIN c.courseCategories cc
		    WHERE c.psychologist.psychId = :psychId
		      AND (
		            :keyword IS NULL
		            OR :keyword = ''
		            OR LOWER(c.courseName)
		                LIKE LOWER(CONCAT('%', :keyword, '%'))
		            OR LOWER(cc.courseCatName)
		                LIKE LOWER(CONCAT('%', :keyword, '%'))
		          )
		      AND (
		            :courseStatus IS NULL
		            OR c.courseStatus = :courseStatus
		          )
		    """)
		Page<Course> searchCourseByPsychologist(
		        @Param("keyword") String keyword,
		        @Param("psychId") Integer psychId,
		        @Param("courseStatus") Byte courseStatus,
		        Pageable pageable
		);

	// admin_function
	int countByCourseStatus(Byte courseStatus);

	@Query("""
			SELECT c
			FROM Course c
			JOIN c.psychologist p
			WHERE c.courseStatus = :courseStatus
			  AND (
			        LOWER(c.courseName) LIKE LOWER(CONCAT('%', :keyword, '%'))
			        OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
			      )
			""")
	Page<Course> searchByCourseOrPsychologist(@Param("keyword") String keyword,
			@Param("courseStatus") Byte courseStatus, Pageable pageable);

	// member_function
	@Query("""
			SELECT DISTINCT c
			FROM Course c
			LEFT JOIN c.psychologist p
			LEFT JOIN c.courseCategories cc
			WHERE c.courseId IN :courseIds
			  AND (
			        :keyword = ''
			        OR LOWER(c.courseName) LIKE LOWER(CONCAT('%', :keyword, '%'))
			        OR LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
			        OR LOWER(cc.courseCatName) LIKE LOWER(CONCAT('%', :keyword, '%'))
			      )
			""")
	Page<Course> searchBookmarkCourses(@Param("courseIds") List<Integer> courseIds, @Param("keyword") String keyword,
			Pageable pageable);

	Page<Course> findByCourseStatusAndCourseCategories_CourseCatId(Byte courseStatus, Integer courseCatId,
			Pageable pageable);

}
