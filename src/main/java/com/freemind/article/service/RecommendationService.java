package com.freemind.article.service;

import java.util.List;

import com.freemind.article.entity.Article;
import com.freemind.course.course.model.Course;
import com.freemind.login.member.model.Member;

public interface RecommendationService {
	
	List<Article> getArticleRecommendation(Integer articleId);
	
	List<Article> getArticleRecommendation(Member member, Integer articleId);
	
	List<Course> getCourseRecommendation(Member member);
}
