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
public class StatsSummaryDTO {
	private long totalPublishedCount;
	private long totalUnPublishedCount;
	private long totalViewCount;
	private long totalLikeCount;
	private long totalBookmarkCount;
	private long totalShareCount;
}
