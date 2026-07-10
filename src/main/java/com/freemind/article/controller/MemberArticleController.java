package com.freemind.article.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.freemind.article.service.ArticleInteractionService;
import com.freemind.login.security.membersecurity.MemberUserDetails;


@Controller
@RequestMapping("/member/article")  
public class MemberArticleController {
	
	@Autowired
	private ArticleInteractionService articleInteractionService;
	
	@PostMapping("/{articleId}/like")
	public String toggleLike(@PathVariable(name = "articleId") Integer articleId,
			@RequestParam(name = "page", defaultValue = "1") Integer page,
            @RequestParam(name = "catId", required = false) Integer catId,
            @AuthenticationPrincipal MemberUserDetails principal) {
		
		
		Integer memberId = principal.getMember().getMemberId();
		
		articleInteractionService.toggleLike(articleId, memberId);
		return "redirect:/article/" + articleId + "?page=" + page 
				+ (catId != null ? "&catId=" + catId : "");
	}

}
