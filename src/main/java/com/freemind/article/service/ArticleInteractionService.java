package com.freemind.article.service;

public interface ArticleInteractionService {
	
	void toggleLike(Integer articleId, Integer memberId);
	
	long getLikeCount(Integer articleId);
	
	boolean isLikedByMember(Integer articleId, Integer memberId);
}
