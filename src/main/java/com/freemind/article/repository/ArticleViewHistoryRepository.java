package com.freemind.article.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.freemind.article.entity.ArticleViewHistory;
import com.freemind.article.entity.ArticleViewHistoryId;

public interface ArticleViewHistoryRepository extends JpaRepository<ArticleViewHistory, ArticleViewHistoryId>{

	@Query("SELECT a FROM ArticleViewHistory a WHERE a.member.memberId = :memberId")
	Page<ArticleViewHistory> findByMemberId(@Param("memberId")Integer memberId, Pageable pageable);

	@Query("SELECT a FROM ArticleViewHistory a WHERE a.member.memberId = :memberId AND a.article.articleId = :articleId")
	Optional<ArticleViewHistory> findByMemberIdAndArticleId(@Param("memberId")Integer memberId, @Param("articleId")Integer articleId);
}
