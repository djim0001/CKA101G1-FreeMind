package com.freemind.article.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/article")
public class ArticleImageUploadController {

	@Value("${article.upload.dir}")
	private String uploadDir;
	
	@Value("${article.upload.url-path}")
	private String urlPath;
	
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
			
		} catch (Exception e){
			jsonResult.put("error", "上傳失敗: " + e.getMessage());
			return ResponseEntity.internalServerError().body(jsonResult); // 500
		}
		
	}
}
