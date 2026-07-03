package com.freemind.article.service;

import java.util.List;

import com.freemind.article.dto.ArticleCreateForm;
import com.freemind.article.entity.Article;

public interface ArticleService {
	
	Article createDraft(ArticleCreateForm form, Integer psychId);
	
	Article createAndSubmit(ArticleCreateForm form, Integer psychId);
	
	Article submitExistingDraft(Integer articleId, Integer psychId);
	
	List<Article> getMyArticles(Integer psychId);
	
}
