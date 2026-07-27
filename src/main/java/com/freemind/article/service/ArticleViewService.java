package com.freemind.article.service;

import java.util.List;

public interface ArticleViewService {
	
	boolean recordViewCount(Integer articleId, String visitorKey);
	
	void syncCountAndScoreToDb();
	
	List<Integer> getHotArticleIds(int topN);
	
	void adjustHotScore(Integer articleId, double delta);
	
}
