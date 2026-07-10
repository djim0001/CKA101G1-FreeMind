package com.freemind.article.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.freemind.article.entity.ArticleBookmark;
import com.freemind.article.entity.ArticleBookmarkId;

public interface ArticleBookmarkRepository extends JpaRepository<ArticleBookmark, ArticleBookmarkId> {

	
//	long countByArticleId(Integer articleId);
}
