package com.freemind.article.dto;

import com.freemind.article.entity.Article;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ArticleWithStatsDTO {
	private Article article;
	private long viewCount;
	private long likeCount;
	private long bookmarkCount;
	private long shareCount; 
}
