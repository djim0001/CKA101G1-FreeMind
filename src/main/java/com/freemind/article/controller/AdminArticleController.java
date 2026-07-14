package com.freemind.article.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.article.entity.Article;
import com.freemind.article.entity.ArticleCat;
import com.freemind.article.service.ArticleCatService;
import com.freemind.article.service.ArticleService;
import com.freemind.login.security.adminsecurity.AdminUserDetails;


@Controller
@RequestMapping("/admin/article")
public class AdminArticleController {

	@Autowired
	private ArticleService articleService;
	
	@Autowired
	private ArticleCatService articleCatService;
	
	@GetMapping
    public String articleAdmin(Model model,
    		@AuthenticationPrincipal AdminUserDetails prinUserDetails) {
		
		return "back-end/article/articleAdmin";
    }
	
    @GetMapping("/pending")
    public String getPendingArticles(Model model, 
    		@RequestParam(name = "page", defaultValue = "1") Integer page,
    		@AuthenticationPrincipal AdminUserDetails prinUserDetails) {
		
		Page<Article> articlePage = articleService.getPendingArticles(page);
        model.addAttribute("articlePage", articlePage);
        model.addAttribute("currentPage", page);
	    return "back-end/article/pendingList";

    }
    
    @GetMapping("/reviewed")
    public String getReviewedArticles(Model model,
    		@RequestParam(name = "status", required = false) Integer status,
    		@RequestParam(name = "page", defaultValue = "1") Integer page,
    		@AuthenticationPrincipal AdminUserDetails prinUserDetails) {
		
		Page<Article> articlePage = articleService.getReviewedArticles(status, page);
		model.addAttribute("articlePage", articlePage);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("currentStatus", status);
    	return "back-end/article/reviewedList";
    }
    
    @GetMapping("/{articleId}/review")
    public String getReviewDetail(Model model, 
			@PathVariable Integer articleId,
			@AuthenticationPrincipal AdminUserDetails prinUserDetails,
			RedirectAttributes redirectAttributes) {
    	
		try {
			Article article = articleService.getArticleForReview(articleId);
			model.addAttribute("article", article);
		} catch (IllegalArgumentException | IllegalStateException e) {
			redirectAttributes.addFlashAttribute("alertMessage", e.getMessage());
			return "redirect:/admin/article/pending";
		}
		
    	return "back-end/article/reviewDetail";
    }
    
    @GetMapping("/categories")
    public String getCatSearch(Model model,
    		@RequestParam(name = "catId", required = false) Integer catId,
    		@RequestParam(name = "page", defaultValue = "1") Integer page,
    		@AuthenticationPrincipal AdminUserDetails prinUserDetails) {
		
//    	Page<ArticleCat> articleCatPage = articleCatService.getAllCats(catId, page);
//    	model.addAttribute("articleCatPage", articleCatPage);
//    	model.addAttribute("currentPage", page);
		
		List<ArticleCat> cats = articleCatService.getAllCats();
		List<Map<String, Object>> catList = new ArrayList<>();
		
		for (ArticleCat cat : cats) {
			Map<String, Object> map = new HashMap<>();
			map.put("articleCatId", cat.getArticleCatId());
			map.put("articleCatName", cat.getArticleCatName());
			catList.add(map);
		}
		
		model.addAttribute("articleCats", catList);
    	return "back-end/article/articleCatSearch";
	}
    
    @GetMapping("/categories/create")
    public String getCreateForm(Model model,
    		@AuthenticationPrincipal AdminUserDetails prinUserDetails) {
		
    	return "back-end/article/createCatForm";
    }
    
    @GetMapping("/categories/{catId}")
    public String getCatDetail(Model model,
    		@PathVariable Integer catId,
    		@AuthenticationPrincipal AdminUserDetails prinUserDetails,
    		RedirectAttributes redirectAttributes) {
		
		try {
			ArticleCat articleCat = articleCatService.getCatById(catId);
			model.addAttribute("articleCat", articleCat);
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("alertMessage", e.getMessage());
	        return "redirect:/admin/article/categories";
		}
		
    	return "back-end/article/articleCatDetail";
    }
    
    @PostMapping("/categories/create")
    public String createCat(Model model,
    		@RequestParam(name = "catName") String catName,
    		@AuthenticationPrincipal AdminUserDetails prinUserDetails,
    		RedirectAttributes redirectAttributes) {
    	
		try {
			ArticleCat articleCat = articleCatService.createCat(catName);
			redirectAttributes.addFlashAttribute("alertMessage", "新增成功");
			redirectAttributes.addFlashAttribute("alertType", "success");
		    return "redirect:/admin/article/categories/" + articleCat.getArticleCatId();
		} catch (IllegalArgumentException e) {
	        redirectAttributes.addFlashAttribute("alertMessage", e.getMessage());
	        redirectAttributes.addFlashAttribute("alertType", "error");
	        return "redirect:/admin/article/categories/create";
	    }
				
    }
    
    @PostMapping("/categories/{catId}/edit")
    public String editArticleCat(Model model,
    		@PathVariable(name = "catId") Integer catId,
    		@RequestParam(name = "catName") String catName,
    		@AuthenticationPrincipal AdminUserDetails prinUserDetails,
    		RedirectAttributes redirectAttributes) {
    	
		try {
			articleCatService.updateCat(catId, catName);
	        redirectAttributes.addFlashAttribute("alertMessage", "更新成功");
	        redirectAttributes.addFlashAttribute("alertType", "success");
		} catch (IllegalArgumentException e) {
	        redirectAttributes.addFlashAttribute("alertMessage", e.getMessage());
	        redirectAttributes.addFlashAttribute("alertType", "error"); 
	    }
    	
    	return "redirect:/admin/article/categories/" + catId;
    }
    
    @PostMapping("/categories/{catId}/deactivate")
    public String deactivateArticleCat(Model model,
    		@PathVariable Integer catId,
    		@AuthenticationPrincipal AdminUserDetails prinUserDetails,
    		RedirectAttributes redirectAttributes) {
    	
		try {
			articleCatService.deactivateCat(catId);
	        redirectAttributes.addFlashAttribute("alertMessage", "更新成功");
	        redirectAttributes.addFlashAttribute("alertType", "success");
		} catch (IllegalArgumentException | IllegalStateException e) {
			 redirectAttributes.addFlashAttribute("alertMessage", e.getMessage());
			 redirectAttributes.addFlashAttribute("alertType", "error");
		}
		
    	return "redirect:/admin/article/categories/" + catId;
    }
    		
	@PostMapping("/{articleId}/review")
	public String reviewArticle(Model model, 
			@PathVariable Integer articleId,
			@RequestParam(name = "action") String action,
			@RequestParam(value = "rejectReason", required = false) Integer rejectReason,
			@RequestParam(value = "rejectNote", required = false) String rejectNote,
			@AuthenticationPrincipal AdminUserDetails prinUserDetails,
			RedirectAttributes redirectAttributes) {
		
		Integer adminId = prinUserDetails.getAdmin().getAdminId();
		
		try {
			if ("approve".equals(action)) {
				articleService.approveArticle(articleId, adminId);
			} else if ("reject".equals(action)) {
				articleService.rejectArticle(articleId, adminId, rejectReason, rejectNote);
			}
		} catch (IllegalArgumentException | IllegalStateException e) {
			redirectAttributes.addFlashAttribute("alertMessage", e.getMessage());
		} catch (Exception e) { // 資料庫欄位超長: DataIntegrityViolationException
	        redirectAttributes.addFlashAttribute("alertMessage", "系統錯誤，請稍後再試");
		}
		
		return "redirect:/admin/article/pending";
	}
	
	@PostMapping("/{articleId}/unpublish")
	public String unpublishArticle(Model model,
			@PathVariable(name = "articleId") Integer articleId,
			@AuthenticationPrincipal AdminUserDetails prinUserDetails,
			RedirectAttributes redirectAttributes) {
		Integer adminId = prinUserDetails.getAdmin().getAdminId();
		
		try {
			articleService.unPublishArticle(articleId, adminId);
		} catch (IllegalArgumentException | IllegalStateException e) {
	        redirectAttributes.addFlashAttribute("alertMessage", e.getMessage());
		}
		
		return "redirect:/admin/article/reviewed";
	}
	
}
