package com.freemind.article.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.article.dto.ArticleListRecommendDTO;
import com.freemind.article.dto.ArticleWithStatsDTO;
import com.freemind.article.entity.Article;
import com.freemind.article.entity.ArticleCat;
import com.freemind.article.service.ArticleCatService;
import com.freemind.article.service.ArticleInteractionService;
import com.freemind.article.service.ArticleService;
import com.freemind.article.service.ArticleViewService;
import com.freemind.article.service.RecommendationService;
import com.freemind.course.course.model.Course;
import com.freemind.course.dto.CourseListRecommendDTO;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;
import com.freemind.login.psychologist.dto.PsychologistProfileRes;
import com.freemind.login.psychologist.entity.Psychologist;
import com.freemind.login.psychologist.service.PsychologistService;
import com.freemind.login.security.membersecurity.MemberUserDetails;
import com.freemind.login.security.psychologistsecurity.PsychUserDetails;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/article")
public class ArticleController {

	// 日期區間只存 session，不進網址
	private static final String SESSION_DATE_FROM = "articleDateFrom";
	private static final String SESSION_DATE_TO = "articleDateTo";

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
	
	@Autowired
    private MemberService memberService;

	@ModelAttribute("member")
    public Member currentMember(Authentication authentication) {
    	if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
    		return null;
    	}
    	return memberService.findByAccount(authentication.getName());
    }

	@ModelAttribute("psych")
    public Psychologist currentPsych(Authentication authentication) {
    	if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
    		return null;
    	}
    	return psychologistService.findByAccount(authentication.getName());
    }

	@ModelAttribute("articleCats")
	public List<ArticleCat> articleCatList() {
		return articleCatService.getActiveCats();
	}

	// 套用日期篩選：只寫進 session 後導回 /article，日期不會出現在網址上
	@PostMapping("/search")
	public String applyFilters(HttpSession session, RedirectAttributes ra,
						@RequestParam(name = "catId", required = false) Integer catId,
						@RequestParam(name = "keyword", required = false) String keyword,
						@RequestParam(name = "sort", defaultValue = "newest") String sort,
						@RequestParam(name = "dateFrom", required = false)
						@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
						@RequestParam(name = "dateTo", required = false)
						@DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo) {
		// 日期留白送出即代表取消日期篩選
		session.setAttribute(SESSION_DATE_FROM, dateFrom);
		session.setAttribute(SESSION_DATE_TO, dateTo);

		if (catId != null) ra.addAttribute("catId", catId);
		if (keyword != null && !keyword.isBlank()) ra.addAttribute("keyword", keyword);
		if (!"newest".equals(sort)) ra.addAttribute("sort", sort);
		return "redirect:/article";
	}

	// 清除所有篩選條件(含 session 中的日期區間)
	@GetMapping("/reset")
	public String resetFilters(HttpSession session) {
		clearDateFilter(session);
		return "redirect:/article";
	}

	@GetMapping
	public String getPublishedArticles(Model model, HttpSession session,
						@RequestParam(name = "page", defaultValue = "1") Integer page,
						@RequestParam(name = "catId", required = false) Integer catId,
						@RequestParam(name = "keyword", required = false) String keyword,
						@RequestParam(name = "sort", defaultValue = "newest") String sort) {
		LocalDate dateFrom = (LocalDate) session.getAttribute(SESSION_DATE_FROM);
		LocalDate dateTo = (LocalDate) session.getAttribute(SESSION_DATE_TO);

		String dateError = null;
		LocalDate today = LocalDate.now();
		if ((dateFrom != null && dateFrom.isAfter(today)) || (dateTo != null && dateTo.isAfter(today))) {
			dateError = "發布日期不可選擇未來日期";
		} else if (dateFrom != null && dateTo != null && dateFrom.isAfter(dateTo)) {
			dateError = "開始日期不可晚於結束日期";
		} else if ((dateFrom == null) != (dateTo == null)) {
			dateError = "請同時輸入開始與結束日期";
		}
		if (dateError != null) {
			dateFrom = null;
			dateTo = null;
			clearDateFilter(session);
		}

		Article featuredArticle = articleInteractionService.getMostSavedArticle();
		model.addAttribute("featuredArticle", featuredArticle);

		Integer excludeId = (featuredArticle != null) ? featuredArticle.getArticleId() : null;
		Page<Article> articlePage = articleService.getPublishedArticles(catId, keyword, dateFrom, dateTo, page, sort, excludeId);
		model.addAttribute("articlePage", articlePage);
		model.addAttribute("currentPage", page);
		model.addAttribute("selectedCatId", catId);
		model.addAttribute("keyword", keyword);
		model.addAttribute("sort", sort);
		model.addAttribute("dateFrom", dateFrom);
		model.addAttribute("dateTo", dateTo);
		model.addAttribute("dateError", dateError);

		List<Integer> hotIds = articleViewService.getHotArticleIds(3);
		List<Article> hotArticles = articleService.getPublishedArticlesByIds(hotIds);
		model.addAttribute("hotArticles", hotArticles);
		
		return "front-end/member/article/articleList";
	}

	private void clearDateFilter(HttpSession session) {
		session.removeAttribute(SESSION_DATE_FROM);
		session.removeAttribute(SESSION_DATE_TO);
	}

	@GetMapping("/{articleId}")
	public String getArticleDetail(Model model,
						@PathVariable Integer articleId, 
						@RequestParam(name = "page", defaultValue = "1") Integer page,
			            @RequestParam(name = "catId", required = false) Integer catId,
			            HttpServletRequest request,
			            @AuthenticationPrincipal MemberUserDetails prinMemberUser,
			            @AuthenticationPrincipal PsychUserDetails prinPsychUser) {
		Integer psychId = (prinPsychUser != null) ? prinPsychUser.getPsychologist().getPsychId() : null;
		
		Article article = articleService.getArticle(articleId, psychId);
		
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
	@PostMapping("/{articleId}/recommend/article")
	public ResponseEntity<ArticleListRecommendDTO> getRecommendatedArticles(@PathVariable Integer articleId,
																		    @AuthenticationPrincipal MemberUserDetails prinMemberUser) {
		List<Article> articleList;
		if (prinMemberUser != null) {
			articleList = recommendationService.getArticleRecommendation(prinMemberUser.getMember(), articleId);
		} else {
			articleList = recommendationService.getArticleRecommendation(articleId);
		}
		
		ArticleListRecommendDTO dto = new ArticleListRecommendDTO(articleList);
		return ResponseEntity.ok().body(dto);
	}
	
	@ResponseBody
	@PostMapping("/{articleId}/recommend/course")
	public ResponseEntity<CourseListRecommendDTO> getRecommendatedCourses(@PathVariable Integer articleId) {
		List<Course> courseList = recommendationService.getCourseRecommendation(articleId);
		
		CourseListRecommendDTO dto = new CourseListRecommendDTO(courseList);
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
