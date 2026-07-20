package com.freemind.article.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.freemind.article.entity.Article;

import lombok.Getter;

@Getter
public class ArticleListRecommendDTO {
	private List<ArticleRecommendDTO> articleList;

	public ArticleListRecommendDTO(List<Article> articleList) {
		this.articleList = articleList.stream()
				                      .map(article -> new ArticleRecommendDTO(article))
				                      .collect(Collectors.toList());
	}

}
