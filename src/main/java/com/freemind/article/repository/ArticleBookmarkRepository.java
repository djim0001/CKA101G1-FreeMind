package com.freemind.article.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.freemind.article.entity.ArticleBookmark;
import com.freemind.article.entity.ArticleBookmarkId;

public interface ArticleBookmarkRepository extends JpaRepository<ArticleBookmark, ArticleBookmarkId> {

	@Query("SELECT COUNT(a) FROM ArticleBookmark a WHERE a.article.articleId = :articleId")
	long countByArticleId(@Param("articleId") Integer articleId);

	@Query("SELECT a FROM ArticleBookmark a WHERE a.member.memberId = :memberId")
	Page<ArticleBookmark> findByMemberId(@Param("memberId")Integer memberId, Pageable pageable);
}
