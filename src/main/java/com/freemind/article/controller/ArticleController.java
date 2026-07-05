package com.freemind.article.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.freemind.article.entity.Article;
import com.freemind.article.entity.ArticleCat;
import com.freemind.article.service.ArticleCatService;
import com.freemind.article.service.ArticleService;

@Controller
@RequestMapping("/article")
public class ArticleController {
	
	@Autowired
	private ArticleService articleService;
	
	@Autowired
	private ArticleCatService articleCatService;
	
	@ModelAttribute("articleCats")
	public List<ArticleCat> articleCatList() {
		return articleCatService.getAllCats();
	}

	@GetMapping("/list")
	public String listPublishedArticles(Model model, 
										@RequestParam(name = "page", defaultValue = "1") Integer page, 
										@RequestParam(name = "catId", required = false) Integer catId) {
		Page<Article> articlePage = articleService.getPublishedArticles(page, catId);
		model.addAttribute("articlePage", articlePage);
		model.addAttribute("currentPage", page);
		model.addAttribute("articleCats", articleCatService.getAllCats());
		model.addAttribute("selectedCatId", catId);
		return "front-end/member/article/articleList";
	}
	
	@GetMapping("/detail/{articleId}")
	public String getArticleDetail(@PathVariable Integer articleId, Model model) {
		Article article = articleService.getPublishedArticle(articleId);
		
		if (article == null) {
			model.addAttribute("errorMessage", "查無此文章");
			return "front-end/member/article/articleList";
		}
		
		model.addAttribute("article", article);
		return "front-end/member/article/articleDetail";
	}
}
