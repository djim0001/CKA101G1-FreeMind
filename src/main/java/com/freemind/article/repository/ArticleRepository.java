package com.freemind.article.repository;

import java.time.LocalDateTime;
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
	List<Article> findArticlesByPsychId(Integer psychId);

	@Query("SELECT a FROM Article a WHERE a.psychologist.psychId = :psychId " +
		   "AND (a.articleCat.articleCatId = :catId OR :catId IS NULL) " +
		   "AND (a.articleStatus = :status OR :status IS NULL) ")
	Page<Article> findMyArticlesWithFilters(@Param("psychId")Integer psychId, 
			                                @Param("catId")Integer catId, 
											@Param("status")Integer status, 
											Pageable pageable);
	
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
	
	// 合併成多條件查詢
	@Query("SELECT a FROM Article a WHERE a.articleStatus = :status " +
	       "AND (:excludeId IS NULL OR a.articleId != :excludeId) " +
	       "AND (:catId IS NULL OR a.articleCat.articleCatId = :catId) " +
	       "AND (:keyword IS NULL OR a.title LIKE %:keyword% OR a.psychologist.name LIKE %:keyword%) " +
	       "AND (:dateFrom IS NULL OR a.publishedAt >= :dateFrom) " +
	       "AND (:dateTo IS NULL OR a.publishedAt < :dateTo)")
	Page<Article> searchArticles(@Param("status") Integer status,
	                             @Param("excludeId") Integer excludeId,
	                             @Param("catId") Integer catId,
	                             @Param("keyword") String keyword,
	                             @Param("dateFrom") LocalDateTime dateFrom,
	                             @Param("dateTo") LocalDateTime dateTo,
	                             Pageable pageable);

	@Query("SELECT a FROM Article a WHERE a.articleStatus IN :statuses " +
		       "AND (:catId IS NULL OR a.articleCat.articleCatId = :catId) " +
		       "AND (:keyword IS NULL OR a.title LIKE %:keyword% OR a.psychologist.name LIKE %:keyword%) " +
		       "AND (:status IS NULL OR a.articleStatus = :status)")
	Page<Article> findSubmittedArticlesWithFilters(@Param("statuses") List<Integer> statuses,
	                                      @Param("catId") Integer catId,
	                                      @Param("keyword") String keyword,
	                                      @Param("status") Integer status,
	                                      Pageable pageable);
	
	@Query("SELECT a FROM Article a WHERE a.parentArticleId = :parentId AND a.articleStatus IN :statuses")
	Article findEditCopy(@Param("parentId")Integer parentArticleId, @Param("statuses")List<Integer> statuses);

	boolean existsByArticleCat(ArticleCat articleCat);

	@Modifying
	@Query("UPDATE Article a SET a.shareCount = a.shareCount + 1 WHERE a.articleId = :articleId")
	void incrementShareCount(@Param("articleId")Integer articleId);
	
	@Modifying
	@Query("UPDATE Article a SET a.viewCount = a.viewCount + :count WHERE a.articleId = :articleId")
	void incrementViewCount(@Param("articleId")Integer articleId, long count);

	@Query("SELECT a FROM Article a WHERE a.articleCat IN :cats " +
	       "AND a.articleStatus = 2 AND a.articleId NOT IN :articleIds " +
		   "ORDER BY a.viewCount DESC LIMIT 3")
	List<Article> getPopularFromCats(@Param("cats")List<ArticleCat> cats, @Param("articleIds")List<Integer> viewedArticleIds);
	
	@Query("SELECT a.articleCat FROM Article a " +
	       "WHERE a.articleStatus = 2 AND a.articleCat.articleCatStatus = TRUE " +
		   "GROUP BY a.articleCat ORDER BY COUNT(a) DESC LIMIT :number")
	List<ArticleCat> getPopularCats(@Param("number") int number);

}
