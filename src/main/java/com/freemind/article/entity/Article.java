package com.freemind.article.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.freemind.login.admin.model.Admin;
import com.freemind.login.psychologist.entity.Psychologist;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Entity
@Table(name = "articles")
public class Article implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "article_id")
	private Integer articleId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "article_cat_id")
	private ArticleCat articleCat;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "psych_id", nullable = false)
	private Psychologist psychologist;
	
	@ManyToOne(fetch = FetchType.LAZY) 
	@JoinColumn(name = "admin_id")
	private Admin admin;

	@Lob
	@Column(name = "cover_image", columnDefinition = "LONGBLOB")
	private byte[] coverImage;
	
	@Column(name = "title", length = 50)
	private String title;
	
	@Lob
	@Column(name = "article_content", columnDefinition = "LONGTEXT")
	private String content;
	
	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;
	
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;

	@Column(name = "submitted_at")
	private LocalDateTime submittedAt;
	
	@Column(name = "reviewed_at")
	private LocalDateTime reviewedAt;
	
	@Column(name = "published_at")
	private LocalDateTime publishedAt;
	
	@Column(name = "unpublished_at")
	private LocalDateTime unpublishedAt;

	@Column(name = "article_status", nullable = false)
	private Integer articleStatus = 0;  

	@Column(name = "reject_reason")
	private Integer rejectReason;
	
	@Column(name = "reject_note", length = 200)
	private String rejectNote;
	
	@Column(name = "view_count", nullable = false)
	private Integer viewCount = 0;
	
	@Column(name = "like_count", nullable = false)
	private Integer likeCount = 0;
	
	@Column(name = "save_count", nullable = false)
	private Integer saveCount = 0;
	
	@Column(name = "share_count", nullable = false)
	private Integer shareCount = 0;
}
