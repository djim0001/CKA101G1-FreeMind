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
@Table(name = "article_likes")
public class ArticleLike implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private ArticleLikeId likeId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("articleId")
	@JoinColumn(name = "article_id")
	private Article article;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("memberId")
	@JoinColumn(name = "member_id")
	private Member member;
	
	@Column(name = "liked_at", nullable = false)
	private LocalDateTime likedAt;
	
	public ArticleLike(Article article, Member member, LocalDateTime likedAt) {
		this.article = article;
		this.member = member;
		this.likedAt = likedAt;
		this.likeId = new ArticleLikeId();   // 讓 @MapsId 有空物件可填
	}
	
	public void setLikedAt(LocalDateTime likedAt) {
		this.likedAt = likedAt;
	}
	
}
