package com.freemind.course.dto;

import java.time.LocalDateTime;

public class CourseQaSearchCondition {

    // 課程名稱、會員姓名、提問內容的模糊搜尋
    private String keyword;

    // 提問時間起始
    private LocalDateTime askedAtStart;

    // 提問時間結束
    private LocalDateTime askedAtEnd;

    // 回覆狀態
    private AnswerStatus answerStatus;

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public LocalDateTime getAskedAtStart() {
        return askedAtStart;
    }

    public void setAskedAtStart(LocalDateTime askedAtStart) {
        this.askedAtStart = askedAtStart;
    }

    public LocalDateTime getAskedAtEnd() {
        return askedAtEnd;
    }

    public void setAskedAtEnd(LocalDateTime askedAtEnd) {
        this.askedAtEnd = askedAtEnd;
    }

    public AnswerStatus getAnswerStatus() {
        return answerStatus;
    }

    public void setAnswerStatus(AnswerStatus answerStatus) {
        this.answerStatus = answerStatus;
    }
}