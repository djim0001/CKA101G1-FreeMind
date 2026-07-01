package com.freemind.course.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
	@Value("${course.video.upload-path}")
	private String videoUploadPath;

	@Value("${course.video.url-path}")
	private String videoUrlPath;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler(videoUrlPath).addResourceLocations("file:" + videoUploadPath);
	}

}
