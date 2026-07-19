package com.freemind.article.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.freemind.article.service.ArticleInteractionService;
import com.freemind.login.security.membersecurity.MemberUserDetails;


@RestController
@RequestMapping("/member/article")  
public class MemberArticleController {
	
	@Autowired
	private ArticleInteractionService articleInteractionService;
	
	@PostMapping("/{articleId}/like")
	public ResponseEntity<Map<String, Object>> toggleLike(@PathVariable Integer articleId,
														  @AuthenticationPrincipal MemberUserDetails prinMemberUser) {
		Integer memberId = prinMemberUser.getMember().getMemberId();
		articleInteractionService.toggleLike(articleId, memberId);
		
		return ResponseEntity.ok(Map.of(
				"likeCount", articleInteractionService.getLikeCount(articleId),
				"likedByMe", articleInteractionService.isLikedByMember(articleId, memberId))
				);
	}
	
	@PostMapping("/{articleId}/bookmark")
	public ResponseEntity<Map<String, Object>> toggleBookmark(@PathVariable Integer articleId,
            												  @AuthenticationPrincipal MemberUserDetails prinMemberUser) {
		Integer memberId = prinMemberUser.getMember().getMemberId();
		articleInteractionService.toggleBookmark(articleId, memberId);
		
		return ResponseEntity.ok(Map.of(
				"bookmarkCount", articleInteractionService.getBookmarkCount(articleId),
				"savedByMe", articleInteractionService.isSavedByMember(articleId, memberId))
				);
	}

}
