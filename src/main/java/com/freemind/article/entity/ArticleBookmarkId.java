package com.freemind.article.entity;

import java.io.Serializable;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Embeddable
public class ArticleBookmarkId implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private Integer articleId;
	private Integer memberId;

}
