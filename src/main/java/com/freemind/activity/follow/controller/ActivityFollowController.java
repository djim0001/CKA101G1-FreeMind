package com.freemind.activity.follow.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.activity.activity.model.Activity;
import com.freemind.activity.follow.model.ActivityFollowService;
import com.freemind.activity.util.PageUtils;
import com.freemind.login.security.membersecurity.MemberUserDetails;

@Controller
@RequestMapping("/member/activity/follow")
public class ActivityFollowController {

    @Autowired
    private ActivityFollowService followSvc;
    
    private static final int PAGE_SIZE = 3;

    @PostMapping("follow")
    public String follow(@RequestParam("activityId") Integer activityId,
                           @AuthenticationPrincipal MemberUserDetails userDetails,
                           RedirectAttributes redirectAttributes) {
    		Integer memberId = userDetails.getMember().getMemberId();
    		try {
    			followSvc.follow(memberId, activityId);
    			redirectAttributes.addFlashAttribute("successMessage", "關注成功");
    		} catch (IllegalStateException e) {
    			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
    		}
    		 return "redirect:/member/activity/listOneActivity?activityId=" + activityId;
    }
 
    @PostMapping("unfollow")
    public String unfollow(@RequestParam("activityId") Integer activityId,
                           @AuthenticationPrincipal MemberUserDetails userDetails,
                           RedirectAttributes redirectAttributes) {
    		Integer memberId = userDetails.getMember().getMemberId();
    		try {
    			followSvc.unfollow(memberId, activityId);
    			redirectAttributes.addFlashAttribute("successMessage", "已取消關注");
    		} catch (IllegalStateException e) {
    			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
    		}
    		 return "redirect:/member/activity/listOneActivity?activityId=" + activityId;
    }
    

    // 我的關注清單
    @GetMapping("myFollows")
    public String myFollows(@RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
    							@AuthenticationPrincipal MemberUserDetails userDetails,
    							ModelMap model) {
    		Integer memberId = userDetails.getMember().getMemberId();
    		List<Activity> list = followSvc.myFollows(memberId);
    		
    		int totalPages = PageUtils.calculateTotalPages(list.size(), PAGE_SIZE);
    	    if (currentPage < 1) {
    	        currentPage = 1;
    	    } else if (currentPage > totalPages) {
    	        currentPage = totalPages;
    	    }

    	    int fromIndex = (currentPage - 1) * PAGE_SIZE;
    	    int toIndex = Math.min(fromIndex + PAGE_SIZE, list.size());
    	    List<Activity> pageData = fromIndex < toIndex 
    	            ? list.subList(fromIndex, toIndex) 
    	            : new ArrayList<>();

    	    model.addAttribute("followListData", pageData);
    	    model.addAttribute("currentPage", currentPage);
    	    model.addAttribute("totalPages", totalPages);
    		
    		return "front-end/member/activity/follow/myFollows";
    }
    
}
