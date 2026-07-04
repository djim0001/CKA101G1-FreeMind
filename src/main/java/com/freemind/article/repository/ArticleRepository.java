package com.freemind.article.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.freemind.article.entity.Article;

public interface ArticleRepository extends JpaRepository<Article, Integer>{

	@Query("SELECT a FROM Article a WHERE a.psychologist.psychId = :psychId")
	List<Article> findArticlesByPsychId(@Param("psychId") Integer psychId);
	
	List<Article> findByArticleStatus(Integer articleStatus);

	@Query("SELECT a FROM Article a WHERE a.articleStatus = :status")
	Page<Article> findByStatus(@Param("status")Integer articleStatus, Pageable pageable);
	
	@Query("SELECT a FROM Article a WHERE a.articleStatus = :status AND a.articleCat.articleCatId = :catId")
	Page<Article> findByStatusAndCatId(@Param("status")Integer articleStatus, 
									   @Param("catId")Integer catId, Pageable pageable);

}
