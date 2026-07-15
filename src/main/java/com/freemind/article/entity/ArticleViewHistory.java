package com.freemind.article.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.freemind.login.member.model.Member;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "article_view_histories")
public class ArticleViewHistory implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private ArticleViewHistoryId viewHistoryId;
	
	@MapsId("articleId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "article_id")
	private Article article;
	
	@MapsId("memberId")
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id")
	private Member member;
	
	@Column(name = "viewed_at", nullable = false)
	private LocalDateTime viewedAt;
	
	public ArticleViewHistory(Article article, Member member, LocalDateTime viewedAt) {
		this.article = article;
		this.member = member;
		this.viewedAt = viewedAt;
		this.viewHistoryId = new ArticleViewHistoryId();
	}

	public void setViewedAt(LocalDateTime viewedAt) {
		this.viewedAt = viewedAt;
	}
	
}
