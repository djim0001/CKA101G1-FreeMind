package com.freemind.article.dto;

import java.util.List;

import com.freemind.article.entity.Article;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ArticleRecommendDTO {
	private List<Article> articleList;
}
