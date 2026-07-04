package com.freemind.article.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.freemind.article.service.ArticleCatService;
import com.freemind.article.service.ArticleService;

@Controller
@RequestMapping("/member/article")  
public class MemberArticleController {
	
	@Autowired
	private ArticleService articleService;
	
	@Autowired
	private ArticleCatService articleCatService;

}
