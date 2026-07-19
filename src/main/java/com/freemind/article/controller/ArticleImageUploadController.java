package com.freemind.article.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.freemind.article.entity.Article;
import com.freemind.article.service.ArticleService;
import com.freemind.login.security.adminsecurity.AdminUserDetails;
import com.freemind.login.security.psychologistsecurity.PsychUserDetails;

@RestController
@RequestMapping("/article")
public class ArticleImageUploadController {

	@Autowired
	private ArticleService articleService;
	
	@GetMapping("/{articleId}/cover")
	public ResponseEntity<byte[]> getCoverImage(@PathVariable Integer articleId,
	        									Authentication authentication) {
	    Article article;
	    if (authentication != null && authentication.getPrincipal() instanceof PsychUserDetails psychUser) {
	        Integer psychId = psychUser.getPsychologist().getPsychId();
	        article = articleService.getArticle(articleId, psychId);
	    } else if (authentication != null && authentication.getPrincipal() instanceof AdminUserDetails) {
	    	article = articleService.getArticleForAdmin(articleId);
		} else {
			article = articleService.getArticle(articleId, null);
	    }
	    
	    // 根據檔案開頭的位元組(Hex Signature)判斷圖片類型
	    byte[] image = article.getCoverImage();
	    MediaType mediaType = MediaType.IMAGE_JPEG; // 預設 JPEG

	    if (image.length > 8 && image[0] == (byte) 0x89 && image[1] == (byte) 0x50) {
	        mediaType = MediaType.IMAGE_PNG;
	    } else if (image.length > 4 && image[0] == (byte) 0x47 && image[1] == (byte) 0x49) {
	        mediaType = MediaType.IMAGE_GIF;
	    }

	    return ResponseEntity.ok()
	            .contentType(mediaType)
	            .body(image);
	}

}