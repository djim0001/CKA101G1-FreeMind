package com.freemind.course.course.model;

import java.util.List;

//做CRUD的地方
import org.springframework.data.jpa.repository.JpaRepository;

import com.freemind.login.psychologist.entity.Psychologist;

public interface CourseQaCommentRepository
        extends JpaRepository<CourseQaComment, Integer> {

    // 心理師查看自己所有課程收到的提問(?
    List<CourseQaComment>
    findByCoursePsychologistPsychIdOrderByAskedAtDesc(
            Integer psychId
    );

    // 會員查看自己在某門課的提問(?
    List<CourseQaComment>
    findByCourseCourseIdAndMemberMemberIdOrderByAskedAtDesc(
            Integer courseId,
            Integer memberId
    );
    // 查詢某一堂課的所有 QA
    List<CourseQaComment> findByCourse_CourseId(Integer courseId);
    
    // 查詢某一堂課的所有 QA，照提問時間排序
    List<CourseQaComment> findByCourse_CourseIdOrderByAskedAtDesc(Integer courseId);
    
    int countByCoursePsychologistPsychIdAndCourseAnswerIsNull(
            Integer psychId
    );
    
}