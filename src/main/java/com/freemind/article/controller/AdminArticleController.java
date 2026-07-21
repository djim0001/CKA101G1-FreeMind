package com.freemind.article.controller;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.article.dto.ArticleWithStatsDTO;
import com.freemind.article.entity.Article;
import com.freemind.article.entity.ArticleCat;
import com.freemind.article.service.ArticleCatService;
import com.freemind.article.service.ArticleInteractionService;
import com.freemind.article.service.ArticleService;
import com.freemind.login.admin.model.Admin;
import com.freemind.login.notice.service.NoticeService;
import com.freemind.login.security.adminsecurity.AdminUserDetails;

import org.springframework.security.core.Authentication;


@Controller
@RequestMapping("/admin/article")
public class AdminArticleController {

	@Autowired
	private ArticleService articleService;

	@Autowired
	private ArticleCatService articleCatService;

	@Autowired
	private ArticleInteractionService articleInteractionService;

	@Autowired
	private NoticeService noticeService;

	@ModelAttribute("admin")
	public Admin currentAdmin(Authentication authentication) {
		if (authentication == null || 
		    !(authentication.getPrincipal() instanceof AdminUserDetails adminUser)) {
			return null;
		}
		return adminUser.getAdmin();
	}

	@GetMapping
    public String articleAdmin(Model model,
    		@RequestParam(name = "page", defaultValue = "1") Integer page,
    		@RequestParam(name = "keyword", required = false) String keyword,
	        @RequestParam(name = "catId", required = false) Integer catId,
	        @RequestParam(name = "status", required = false) Integer status,
	        @RequestParam(name = "sort", defaultValue = "newest") String sort) {
		Page<Article> articlePage = articleService.getSubmittedArticles(keyword, catId, status, sort, page);
	    model.addAttribute("articlePage", articlePage);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("articleCats", articleCatService.getAllCats());
	    model.addAttribute("currentKeyword", keyword);
	    model.addAttribute("currentCatId", catId);
	    model.addAttribute("currentStatus", status);
	    model.addAttribute("currentSort", sort);
	    
	    Map<Integer, String> articleStatuses = new LinkedHashMap<>();
	    articleStatuses.put(1, "待審核");
	    articleStatuses.put(2, "已上架");
	    articleStatuses.put(3, "已退回");
	    articleStatuses.put(4, "已下架");
	    model.addAttribute("articleStatuses", articleStatuses);
	    
	    StringBuilder qs = new StringBuilder();
	    if (keyword != null && !keyword.isEmpty()) qs.append("keyword=").append(keyword).append("&");
	    if (catId != null) qs.append("catId=").append(catId).append("&");
	    if (status != null) qs.append("status=").append(status).append("&");
	    if (sort != null && !"newest".equals(sort)) qs.append("sort=").append(sort).append("&");
	    if (qs.length() > 0) qs.setLength(qs.length() - 1);
	    model.addAttribute("filterQueryString", qs.toString());
	    
		return "back-end/article/articleAdmin";
    }
	
    @GetMapping("/pending")
    public String getPendingArticles(Model model, 
    		@RequestParam(name = "page", defaultValue = "1") Integer page) {
		Page<Article> articlePage = articleService.getPendingArticles(page);
        model.addAttribute("articlePage", articlePage);
        model.addAttribute("currentPage", page);
	    return "back-end/article/pendingList";

    }
    
    @GetMapping("/reviewed")
    public String getReviewedArticles(Model model,
    		@RequestParam(name = "status", required = false) Integer status,
    		@RequestParam(name = "page", defaultValue = "1") Integer page) {
		Page<Article> articlePage = articleService.getReviewedArticles(status, page);
		
		List<ArticleWithStatsDTO> articleList = new ArrayList<>();
		
		for (Article article : articlePage.getContent()) {
			ArticleWithStatsDTO stats;

			if (article.getArticleStatus() == 2 || article.getArticleStatus() == 4) {
				stats = articleInteractionService.getArticleStatistics(article);
			} else {
				stats = ArticleWithStatsDTO.builder().article(article).build();
			}

			articleList.add(stats);
		}
		
		model.addAttribute("articlePage", articlePage);
	    model.addAttribute("currentPage", page);
	    model.addAttribute("currentStatus", status);
		model.addAttribute("articleList", articleList);
    	return "back-end/article/reviewedList";
    }
    
    @GetMapping("/{articleId}/review")
    public String getReviewDetail(Model model, 
			@PathVariable Integer articleId,
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
    		@RequestParam(name = "keyword", required = false) String keyword,
    		@RequestParam(name = "page", defaultValue = "1") Integer page) {
		List<ArticleCat> cats;
		if (keyword != null && !keyword.isBlank()) {
			cats = articleCatService.getCatsByName(keyword);
		} else {
			cats = articleCatService.getAllCats();
		}
		
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
    public String getCreateForm(Model model) {
    	List<String> catNames = articleCatService.getAllCats()
    											 .stream()
								                 .map(ArticleCat::getArticleCatName)
								                 .toList();
        model.addAttribute("catNames", catNames);
    	return "back-end/article/createCatForm";
    }
    
    @GetMapping("/categories/{catId}")
    public String getCatDetail(Model model,
    		@PathVariable Integer catId,
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
			@AuthenticationPrincipal AdminUserDetails prinAdminUser,
			RedirectAttributes redirectAttributes) {
		Integer adminId = prinAdminUser.getAdmin().getAdminId();
		
		try {
			Article article = articleService.getArticleForReview(articleId);
			Integer psychId = article.getPsychologist().getPsychId();
			String title = article.getTitle();
			
			if ("approve".equals(action)) {
				articleService.approveArticle(articleId, adminId);
				noticeService.sendToPsych(psychId, adminId,
						"您的文章「" + title + "」已審核通過並上架", (byte) 0);
				
			} else if ("reject".equals(action)) {
				articleService.rejectArticle(articleId, adminId, rejectReason, rejectNote);
				
				String[] rejectReasons = {"內容品質", "違反專業法規", "版權問題", "違反平台規範", "其他"};
				String reason = rejectReasons[rejectReason];
				noticeService.sendToPsych(psychId, adminId,
						"您的文章「" + title + "」已被退回，原因：" + reason, (byte) 0);
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
			@AuthenticationPrincipal AdminUserDetails prinAdminUser,
			RedirectAttributes redirectAttributes) {
		Integer adminId = prinAdminUser.getAdmin().getAdminId();
		
		try {
			Article article = articleService.getPublishedArticle(articleId);
			Integer psychId = article.getPsychologist().getPsychId();
			String title = article.getTitle();
			
			articleService.unPublishArticle(articleId, adminId);
			noticeService.sendToPsych(psychId, adminId,
					"您的文章「" + title + "」已被管理員下架", (byte) 0);
		} catch (IllegalArgumentException | IllegalStateException e) {
	        redirectAttributes.addFlashAttribute("alertMessage", e.getMessage());
		}
		
		return "redirect:/admin/article/reviewed";
	}
	
}
