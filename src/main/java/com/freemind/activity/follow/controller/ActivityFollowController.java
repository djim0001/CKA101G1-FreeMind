package com.freemind.activity.follow.controller;

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
import com.freemind.login.security.membersecurity.MemberUserDetails;

@Controller
@RequestMapping("/member/activity/follow")
public class ActivityFollowController {

    @Autowired
    private ActivityFollowService followSvc;

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
    public String myFollows(@AuthenticationPrincipal MemberUserDetails userDetails,
    							  ModelMap model) {
    		Integer memberId = userDetails.getMember().getMemberId();
    		List<Activity> list = followSvc.myFollows(memberId);
    		model.addAttribute("followListData", list);
    		
    		return "front-end/member/activity/follow/myFollows";
    }
    
}
