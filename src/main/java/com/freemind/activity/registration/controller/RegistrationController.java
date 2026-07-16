package com.freemind.activity.registration.controller;

import java.util.List;
import java.util.Map;

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
import com.freemind.activity.activity.model.ActivityService;
import com.freemind.activity.registration.model.Registration;
import com.freemind.activity.registration.model.RegistrationService;
import com.freemind.activity.report.model.ActivityReportService;
import com.freemind.login.member.model.Member;
import com.freemind.login.security.membersecurity.MemberUserDetails;

@Controller
@RequestMapping("/member/activity/registration")
public class RegistrationController {

    @Autowired
    private RegistrationService regisSvc;
    
    @Autowired 
    private ActivityService activitySvc;
    
    @Autowired
    private ActivityReportService reportSvc;

    // 一、我的報名清單
    @GetMapping("myRegistrations")
    public String myRegistrations(@AuthenticationPrincipal MemberUserDetails userDetails,
                                  ModelMap model) {
      
    		Member member = userDetails.getMember();
    		List<Registration> list = regisSvc.getMyRegistrations(member);
    		model.addAttribute("regisListData", list);
    		
    		Map<Integer, String> reportedActivityMap = reportSvc.getReportedActivityMap(member);
    	    model.addAttribute("reportedActivityMap", reportedActivityMap);
    		
    		return "front-end/member/activity/registration/myRegistrations";
    }
    
    // 二、送出報名
    @PostMapping("register")
    public String register(@RequestParam("activityId") Integer activityId,
                           @AuthenticationPrincipal MemberUserDetails userDetails,
                           RedirectAttributes redirectAttributes) {
    		Member member = userDetails.getMember();
    		Activity activity = activitySvc.getOneActivity(activityId);
    		
    		if (activity == null) {
    			redirectAttributes.addFlashAttribute("errorMessage", "活動不存在");
    			return "redirect:/member/activity/registration/myRegistrations";
    		}
    		
    		try {
    			regisSvc.register(member, activity);
    			redirectAttributes.addFlashAttribute("successMessage", "報名成功,等待審核");
    		} catch (IllegalArgumentException | IllegalStateException e) {
    			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
    		}
    		 return "redirect:/member/activity/registration/myRegistrations";
    }
    
    // 三、取消報名
    @PostMapping("cancel")
    public String cancel(@RequestParam("regisId") Integer regisId,
                         @RequestParam("cancelReason") Integer cancelReason,
                         @RequestParam("cancelNote") String cancelNote,
                         @AuthenticationPrincipal MemberUserDetails userDetails,
                         RedirectAttributes redirectAttributes) {
    		try {
    			regisSvc.cancel(regisId, cancelReason, cancelNote, userDetails.getMember());
    			redirectAttributes.addFlashAttribute("successMessage", "已取消報名");
    		} catch (IllegalArgumentException | IllegalStateException e) {
    			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
    		}
    		 return "redirect:/member/activity/registration/myRegistrations";
    }
    
    // 四、活動報名名單(給發起人檢視)
    @GetMapping("activityRegistrations")
    public String activityRegistrations(@RequestParam("activityId") Integer activityId,
                                        @AuthenticationPrincipal MemberUserDetails userDetails,
                                        ModelMap model) {
    		Activity activity = activitySvc.getOneActivity(activityId);
    		List<Registration> list = regisSvc.getRegistrationsByActivity(activity, userDetails.getMember());
    		model.addAttribute("regisListData", list);
    		model.addAttribute("activity", activity);
    		
        return "front-end/member/activity/registration/activityRegistrations";
    }
    
    // 五、審核活動報名(發起人)
    @PostMapping("approve")
    public String approve(@RequestParam("regisId") Integer regisId,
    						  @RequestParam("activityId") Integer activityId, 
                          @AuthenticationPrincipal MemberUserDetails userDetails,
                          RedirectAttributes redirectAttributes) {
   
		try {
			regisSvc.approve(regisId, userDetails.getMember());
			redirectAttributes.addFlashAttribute("successMessage", "審核完成");
		} catch (IllegalArgumentException | IllegalStateException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		return "redirect:/member/activity/registration/activityRegistrations?activityId=" + activityId;
    }
    
    // 六、填寫評論
    @PostMapping("review")
    public String review(@RequestParam("regisId") Integer regisId,
                         @RequestParam("rating") Integer rating,
                         @RequestParam("reviewContent") String reviewContent,
                         @AuthenticationPrincipal MemberUserDetails userDetails,
                         RedirectAttributes redirectAttributes) {
    		try {
    			regisSvc.review(regisId, rating, reviewContent, userDetails.getMember());
    			redirectAttributes.addFlashAttribute("successMessage", "已送出評論");
    		} catch (IllegalArgumentException | IllegalStateException e) {
    			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
    		}
    		 return "redirect:/member/activity/registration/myRegistrations";
    }
}