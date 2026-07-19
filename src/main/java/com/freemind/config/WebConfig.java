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

	
	@Value("${activity.upload.dir}")
	private String activityUploadDir;

	@Value("${activity.upload.url-path}")
	private String activityUrlPath;


	@Value("${psych.upload.dir}")
	private String psychUploadDir;

	@Value("${psych.upload.url-path}")
	private String psychUrlPath;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	    registry.addResourceHandler(videoUrlPath).addResourceLocations("file:" + videoUploadDir);
	    registry.addResourceHandler(articleUrlPath).addResourceLocations("file:" + articleUploadDir);
	    registry.addResourceHandler(psychUrlPath).addResourceLocations("file:" + psychUploadDir);
	    registry.addResourceHandler(activityUrlPath).addResourceLocations("file:" + activityUploadDir);
	}
   
	
}
