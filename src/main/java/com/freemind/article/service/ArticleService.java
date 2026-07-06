package com.freemind.article.service;

import org.springframework.data.domain.Page;

import com.freemind.article.dto.ArticleCreateForm;
import com.freemind.article.entity.Article;

public interface ArticleService {
	
	Article createDraft(ArticleCreateForm form, Integer psychId);
	
	Article createAndSubmit(ArticleCreateForm form, Integer psychId);
	
	Article submitExistingDraft(Integer articleId, Integer psychId);
	
	Page<Article> getMyArticles(Integer psychId, Integer page);
	
	Page<Article> getPublishedArticles(Integer catId, Integer page);
	
	Article getArticle(Integer articleId, Integer psychId);

	Article getPublishedArticle(Integer articleId);
	
	Article getEditableArticle(Integer articleId, Integer pysch);
	
	Article updateDraft(Integer articleId, ArticleCreateForm form, Integer psychId);
	
	Article updateAndSubmit(Integer articleId, ArticleCreateForm form, Integer psychId);
	
}
