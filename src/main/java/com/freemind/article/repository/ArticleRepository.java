package com.freemind.article.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.freemind.article.entity.Article;

public interface ArticleRepository extends JpaRepository<Article, Integer>{

	@Query("SELECT a FROM Article a WHERE a.psychologist.psychId = :psychId")
	List<Article> findArticlesByPsychId(@Param("psychId") Integer psychId);

}
