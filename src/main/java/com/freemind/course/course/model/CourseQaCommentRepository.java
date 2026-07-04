package com.freemind.course.course.model;

import java.util.List;
  //做CRUD的地方
import org.springframework.data.jpa.repository.JpaRepository;

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
}