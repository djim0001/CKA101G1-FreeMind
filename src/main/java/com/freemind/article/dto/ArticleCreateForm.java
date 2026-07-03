package com.freemind.article.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ArticleCreateForm {
	
	private Integer articleCatId;
	private String title;
	private String content;
	private MultipartFile coverImageFile;
	
}
