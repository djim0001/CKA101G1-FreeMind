package com.freemind.course.dto;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

public class CourseOrderSearchCondition {

    // 訂單編號或會員姓名
    private String keyword;

    // 0：未付款、1：已付款、2：取消訂單
    private Byte paymentStatus;

    // 查詢指定日期的訂單
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate orderedDate;

    public CourseOrderSearchCondition() {
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Byte getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(Byte paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public LocalDate getOrderedDate() {
        return orderedDate;
    }

    public void setOrderedDate(LocalDate orderedDate) {
        this.orderedDate = orderedDate;
    }
}