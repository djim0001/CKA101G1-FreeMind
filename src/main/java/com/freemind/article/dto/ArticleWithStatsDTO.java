package com.freemind.article.dto;

import com.freemind.article.entity.Article;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ArticleWithStatsDTO {
	private Article article;
	private ArticleInteractionStatsDTO articleStatistics;
}
