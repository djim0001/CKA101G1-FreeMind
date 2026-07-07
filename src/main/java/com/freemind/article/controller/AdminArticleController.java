package com.freemind.article.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.article.entity.Article;
import com.freemind.article.service.ArticleCatService;
import com.freemind.article.service.ArticleService;

import jakarta.servlet.http.HttpSession;


@Controller
@RequestMapping("/admin/article")
public class AdminArticleController {

	@Autowired
	private ArticleService articleService;
	
	// testing
	@PostMapping("/set_adminId_session")
	public String setAdminIdSession(@RequestParam(name = "adminIdSession", required = false) Integer adminIdSession, HttpSession session) {
		session.setAttribute("adminId", adminIdSession);
		return "redirect:/admin/article";
	}
	
	@GetMapping({"", "/"})
    public String articleAdmin(Model model,
    		@SessionAttribute(name="adminId", required = false) Integer adminId) {
		
		// testing
		if (adminId == null) {
			model.addAttribute("errorMessage", "*請先登入");
			return "back-end/article/test-login";
		}
		
		return "back-end/article/articleAdmin";
    }
	
    @GetMapping("/pending")
    public String getPendingArticles(Model model, 
    		@RequestParam(name = "page", defaultValue = "1") Integer page,
    		@SessionAttribute(name="adminId", required = false) Integer adminId,
    		RedirectAttributes redirectAttributes) {
		
    	// testing
		if (adminId == null) {
			model.addAttribute("errorMessage", "*請先登入");
			return "back-end/article/test-login";
		}
    	
		Page<Article> articlePage = articleService.getPendingArticles(page);
        model.addAttribute("articlePage", articlePage);
        model.addAttribute("currentPage", page);
	    return "back-end/article/pendingList";

    }
    
    @GetMapping("/reviewed")
    public String getReviewedArticles(Model model,
    		@RequestParam(name = "status", required = false) Integer status,
    		@RequestParam(name = "page", defaultValue = "1") Integer page,
    		@SessionAttribute(name = "adminId", required = false) Integer adminId) {
		
    	// testing
		if (adminId == null) {
			model.addAttribute("errorMessage", "*請先登入");
			return "back-end/article/test-login";
		}
    	
		Page<Article> articlePage = articleService.getReviewedArticles(status, page);
		model.addAttribute("articlePage", articlePage);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("currentStatus", status);
    	return "back-end/article/reviewedList";
    }
    
    @GetMapping("/{articleId}/review")
    public String getReviewDetail(Model model, 
			@PathVariable Integer articleId,
			@SessionAttribute(name = "adminId", required = false) Integer adminId,
			RedirectAttributes redirectAttributes) {
    	
    	// testing
		if (adminId == null) {
			model.addAttribute("errorMessage", "*請先登入");
			return "back-end/article/test-login";
		}
		
		try {
			Article article = articleService.getArticleForReview(articleId);
			model.addAttribute("article", article);
		} catch (IllegalArgumentException | IllegalStateException e) {
			redirectAttributes.addFlashAttribute("alertMessage", e.getMessage());
			return "redirect:/admin/article/pending";
		}
		
    	return "back-end/article/reviewDetail";
    }
    
	@PostMapping("/{articleId}/review")
	public String reviewArticle(Model model, 
			@PathVariable Integer articleId,
			@RequestParam("action") String action,
			@RequestParam(value = "rejectReason", required = false) Integer rejectReason,
			@RequestParam(value = "rejectNote", required = false) String rejectNote,
			@SessionAttribute(name = "adminId", required = false) Integer adminId,
			RedirectAttributes redirectAttributes) {
		
		// testing
		if (adminId == null) {
			model.addAttribute("errorMessage", "*請先登入");
			return "back-end/article/test-login";
		}
		
		try {
			if ("approve".equals(action)) {
				articleService.approveArticle(articleId, adminId);
			} else if ("reject".equals(action)) {
				articleService.rejectArticle(articleId, adminId, rejectReason, rejectNote);
			}
		} catch (IllegalArgumentException | IllegalStateException e) {
			redirectAttributes.addFlashAttribute("alertMessage", e.getMessage());
		}
		
		return "redirect:/admin/article/pending";
	}
	
	@PostMapping("/{articleId}/unpublish")
	public String unpublishArticle(Model model,
			@PathVariable(name = "articleId") Integer articleId,
			@SessionAttribute(name = "adminId", required = false) Integer adminId,
			RedirectAttributes redirectAttributes) {
		
		// testing
		if (adminId == null) {
			model.addAttribute("errorMessage", "*請先登入");
			return "back-end/article/test-login";
		}
		
		try {
			articleService.unPublishArticle(articleId, adminId);
		} catch (IllegalArgumentException | IllegalStateException e) {
	        redirectAttributes.addFlashAttribute("alertMessage", e.getMessage());
		}
		
		return "redirect:/admin/article/reviewed";
	}
	
}
