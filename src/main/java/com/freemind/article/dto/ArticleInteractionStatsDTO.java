package com.freemind.article.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
public class ArticleInteractionStatsDTO {
	private Integer articleId;
	private long viewCount;
	private long likeCount;
	private long bookmarkCount;
	private long shareCount;
}
