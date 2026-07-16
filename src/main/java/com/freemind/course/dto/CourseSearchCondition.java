package com.freemind.course.dto;

public class CourseSearchCondition {

    private String keyword;

    private Integer categoryId;

    // 課程申請狀態
    private Byte courseStatus;

    public CourseSearchCondition() {
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public Byte getCourseStatus() {
        return courseStatus;
    }

    public void setCourseStatus(Byte courseStatus) {
        this.courseStatus = courseStatus;
    }
    
    
    
}
