package com.freemind.article.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.freemind.article.dto.ArticleCreateForm;
import com.freemind.article.entity.Article;
import com.freemind.article.entity.ArticleCat;
import com.freemind.article.service.ArticleCatService;
import com.freemind.article.service.ArticleService;

import jakarta.servlet.http.HttpSession;

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
	
	@ExceptionHandler(value = {IllegalStateException.class, IllegalArgumentException.class}) // Service 層的表單驗證錯誤
	public String handleError(Exception e, Model model) {
		model.addAttribute("errorMessage", e.getMessage());
		model.addAttribute("form", new ArticleCreateForm());
		return "front-end/article/createForm";
	}
	
	// testing
	@PostMapping("/set_psychId_session")
	public String setPsychIdSession(@RequestParam("psychIdSession") Integer psychIdSession, 
									HttpSession session) {
		session.setAttribute("psychId", psychIdSession);
		return "redirect:/article/myArticles";
	}
	
	@GetMapping("/create")
	public String createArticlePage(Model model,
			@SessionAttribute(name = "psychId", required = false) Integer psychId) {
		
		if (psychId == null) {
	        model.addAttribute("errorMessage", "請先登入心理師帳號");
	     // testing
	        return "front-end/article/test-login";
	    }
		
		model.addAttribute("form", new ArticleCreateForm());
		return "front-end/article/createForm";
	}
	
	@GetMapping("/myArticles")
	public String myArticles(Model model, 
			@SessionAttribute(name = "psychId", required = false) Integer psychId) {
		
		if (psychId == null) {
			model.addAttribute("errorMessage", "請先登入心理師帳號");
			// testing
			return "front-end/article/test-login";
		}
		
		List<Article> articles = articleService.getMyArticles(psychId);
		model.addAttribute("articles", articles);
		return "front-end/article/myArticles";
	}
	
	@PostMapping("/create")
	public String createArticle(Model model, 
			@ModelAttribute ArticleCreateForm form, 
			@RequestParam("action") String action,
			@SessionAttribute(name = "psychId", required = false) Integer psychId) {
		
		if (psychId == null) {
			model.addAttribute("errorMessage", "請先登入心理師帳號");
			// testing
			return "front-end/article/test-login";
		}
		
		if ("submit".equals(action)) {
			articleService.createAndSubmit(form, psychId);
		} else {
			articleService.createDraft(form, psychId);
		}
		
		return "redirect:/article/myArticles";
	}
	
	@PostMapping("/{articleId}/submit")
	public String submitDraft(Model model, 
			@PathVariable Integer articleId, 
			@SessionAttribute(name = "psychId", required = false) Integer psychId) {
		
		if (psychId == null) {
			model.addAttribute("errorMessage", "請先登入心理師帳號");
			// testing
			return "front-end/article/test-login";
		}
		
		articleService.submitExistingDraft(articleId, psychId);
		
		return "redirect:/article/myArticles";
	}
	
}
