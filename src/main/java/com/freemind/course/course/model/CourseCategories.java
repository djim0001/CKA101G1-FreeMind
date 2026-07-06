package com.freemind.course.course.model;

import java.util.Set;

import com.freemind.course.coupon.model.MemberCoupon;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "course_categories")
public class CourseCategories {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "course_cat_id")
	private Integer courseCatId;
	@Column(name = "course_cat_name")
	@NotEmpty(message="課程分類名稱：請勿空白")
	@Pattern(regexp = "^[(\u4e00-\u9fa5)(a-zA-Z0-9_)]{2,10}$", 
		message = "課程分類名稱: 只能是中、英文字母、數字和_ , 且長度必需在2到10之間")
	private String courseCatName;
	
	@OneToMany(mappedBy = "courseCategories", cascade = CascadeType.ALL)
	@OrderBy("course_id asc")
	private Set<Course> courses;
	
	
	public Integer getCourseCatId() {
		return courseCatId;
	}
	public void setCourseCatId(Integer courseCatId) {
		this.courseCatId = courseCatId;
	}
	public String getCourseCatName() {
		return courseCatName;
	}
	public void setCourseCatName(String courseCatName) {
		this.courseCatName = courseCatName;
	}
	public Set<Course> getCourses() {
		return courses;
	}
	public void setCourses(Set<Course> courses) {
		this.courses = courses;
	}
	
}
