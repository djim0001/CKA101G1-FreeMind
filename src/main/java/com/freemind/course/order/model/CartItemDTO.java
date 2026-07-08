package com.freemind.course.order.model;

import java.math.BigDecimal;

public class CartItemDTO {

    private Integer courseId;
    private String courseName;
    private String psychologistName;
    private Integer price;
    private BigDecimal psychDiscount;
    private BigDecimal subtotal;
	public Integer getCourseId() {
		return courseId;
	}
	public void setCourseId(Integer courseId) {
		this.courseId = courseId;
	}
	public String getCourseName() {
		return courseName;
	}
	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}
	public String getPsychologistName() {
		return psychologistName;
	}
	public void setPsychologistName(String psychologistName) {
		this.psychologistName = psychologistName;
	}
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
	public BigDecimal getPsychDiscount() {
		return psychDiscount;
	}
	public void setPsychDiscount(BigDecimal psychDiscount) {
		this.psychDiscount = psychDiscount;
	}
	public BigDecimal getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}
	

}
