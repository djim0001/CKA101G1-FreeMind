package com.freemind.article.controller;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import com.freemind.article.entity.Article;
import com.freemind.article.service.ArticleService;

@RestController
@RequestMapping("/article")
public class ArticleImageUploadController {
	@Value("${article.upload.dir}")
	private String uploadDir;

	@Value("${article.upload.url-path}")
	private String urlPath;

	@Autowired
	private ArticleService articleService;
	
	@GetMapping("/{articleId}/cover")
	public ResponseEntity<byte[]> getCoverImage(@PathVariable Integer articleId,
			@SessionAttribute(name = "psychId", required = false) Integer psychId) {
		Article article = articleService.getArticle(articleId, psychId);
	    
		if (article == null || article.getCoverImage() == null) {
	        return ResponseEntity.notFound().build();
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

	// saving <img src=""> to the backend from TinyMCE
	@PostMapping("/uploadImage") 
	public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
		Map<String, String> jsonResult = new HashMap<>();

		if (file == null || file.isEmpty()) {
			jsonResult.put("error", "未選擇檔案");
			return ResponseEntity.badRequest().body(jsonResult); // 400
		}

		try {
			File dir = new File(uploadDir);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			String originFileName = file.getOriginalFilename();
			String ext = "";

			if (originFileName != null && originFileName.contains(".")) {
				ext = originFileName.substring(originFileName.lastIndexOf("."));
			}

			String newFileName = UUID.randomUUID().toString() + ext;
			File dest = new File(dir, newFileName);
			file.transferTo(dest.toPath());

			String basePath = urlPath.replace("**", "");
			jsonResult.put("location", basePath + newFileName);
			return ResponseEntity.ok(jsonResult); // 200

		} catch (Exception e) {
			jsonResult.put("error", "上傳失敗: " + e.getMessage());
			return ResponseEntity.internalServerError().body(jsonResult); // 500
		}

	}

}