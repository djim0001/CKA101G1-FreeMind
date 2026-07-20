package com.freemind.article.dto;

import java.time.LocalDateTime;

import com.freemind.article.entity.Article;

import lombok.Getter;

@Getter
public class ArticleRecommendDTO {
	private Integer articleId;
	private boolean hasCover;
	private String articleCat;
	private String title;
	private String psych;
	private String content;
	private LocalDateTime publishedAt;

	public ArticleRecommendDTO(Article article) {
		this.articleId = article.getArticleId();
		this.hasCover = article.getCoverImage() != null && article.getCoverImage().length > 0;
		this.articleCat = article.getArticleCat().getArticleCatName();
		this.title = article.getTitle();
		this.psych = article.getPsychologist().getName();
		this.content = article.getContent();
		this.publishedAt = article.getPublishedAt();
	}

}

