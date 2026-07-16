package com.freemind.login.member.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.freemind.article.entity.Article;
import com.freemind.article.service.ArticleInteractionService;
import com.freemind.login.security.membersecurity.MemberUserDetails;


@Controller      
@RequestMapping("/member/dashboard")
public class MemberDashboardController {  
	
	@Autowired
	private ArticleInteractionService articleInteractionService;
	
	@GetMapping
    public String getDashboard() {
        return "front-end/member/memberpage/dashboard";
    }
	
	@GetMapping("/myActivityRegistration")
	public String myActivityRegistration() {
	    return "redirect:/member/activity/registration/myRegistrations";
	}

	
	@GetMapping("/myCollection")
    public String getMyCollection() {
        return "front-end/member/memberpage/myCollection";
    }
	
	@GetMapping("/myCollection/article")
	public String articleTabs(Model model,
			 @RequestParam(defaultValue = "bookmark") String type,
			 @RequestParam(defaultValue = "1") Integer page,
			 @AuthenticationPrincipal MemberUserDetails prinUserDetails) {
		
		if (prinUserDetails == null) {
		    return "redirect:/front-end/login";
		}
		
		Integer memberId = prinUserDetails.getMember().getMemberId();
	
		Page<Article> articlePage;
		switch(type) {
			case "like":
				articlePage = articleInteractionService.getLikedArticles(memberId, page);
				break;
			case "history":
				articlePage = articleInteractionService.getViewHistory(memberId, page);
				break;
			default:
				type = "bookmark";
				articlePage = articleInteractionService.getSavedArticles(memberId, page);
				break;
		}
		
		model.addAttribute("activeTab", type);
		model.addAttribute("articlePage", articlePage);
	    model.addAttribute("currentPage", page);
		return "front-end/member/article/myArticleCollection";
	}
	
	@GetMapping("/myCollection/activity")
	public String myCollectionActivity() {
	    return "redirect:/member/activity/follow/myFollows";
	}
	
}
