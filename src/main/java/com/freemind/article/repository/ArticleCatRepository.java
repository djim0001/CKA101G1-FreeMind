package com.freemind.article.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.freemind.article.entity.ArticleCat;

public interface ArticleCatRepository extends JpaRepository<ArticleCat, Integer>{

	List<ArticleCat> findByArticleCatStatusTrue();
	
	Page<ArticleCat> findByArticleCatId(Integer catId, Pageable pageable);

	@Query("SELECT COUNT(c) > 0 FROM ArticleCat c WHERE LOWER(TRIM(c.articleCatName)) = LOWER(TRIM(:catName))")
	boolean existsCatName(@Param("catName") String catName);

	@Query("SELECT COUNT(c) > 0 FROM ArticleCat c WHERE LOWER(TRIM(c.articleCatName)) = LOWER(TRIM(:catName)) AND c.articleCatId <> :catId")
	boolean existsCatNameExcludingId(@Param("catName") String catName, @Param("catId") Integer catId);

	@Query("SELECT c FROM ArticleCat c WHERE LOWER(TRIM(c.articleCatName)) LIKE LOWER(CONCAT('%', TRIM(:keyword), '%'))")
	List<ArticleCat> findCatsByName(@Param("keyword") String keyword);

}
