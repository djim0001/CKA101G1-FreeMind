package com.freemind.course.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.freemind.course.course.model.Course;

import lombok.Getter;

@Getter
public class CourseListRecommendDTO {
	private List<CourseRecommendDTO> courseList;
	
	public CourseListRecommendDTO(List<Course> courseList) {
		this.courseList = courseList.stream()
				                    .map(course -> new CourseRecommendDTO(course))
				                    .collect(Collectors.toList());
	}
}
