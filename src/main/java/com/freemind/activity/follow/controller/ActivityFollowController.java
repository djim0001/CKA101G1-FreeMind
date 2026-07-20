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
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "請先登入");
            return "redirect:/front-end/login";
        }
    	    		
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
                              @RequestParam(value = "page", required = false) Integer page,
                              @RequestParam(value = "redirectTo", required = false) String redirectTo,
                              @AuthenticationPrincipal MemberUserDetails userDetails,
                              RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "請先登入");
            return "redirect:/front-end/login";
        }

        Integer memberId = userDetails.getMember().getMemberId();
        try {
            followSvc.unfollow(memberId, activityId);
            redirectAttributes.addFlashAttribute("successMessage", "已取消關注");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        if ("myFollows".equals(redirectTo)) {
            int p = (page != null) ? page : 1;
            return "redirect:/member/activity/follow/myFollows?page=" + p;
        }
        return "redirect:/member/activity/listOneActivity?activityId=" + activityId;
    }

 // 我的關注清單
    @GetMapping("myFollows")
    public String myFollows(@RequestParam(value = "page", required = false) Integer page,
        						@AuthenticationPrincipal MemberUserDetails userDetails,
        						ModelMap model) {
        if (userDetails == null) {
            return "redirect:/front-end/login";
        }

        Integer memberId = userDetails.getMember().getMemberId();
        List<Activity> list = followSvc.myFollows(memberId);

        int totalPages = PageUtils.calculateTotalPages(list.size(), PAGE_SIZE);

        int currentPage = (page == null) ? 1 : page;
        if (currentPage < 1) {
            currentPage = 1;
        } else if (totalPages > 0 && currentPage > totalPages) {
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
