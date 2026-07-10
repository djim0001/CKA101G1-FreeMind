package com.freemind.article.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.freemind.article.entity.ArticleLike;
import com.freemind.article.entity.ArticleLikeId;

public interface ArticleLikeRepository extends JpaRepository<ArticleLike, ArticleLikeId> {

	@Query("SELECT COUNT(a) FROM ArticleLike a WHERE a.article.articleId = :articleId")
	long countByArticleId(@Param("articleId") Integer articleId);
}
