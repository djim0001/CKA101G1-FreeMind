package com.freemind.article.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.freemind.article.entity.Article;
import com.freemind.article.entity.ArticleCat;
import com.freemind.article.service.ArticleCatService;
import com.freemind.article.service.ArticleInteractionService;
import com.freemind.article.service.ArticleService;
import com.freemind.article.service.ArticleViewService;
import com.freemind.login.security.membersecurity.MemberUserDetails;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/article")
public class ArticleController {
	
	@Autowired
	private ArticleService articleService;
	
	@Autowired
	private ArticleCatService articleCatService;
	
	@Autowired
	private ArticleInteractionService articleInteractionService;
	
	@Autowired
	private StringRedisTemplate redisTemplate;
	
	@Autowired
	private ArticleViewService articleViewService;
	
	@ModelAttribute("articleCats")
	public List<ArticleCat> articleCatList() {
		return articleCatService.getActiveCats();
	}

	@GetMapping
	public String getPublishedArticles(Model model, 
						@RequestParam(name = "page", defaultValue = "1") Integer page, 
						@RequestParam(name = "catId", required = false) Integer catId) {
		Page<Article> articlePage = articleService.getPublishedArticles(page, catId);
		model.addAttribute("articlePage", articlePage);
		model.addAttribute("currentPage", page);
		model.addAttribute("selectedCatId", catId);
		return "front-end/member/article/articleList";
	}
	
	@GetMapping("/{articleId}")
	public String getArticleDetail(Model model,
						@PathVariable Integer articleId, 
						@RequestParam(name = "page", defaultValue = "1") Integer page,
			            @RequestParam(name = "catId", required = false) Integer catId,
			            HttpServletRequest request,
			            @CookieValue(value = "visitorId", required = false) String visitorId,
			            @AuthenticationPrincipal MemberUserDetails prinUserDetails) {
		Article article = articleService.getPublishedArticle(articleId);
		
		if (article == null) {
			model.addAttribute("errorMessage", "查無此文章");
			return "front-end/member/article/articleList";
		}
		
		Integer memberId = (prinUserDetails != null) ? prinUserDetails.getMember().getMemberId() : null;
		
		String visitorKey = buildVisitorKey(memberId, request, visitorId);
		articleViewService.recordViewCount(articleId, visitorKey);
		
		if (memberId != null) articleInteractionService.recordView(articleId, memberId);
		
		long realLikeCount = articleInteractionService.getLikeCount(articleId);
		long displayLikeCount = article.getLikeBaseCount() + realLikeCount;
		long realBookmarkCount = articleInteractionService.getBookmarkCount(articleId);
		long displayBookmarkCount = article.getBookmarkBaseCount() + realBookmarkCount;
		
		long dbViewCount = article.getViewCount();
		String pendingValue = redisTemplate.opsForValue().get("view:pending:" + articleId);
		long pendingViewCount = (pendingValue != null) ? Long.parseLong(pendingValue) : 0L;
		long displayViewCount = dbViewCount + pendingViewCount;
		
		model.addAttribute("article", article);
		model.addAttribute("currentPage", page);
		model.addAttribute("selectedCatId", catId);
//		model.addAttribute("likeCount", articleInteractionService.getLikeCount(articleId));
//		model.addAttribute("bookmarkCount", articleInteractionService.getBookmarkCount(articleId));
		model.addAttribute("likeCount", displayLikeCount);
		model.addAttribute("bookmarkCount", displayBookmarkCount);
		model.addAttribute("shareCount", article.getShareCount());
		model.addAttribute("viewCount", displayViewCount);
		model.addAttribute("likedByMe", articleInteractionService.isLikedByMember(articleId, memberId));
		model.addAttribute("savedByMe", articleInteractionService.isSavedByMember(articleId, memberId));
		model.addAttribute("isLoggedIn", memberId != null);
		return "front-end/member/article/articleDetail";
	}
	
	@ResponseBody
	@PostMapping("/{articleId}/share")
	public ResponseEntity<Map<String, Object>> shareArticle(@PathVariable Integer articleId) {
		long shareCount = articleService.incrementAndGetShareCount(articleId);
		return ResponseEntity.ok(Map.of("shareCount", shareCount));
	}
	
	private String buildVisitorKey(Integer memberId, HttpServletRequest request, String visitorId) {
		if (memberId != null) {
			return "u:" + memberId;
		}
		
		if (visitorId != null) {
			return "c:" + visitorId;
		}
		
		String raw = request.getRemoteAddr() + ":" + request.getHeader("User-Agent");
		String hash = "ip:" + DigestUtils.md5DigestAsHex(raw.getBytes()); // 固定 32 字元的十六進位字串
		return hash;
	}
	
}
