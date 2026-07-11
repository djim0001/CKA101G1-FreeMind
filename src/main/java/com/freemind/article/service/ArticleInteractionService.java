package com.freemind.article.service;


import org.springframework.data.domain.Page;

import com.freemind.article.entity.Article;

public interface ArticleInteractionService {
	
	void toggleLike(Integer articleId, Integer memberId);
	
	void toggleBookmark(Integer articleId, Integer memberId);
	
	long getLikeCount(Integer articleId);
	
	long getBookmarkCount(Integer articleId);
	
	boolean isLikedByMember(Integer articleId, Integer memberId);
	
	boolean isSavedByMember(Integer articleId, Integer memberId);

	Page<Article> getLikedArticles(Integer memberId, Integer page);
	
	Page<Article> getSavedArticles(Integer memberId, Integer page);

	Page<Article> getViewHistory(Integer memberId, Integer page);

	
}
