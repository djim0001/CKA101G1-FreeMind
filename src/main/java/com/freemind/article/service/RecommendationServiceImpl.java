package com.freemind.article.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.freemind.article.entity.Article;
import com.freemind.article.entity.ArticleCat;
import com.freemind.article.entity.ArticleViewHistory;
import com.freemind.article.repository.ArticleRepository;
import com.freemind.article.repository.ArticleViewHistoryRepository;
import com.freemind.course.course.model.Course;
import com.freemind.course.course.model.CourseService;
import com.freemind.login.member.model.Member;

import jakarta.transaction.Transactional;

@Service
public class RecommendationServiceImpl implements RecommendationService{
	
	@Autowired
	private ArticleRepository articleRepository;
	
	@Autowired
	private ArticleViewHistoryRepository articleViewHistoryRepository;
	
	@Autowired
	private CourseService courseService;
	
	@Override
	@Transactional
	public List<Article> getArticleRecommendation(Integer articleId) {
		List<ArticleCat> catList = articleRepository.getPopularCats(6);
		Collections.shuffle(catList);
		
		List<ArticleCat> random3 = new ArrayList<>();
		random3.add(catList.get(0));
		random3.add(catList.get(1));
		random3.add(catList.get(2));
		
	    List<Article> articles = articleRepository.getPopularFromCats(random3, List.of(articleId));

		return articles;
	}

	@Override
	@Transactional
	public List<Article> getArticleRecommendation(Member member, Integer articleId) {
		List<ArticleViewHistory> articleViewHistoryList = articleViewHistoryRepository.findByMemberId(member.getMemberId());
		Map<ArticleCat, Integer> articleCatCounts = new HashMap<>();
		List<Integer> viewedArticleIds = new ArrayList<>();
		
		for (ArticleViewHistory articleViewHistory : articleViewHistoryList) {
			ArticleCat articleCat = articleViewHistory.getArticle().getArticleCat();
			Integer viewedArticleId = articleViewHistory.getArticle().getArticleId();
			articleCatCounts.put(articleCat, articleCatCounts.getOrDefault(articleCat, 0) + 1);
			viewedArticleIds.add(viewedArticleId);
			
		}
		
		List<Map.Entry<ArticleCat, Integer>> top3 = articleCatCounts.entrySet()
														             .stream()
														             .sorted(Map.Entry.<ArticleCat, Integer>comparingByValue().reversed())
														             .limit(3)
														             .collect(Collectors.toList());
		
		List<ArticleCat> catList = top3.stream().map(c -> c.getKey()).collect(Collectors.toList());
		List<Article> articles = articleRepository.getPopularFromCats(catList, viewedArticleIds);

		return articles;
	}
	
	@Override
	public List<Course> getCourseRecommendation(Integer articleId) {
		Article article = articleRepository.findById(articleId)
				                           .orElseThrow(() -> new IllegalArgumentException("查無此文章"));
		Set<Course> courses = article.getPsychologist().getCourses();
		List<Course> courselist = new ArrayList<>(courses);
		
		if (!courses.isEmpty()) {
			Collections.shuffle(courselist);
		}
		
		if (courselist.size() < 3) {
			List<Course> courseListByCat = courseService.getPopularCoursesByCat(article.getArticleCat().getArticleCatId());
			courselist = Stream.concat(courselist.stream(), courseListByCat.stream())
																	        .limit(3)
																	        .collect(Collectors.toList());
		}
		
		return courselist.stream().limit(3).collect(Collectors.toList());
	}
	
}
