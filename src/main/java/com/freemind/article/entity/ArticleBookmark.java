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
@Table(name = "article_bookmarks")
public class ArticleBookmark implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private ArticleBookmarkId bookmarkId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("articleId")
	@JoinColumn(name = "article_id")
	private Article article;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("memberId")
	@JoinColumn(name = "member_id")
	private Member member;
	
	@Column(name = "saved_at", nullable = false)
	private LocalDateTime savedAt;
	
	public ArticleBookmark(Article article, Member member, LocalDateTime savedAt) {
		this.article = article;
		this.member = member;
		this.savedAt = savedAt;
	}
	
	public void setSavedAt(LocalDateTime savedAt) {
		this.savedAt = savedAt;
	}
	
}
