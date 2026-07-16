package com.freemind.article.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.freemind.article.dto.ArticleCreateForm;
import com.freemind.article.entity.Article;

public interface ArticleService {
	
	Article createDraft(ArticleCreateForm form, Integer psychId);
	
	Article createAndSubmit(ArticleCreateForm form, Integer psychId);
	
	Article submitExistingDraft(Integer articleId, Integer psychId);
	
	Page<Article> getMyArticles(Integer psychId, Integer page);
	
	Page<Article> getPublishedArticles(Integer catId, String keyword, Integer page);
	
//	Page<Article> searchPublishedArticles(String keyword, Integer page);
	
	Page<Article> getSubmittedArticles(Integer page);
	
	Page<Article> getPendingArticles(Integer page);
	
	Article getArticle(Integer articleId, Integer psychId);

	Article getArticleForAdmin(Integer articleId);
	
	Article getPublishedArticle(Integer articleId);
	
	List<Article> getPublishedArticlesByIds(List<Integer> articleIds);
	
	Article getEditableArticle(Integer articleId, Integer pysch);
	
	Article createEditCopy(Integer articleId, Integer pysch);
	
	Article updateDraft(Integer articleId, ArticleCreateForm form, Integer psychId);
	
	Article updateAndSubmit(Integer articleId, ArticleCreateForm form, Integer psychId);
	
	void deleteDraft(Integer articleId, Integer psychId);
	
	void unPublishMyArticle(Integer articleId, Integer psychId);
	
	Article getArticleForReview(Integer articleId);
	
	void approveArticle(Integer articleId, Integer adminId);
	
	void rejectArticle(Integer articleId, Integer adminId, Integer rejectReason, String rejectNote);

	void unPublishArticle(Integer articleId, Integer adminId);

	Page<Article> getReviewedArticles(Integer status, Integer page);

	long incrementAndGetShareCount(Integer articleId);
	
}
