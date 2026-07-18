package com.freemind.course.course.model;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
//做CRUD的地方
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseQaCommentRepository extends JpaRepository<CourseQaComment, Integer> {

	// 心理師查看自己所有課程收到的提問(?
	List<CourseQaComment> findByCoursePsychologistPsychIdOrderByAskedAtDesc(Integer psychId);

	@Query("""
			SELECT cq
			FROM CourseQaComment cq
			JOIN cq.course c
			JOIN cq.member m
			JOIN c.psychologist p
			WHERE p.psychId = :psychId
			  AND (
			        LOWER(c.courseName)
			            LIKE LOWER(CONCAT('%', :keyword, '%'))
			        OR LOWER(m.name)
			            LIKE LOWER(CONCAT('%', :keyword, '%'))
			      )
			""")
	Page<CourseQaComment> searchCourseQaByPsychId(@Param("keyword") String keyword, @Param("psychId") Integer psychId,
			Pageable pageable);

	// 會員查看自己在某門課的提問(?
	List<CourseQaComment> findByCourseCourseIdAndMemberMemberIdOrderByAskedAtDesc(Integer courseId, Integer memberId);

	// 查詢某一堂課的所有 QA
	List<CourseQaComment> findByCourse_CourseId(Integer courseId);

	// 查詢某一堂課的所有 QA，照提問時間排序
	List<CourseQaComment> findByCourse_CourseIdOrderByAskedAtDesc(Integer courseId);

	int countByCoursePsychologistPsychIdAndCourseAnswerIsNull(Integer psychId);

}