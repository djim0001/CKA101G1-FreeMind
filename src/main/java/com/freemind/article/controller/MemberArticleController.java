package com.freemind.article.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freemind.article.entity.Article;
import com.freemind.article.service.ArticleInteractionService;
import com.freemind.article.service.ArticleService;
import com.freemind.login.security.membersecurity.MemberUserDetails;


@RestController
@RequestMapping("/member/article")  
public class MemberArticleController {
	
	@Autowired
	private ArticleService articleService;
	
	@Autowired
	private ArticleInteractionService articleInteractionService;
	
	@PostMapping("/{articleId}/like")
	public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Integer articleId,
														  @AuthenticationPrincipal MemberUserDetails prinUserDetails) {
		Integer memberId = prinUserDetails.getMember().getMemberId();
		articleInteractionService.toggleLike(articleId, memberId);
		
		Article article = articleService.getPublishedArticle(articleId);
		long likeCount = article.getLikeBaseCount() + articleInteractionService.getLikeCount(articleId);
		boolean likedByMe = articleInteractionService.isLikedByMember(articleId, memberId);
		
		return ResponseEntity.ok(Map.of(
				"likeCount", likeCount,
				"likedByMe", likedByMe)
				);
	}
	
	@PostMapping("/{articleId}/bookmark")
	public ResponseEntity<Map<String, Object>> toggleBookmark(@PathVariable Integer articleId,
            												  @AuthenticationPrincipal MemberUserDetails prinUserDetails) {
		Integer memberId = prinUserDetails.getMember().getMemberId();
		articleInteractionService.toggleBookmark(articleId, memberId);
		
		Article article = articleService.getPublishedArticle(articleId);
		long bookmarkCount = article.getBookmarkBaseCount() + articleInteractionService.getBookmarkCount(articleId);
		boolean savedByMe = articleInteractionService.isSavedByMember(articleId, memberId);
		
		return ResponseEntity.ok(Map.of(
				"bookmarkCount", bookmarkCount,
				"savedByMe", savedByMe)
				);
	}

}
