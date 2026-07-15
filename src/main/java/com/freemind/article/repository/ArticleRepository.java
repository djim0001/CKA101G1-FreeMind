package com.freemind.article.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.freemind.article.entity.Article;
import com.freemind.article.entity.ArticleCat;

public interface ArticleRepository extends JpaRepository<Article, Integer>{

	@Query("SELECT a FROM Article a WHERE a.psychologist.psychId = :psychId")
	Page<Article> findArticlesByPsychId(@Param("psychId") Integer psychId, Pageable pageable);
	
	@Query("SELECT a FROM Article a WHERE a.articleStatus = :status")
	List<Article> findByStatus(@Param("status")Integer articleStatus);
	
	@Query("SELECT a FROM Article a WHERE a.articleStatus = :status")
	Page<Article> findByStatus(@Param("status")Integer articleStatus, Pageable pageable);
	
	@Query("SELECT a FROM Article a WHERE a.articleStatus IN :statuses")
	Page<Article> findByStatuses(@Param("statuses")List<Integer> statuses, Pageable pageable);
	
	@Query("SELECT a FROM Article a WHERE a.articleStatus = :status AND a.articleCat.articleCatId = :catId")
	Page<Article> findByStatusAndCatId(@Param("status")Integer articleStatus, 
									   @Param("catId")Integer catId, Pageable pageable);
	
	@Query("SELECT a FROM Article a " +
		   "WHERE a.articleStatus = :status " +
		   "AND (a.title LIKE %:keyword% OR a.psychologist.name LIKE %:keyword%)")
	Page<Article> findByStatusAndTitleOrAuthor(@Param("status")Integer articleStatus, @Param("keyword")String keyword, Pageable pageable);

	@Query("SELECT a FROM Article a WHERE a.parentArticleId = :parentId AND a.articleStatus IN :statuses")
	Article findEditCopy(@Param("parentId")Integer parentArticleId, @Param("statuses")List<Integer> statuses);

	boolean existsByArticleCat(ArticleCat articleCat);

	@Modifying
	@Query("UPDATE Article a SET a.shareCount = a.shareCount + 1 WHERE a.articleId = :articleId")
	void incrementShareCount(@Param("articleId")Integer articleId);
	
	@Modifying
	@Query("UPDATE Article a SET a.viewCount = a.viewCount + :count WHERE a.articleId = :articleId")
	void incrementViewCount(@Param("articleId")Integer articleId, long count);

}
