package com.freemind.article.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.freemind.article.dto.ArticleRecommendDTO;
import com.freemind.article.dto.ArticleWithStatsDTO;
import com.freemind.article.entity.Article;
import com.freemind.article.entity.ArticleCat;
import com.freemind.article.service.ArticleCatService;
import com.freemind.article.service.ArticleInteractionService;
import com.freemind.article.service.ArticleService;
import com.freemind.article.service.ArticleViewService;
import com.freemind.article.service.RecommendationService;
import com.freemind.login.psychologist.dto.PsychologistProfileRes;
import com.freemind.login.psychologist.service.PsychologistService;
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
	private ArticleViewService articleViewService;
	
	@Autowired
	private RecommendationService recommendationService;
	
	@Autowired
	private PsychologistService psychologistService;
	
	@ModelAttribute("articleCats")
	public List<ArticleCat> articleCatList() {
		return articleCatService.getActiveCats();
	}

	@GetMapping
	public String getPublishedArticles(Model model, 
						@RequestParam(name = "page", defaultValue = "1") Integer page,
						@RequestParam(name = "catId", required = false) Integer catId,
						@RequestParam(name = "keyword", required = false) String keyword) {
		Page<Article> articlePage = articleService.getPublishedArticles(catId, keyword, page);
		model.addAttribute("articlePage", articlePage);
		model.addAttribute("currentPage", page);
		model.addAttribute("selectedCatId", catId);
		model.addAttribute("keyword", keyword);
		
		List<Integer> hotIds = articleViewService.getHotArticleIds(3);
		List<Article> hotArticles = articleService.getPublishedArticlesByIds(hotIds);
		model.addAttribute("hotArticles", hotArticles);
		
		return "front-end/member/article/articleList";
	}
	
	@GetMapping("/{articleId}")
	public String getArticleDetail(Model model,
						@PathVariable Integer articleId, 
						@RequestParam(name = "page", defaultValue = "1") Integer page,
			            @RequestParam(name = "catId", required = false) Integer catId,
			            HttpServletRequest request,
			            @AuthenticationPrincipal MemberUserDetails prinMemberUser) {
		Article article = articleService.getPublishedArticle(articleId);
		
		if (article == null) {
			model.addAttribute("errorMessage", "查無此文章");
			return "front-end/member/article/articleList";
		}
		
		if (article.getPsychologist() != null && article.getPsychologist().getPsychId() != null) {
		    try {
		        PsychologistProfileRes authorPsych = psychologistService.getProfile(article.getPsychologist().getPsychId());
		        model.addAttribute("authorPsych", authorPsych);
		    } catch (IllegalArgumentException e) {
		        model.addAttribute("authorPsych", null);
		    }
		}
		
		Integer memberId = (prinMemberUser != null) ? prinMemberUser.getMember().getMemberId() : null;
		
		String visitorKey = buildVisitorKey(memberId, request);
		articleViewService.recordViewCount(articleId, visitorKey);
		
		if (memberId != null) articleInteractionService.recordView(articleId, memberId); // for ArticleViewHistory
		
		ArticleWithStatsDTO stats = articleInteractionService.getArticleStatistics(article);
		model.addAttribute("article", article);
		model.addAttribute("stats", stats); // likeCount, bookmarkCount, shareCount, viewCount
		
		model.addAttribute("currentPage", page);
		model.addAttribute("selectedCatId", catId);
		model.addAttribute("likedByMe", articleInteractionService.isLikedByMember(articleId, memberId));
		model.addAttribute("savedByMe", articleInteractionService.isSavedByMember(articleId, memberId));
		return "front-end/member/article/articleDetail";
	}
	
	@ResponseBody
	@PostMapping("/{articleId}/recommend")
	public ResponseEntity<ArticleRecommendDTO> getRecommendatedArticles(@PathVariable Integer articleId,
																		@AuthenticationPrincipal MemberUserDetails prinMemberUser) {
		List<Article> articleList;
		if (prinMemberUser != null) {
			articleList = recommendationService.getArticleRecommendation(prinMemberUser.getMember(), articleId);
		} else {
			articleList = recommendationService.getArticleRecommendation(articleId);
		}
		
		ArticleRecommendDTO dto = new ArticleRecommendDTO(articleList);
		return ResponseEntity.ok().body(dto);
	}
	
	@ResponseBody
	@PostMapping("/{articleId}/share")
	public ResponseEntity<Map<String, Object>> shareArticle(@PathVariable Integer articleId) {
		long shareCount = articleService.incrementAndGetShareCount(articleId);
		return ResponseEntity.ok(Map.of("shareCount", shareCount));
	}
	
	private String buildVisitorKey(Integer memberId, HttpServletRequest request) {
		if (memberId != null) {
			return "u:" + memberId;
		}
		
		String raw = request.getRemoteAddr() + ":" + request.getHeader("User-Agent");
		String hash = "ip:" + DigestUtils.md5DigestAsHex(raw.getBytes()); // 固定 32 字元的十六進位字串
		return hash;
	}
	
}
