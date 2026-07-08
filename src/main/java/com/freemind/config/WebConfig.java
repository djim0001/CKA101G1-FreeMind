package com.freemind.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	
	@Value("${course.video.upload.dir}")
	private String videoUploadDir;

	@Value("${course.video.url-path}")
	private String videoUrlPath;
	
	@Value("${article.upload.dir}")
	private String articleUploadDir;
	
	@Value("${article.upload.url-path}")
    private String articleUrlPath;
   
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler(videoUrlPath).addResourceLocations("file:" + videoUploadDir);
		registry.addResourceHandler(articleUrlPath).addResourceLocations("file:" + articleUploadDir);
	}
	
}
