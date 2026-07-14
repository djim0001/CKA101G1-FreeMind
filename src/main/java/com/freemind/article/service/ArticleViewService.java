package com.freemind.article.service;

public interface ArticleViewService {
	
	boolean recordViewCount(Integer articleId, String visitorKey);
	
	void syncViewCountsToDb();

}
