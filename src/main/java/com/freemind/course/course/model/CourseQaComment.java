package com.freemind.course.course.model;

import java.time.LocalDateTime;

import com.freemind.login.member.model.Member;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "course_qa_comments")
public class CourseQaComment {
	
	private Integer questionId;
	private Course course;
	private Member member;
	private LocalDateTime askedAt;
	private String courseQuestion;
	private LocalDateTime answeredAt;
	private String courseAnswer;
	
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "question_id")
	
	public Integer getQuestionId() {
		return questionId;
	}
	public void setQuestionId(Integer questionId) {
		this.questionId = questionId;
	}


	@ManyToOne
	@JoinColumn(name = "course_id", referencedColumnName = "course_id", nullable = false)
	@NotNull(message = "課程不可為空")
	
	public Course getCourse() {
		return course;
	}
	public void setCourse(Course course) {
		this.course = course;
	}

	@ManyToOne
	@JoinColumn(name = "member_id", referencedColumnName = "member_id", nullable = false)
	@NotNull(message = "會員不可為空")
	
	public Member getMember() {
		return member;
	}
	public void setMember(Member member) {
		this.member = member;
	}



	@Column(name = "asked_at", nullable = false)
	@NotNull(message = "提問時間不可為空")

	public LocalDateTime getAskedAt() {
		return askedAt;
	}

	public void setAskedAt(LocalDateTime askedAt) {
		this.askedAt = askedAt;
	}

	@Column(name = "course_question", length = 500, nullable = false)
	@NotBlank(message = "提問內容不可為空")
	@Size(max = 500, message = "提問內容不可超過 500 字")

	public String getCourseQuestion() {
		return courseQuestion;
	}

	public void setCourseQuestion(String courseQuestion) {
		this.courseQuestion = courseQuestion;
	}
	
	
	

	@Column(name = "answered_at")
	public LocalDateTime getAnsweredAt() {
		return answeredAt;
	}

	public void setAnsweredAt(LocalDateTime answeredAt) {
		this.answeredAt = answeredAt;
	}



	@Column(name = "course_answer", length = 500)
	@Size(max = 500, message = "回覆內容太多，不可以超過500個字")



	public String getCourseAnswer() {
		return courseAnswer;
	}

	public void setCourseAnswer(String courseAnswer) {
		this.courseAnswer = courseAnswer;
	}

}
