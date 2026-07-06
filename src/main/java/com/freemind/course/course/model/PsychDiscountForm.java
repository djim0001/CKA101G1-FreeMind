package com.freemind.course.course.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class PsychDiscountForm {

    @NotNull(message = "課程編號不能空白")
    private Integer courseId;

    @NotNull(message = "心理師折扣不能空白")
    @DecimalMin(value = "0.01", message = "折扣必須大於 0")
    @DecimalMax(value = "0.99", message = "折扣不能大於 1")
    @Digits(integer = 1, fraction = 2, message = "折扣格式最多為 1 位整數、2 位小數")
    private BigDecimal psychDiscount;

    @NotNull(message = "折扣時間不能空白")
    @Min(value = 1, message = "折扣時間至少 1 個月")
    @Max(value = 12, message = "折扣時間最多 12 個月")
    private Integer discountMonth;

    @NotNull(message = "折扣開始時間不能空白")
    private LocalDate discountStart;

	public Integer getCourseId() {
		return courseId;
	}

	public void setCourseId(Integer courseId) {
		this.courseId = courseId;
	}

	public BigDecimal getPsychDiscount() {
		return psychDiscount;
	}

	public void setPsychDiscount(BigDecimal psychDiscount) {
		this.psychDiscount = psychDiscount;
	}

	public Integer getDiscountMonth() {
		return discountMonth;
	}

	public void setDiscountMonth(Integer discountMonth) {
		this.discountMonth = discountMonth;
	}

	public LocalDate getDiscountStart() {
		return discountStart;
	}

	public void setDiscountStart(LocalDate discountStart) {
		this.discountStart = discountStart;
	}

    
}
