package com.freemind.course.course.model;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.freemind.login.member.model.Member;

@Service
public class CourseQaCommentService {

    @Autowired
    private CourseQaCommentRepository repository;

    // 會員新增提問
    public CourseQaComment addQuestion(
            Course course,
            Member member,
            String courseQuestion) {

        if (course == null) {
            throw new IllegalArgumentException("課程不可為空");
        }

        if (member == null) {
            throw new IllegalArgumentException("會員不可為空");
        }

        if (courseQuestion == null || courseQuestion.isBlank()) {
            throw new IllegalArgumentException("提問內容不可為空");
        }

        if (courseQuestion.length() > 500) {
            throw new IllegalArgumentException("提問內容不可超過 500 字");
        }

        CourseQaComment comment = new CourseQaComment();
        comment.setCourse(course);
        comment.setMember(member);
        comment.setCourseQuestion(courseQuestion);
        comment.setAskedAt(LocalDateTime.now());

        return repository.save(comment);
    }

    // 根據留言編號查詢單筆提問
    public CourseQaComment getOneQuestion(Integer questionId) {
        return repository.findById(questionId).orElse(null);
    }

    // 心理師回覆提問
    public CourseQaComment answerQuestion(
            Integer questionId,
            Integer psychId,
            String courseAnswer) {

        CourseQaComment comment = getOneQuestion(questionId);

        if (comment == null) {
            throw new IllegalArgumentException("找不到此課程提問");
        }

        if (courseAnswer == null || courseAnswer.isBlank()) {
            throw new IllegalArgumentException("回覆內容不可為空");
        }

        if (courseAnswer.length() > 500) {
            throw new IllegalArgumentException("回覆內容不可超過 500 字");
        }

        Integer ownerPsychId =
                comment.getCourse()
                       .getPsychologist()
                       .getPsychId();

        if (psychId == null || !psychId.equals(ownerPsychId)) {
            throw new IllegalArgumentException("你無權回覆此課程提問");
        }

        if (comment.getCourseAnswer() != null
                || comment.getAnsweredAt() != null) {

            throw new IllegalArgumentException(
                    "此課程提問已經回覆，不能再次修改"
            );
        }

        comment.setCourseAnswer(courseAnswer);
        comment.setAnsweredAt(LocalDateTime.now());

        return repository.save(comment);
    }

    // 查詢心理師所有課程收到的提問
    public List<CourseQaComment> getQuestionsByPsychId(
            Integer psychId) {

        return repository
                .findByCoursePsychologistPsychIdOrderByAskedAtDesc(
                        psychId
                );
    }

    // 查詢會員在某門課提出的問題
    public List<CourseQaComment> getQuestionsByCourseAndMember(
            Integer courseId,
            Integer memberId) {

        return repository
                .findByCourseCourseIdAndMemberMemberIdOrderByAskedAtDesc(
                        courseId,
                        memberId
                );
    }
}