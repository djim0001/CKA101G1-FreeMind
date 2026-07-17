package com.freemind.course.course.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.freemind.course.dto.CourseSearchCondition;
import com.freemind.course.util.CourseSortUtil;
import com.freemind.course.util.CourseSpecification;

import jakarta.transaction.Transactional;

@Service
public class CourseService {

	private final CourseRepository repository;
	private final StringRedisTemplate stringRedisTemplate;

	@Value("${app.course.page-size:5}")
	private int coursePageSize;
	private static final Byte COURSE_STATUS_LISTED = 4;

	public CourseService(CourseRepository repository, StringRedisTemplate stringRedisTemplate) {
		this.repository = repository;
		this.stringRedisTemplate = stringRedisTemplate;
	}

	public void addCourse(Course course) {
		repository.save(course);
	}

	public void updateCourse(Course course) {
		repository.save(course);
	}

	public Course getOneCourse(Integer courseId) {
		Optional<Course> optional = repository.findById(courseId);
		return optional.orElse(null);
	}

	public List<Course> getAllCourse() {
		return repository.findAll();
	}

	public Page<Course> findCoursesExcludeStatus(Byte courseStatus, int page, String orderBy) {
		Pageable pageable = PageRequest.of(page, coursePageSize, CourseSortUtil.getCourseSort(orderBy));
		return repository.findByCourseStatusNot(courseStatus, pageable);
	}

	public Page<Course> findCourseByCourseStstus(Byte courseStatus, int page, String orderBy) {
		Pageable pageable = PageRequest.of(page, coursePageSize, CourseSortUtil.getCourseSort(orderBy));
		return repository.findByCourseStatus(courseStatus, pageable);
	}

	public Page<Course> findPopularListedCourses(int page) {

		if (page < 0) {
			page = 0;
		}

		Sort popularSort = Sort.by(Sort.Order.desc("saveCount"), Sort.Order.desc("reviewCount"),
				Sort.Order.desc("starCount"), Sort.Order.desc("courseId"));

		Pageable pageable = PageRequest.of(page, coursePageSize, popularSort);

		return repository.findByCourseStatus(COURSE_STATUS_LISTED, pageable);
	}

	@Transactional
	public Page<Course> searchCourses(CourseSearchCondition condition, Integer page) {

		if (page == null || page < 0) {
			page = 0;
		}

		if (condition == null) {
			condition = new CourseSearchCondition();
		}

		Specification<Course> spec = CourseSpecification.keywordContains(condition.getKeyword())
				.and(CourseSpecification.categoryEquals(condition.getCategoryId()))
				.and(CourseSpecification.courseStatusEquals(condition.getCourseStatus()));

		Pageable pageable = PageRequest.of(page, coursePageSize, Sort.by("courseId").descending());

		return repository.findAll(spec, pageable);
	}
	
	 /**
     * 搜尋已上架課程。
     * courseStatus 固定限制為 4。
     */
	 public Page<Course> searchListedCourses(
	            String keyword,
	            Integer page,
	            Integer size,
	            String orderBy
	    ) {

	        if (page == null || page < 1) {
	            page = 1;
	        }

	        if (size == null || size <= 0) {
	            size = 10;
	        }

	        Pageable pageable = PageRequest.of(
	                page - 1,
	                size,
	                CourseSortUtil.getCourseSort(orderBy)
	        );

	        Specification<Course> spec =
	                CourseSpecification.keywordContains(keyword);

	        return repository.findAll(spec, pageable);
	    }

	@Transactional
	public Page<Course> adminSearchCourses(CourseSearchCondition condition, Integer page) {

		int pageIndex = page == null || page < 0 ? 0 : page;

		if (condition == null) {
			condition = new CourseSearchCondition();
		}

		Byte selectedStatus = condition.getCourseStatus();

		// 管理員審核清單不允許使用狀態 0 作為搜尋條件
		if (selectedStatus != null && selectedStatus.byteValue() == 0) {
			selectedStatus = null;
		}

		Specification<Course> spec = Specification.where(CourseSpecification.courseStatusNotEquals((byte) 0))
				.and(CourseSpecification.keywordContains(condition.getKeyword()))
				.and(CourseSpecification.categoryEquals(condition.getCategoryId()))
				.and(CourseSpecification.courseStatusEquals(selectedStatus));

		Pageable pageable = PageRequest.of(pageIndex, coursePageSize, Sort.by(Sort.Direction.DESC, "courseId"));

		return repository.findAll(spec, pageable);
	}

	// psych_function
	public Page<Course> getCoursesByPsychId(Integer psychId, Integer page, String orderBy) {

		if (psychId == null) {
			throw new IllegalArgumentException("psychId 不可為 null");
		}

		if (page == null || page < 0) {
			page = 0;
		}

		Pageable pageable = PageRequest.of(page, coursePageSize, CourseSortUtil.getCourseSort(orderBy));

		return repository.findByPsychologistPsychId(psychId, pageable);
	}

	// admin_function
	public void delistCourse(Integer courseId, DelistReason delistReason) {

		Course course = repository.findById(courseId)
				.orElseThrow(() -> new IllegalArgumentException("找不到課程，課程編號：" + courseId));
		course.setDelistReason(delistReason);
		course.setDelistedAt(LocalDateTime.now());
		course.setCourseStatus((byte) 5);
		repository.save(course);
	}

	public int countCoursesByStatus(Byte courseStatus) {
		if (courseStatus == null) {
			throw new IllegalArgumentException("課程狀態不能為空");
		}

		return repository.countByCourseStatus(courseStatus);
	}

	public Page<Course> searchListedCourses(String keyword, Integer page, String orderBy) {
		if (page == null || page < 0) {
			page = 0;
		}

		if (keyword == null) {
			keyword = "";
		}

		keyword = keyword.trim();

		Pageable pageable = PageRequest.of(page, coursePageSize, CourseSortUtil.getCourseSort(orderBy));

		return repository.searchByCourseOrPsychologist(keyword, COURSE_STATUS_LISTED, pageable);
	}

	// member_function
	public void checkAllCourseStatus() {
		List<Course> allCourse = getAllCourse();
		for (Course course : allCourse) {
			if (course.getCourseStatus() == 2) {
				course.setCourseStatus((byte) 4);
				course.setListedAt(LocalDateTime.now());
				repository.save(course);
			}
		}
	}

	public Page<Course> findCoursesByMinimumCounts(Byte courseStatus, Integer minSaveCount, Integer minStarCount,
			Integer minReviewCount, Integer minCommentCount, int page, String orderBy) {
		if (page < 0) {
			page = 0;
		}
		Pageable pageable = PageRequest.of(page, coursePageSize, CourseSortUtil.getCourseSort(orderBy));
		Specification<Course> specification = CourseSpecification.searchByCounts(courseStatus, minSaveCount,
				minStarCount, minReviewCount, minCommentCount);
		return repository.findAll(specification, pageable);
	}

	public Page<Course> findListedCoursesByCategory(Integer courseCatId, int page, String orderBy) {

		if (page < 0) {
			page = 0;
		}

		Pageable pageable = PageRequest.of(page, coursePageSize, CourseSortUtil.getCourseSort(orderBy));

		return repository.findByCourseStatusAndCourseCategories_CourseCatId(COURSE_STATUS_LISTED, courseCatId,
				pageable);
	}

	// 收藏課程
	public void addCourseBookmark(Integer memberId, Integer courseId) {
		String key = "bookmark:member:" + memberId;
		stringRedisTemplate.opsForSet().add(key, String.valueOf(courseId));
	}

	public void removeCourseBookmark(Integer memberId, Integer courseId) {
		String key = "bookmark:member:" + memberId;
		stringRedisTemplate.opsForSet().remove(key, String.valueOf(courseId));
	}

	public boolean isCourseInBookmark(Integer memberId, Integer courseId) {
		String key = "bookmark:member:" + memberId;
		Boolean result = stringRedisTemplate.opsForSet().isMember(key, String.valueOf(courseId));
		return Boolean.TRUE.equals(result);
	}

	public Page<Course> getBookmarkCourses(Integer memberId, Integer page) {

		if (page == null || page < 1) {
			page = 0;
		}
		String key = "bookmark:member:" + memberId;

		Set<String> courseIdSet = stringRedisTemplate.opsForSet().members(key);
		if (courseIdSet == null || courseIdSet.isEmpty()) {
			return Page.empty();
		}
		List<Integer> courseIds = courseIdSet.stream().map(Integer::valueOf).toList();
		Pageable pageable = PageRequest.of(page, coursePageSize, Sort.by("courseId").descending());
		return repository.findByCourseIdIn(courseIds, pageable);
	}

	public Page<Course> getBookmarkCourses(Integer memberId, Integer page, String keyword) {

		// 前端頁碼從 1 開始，PageRequest 從 0 開始
		if (page == null || page < 1) {
			page = 1;
		}

		if (keyword == null) {
			keyword = "";
		}

		keyword = keyword.trim();

		String key = "bookmark:member:" + memberId;

		Set<String> courseIdSet = stringRedisTemplate.opsForSet().members(key);

		if (courseIdSet == null || courseIdSet.isEmpty()) {
			return Page.empty();
		}

		List<Integer> courseIds = courseIdSet.stream().map(Integer::valueOf).toList();

		Pageable pageable = PageRequest.of(page, coursePageSize, Sort.by("courseId").descending());

		return repository.searchBookmarkCourses(courseIds, keyword, pageable);
	}

}
