package com.freemind.activity.registration.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.activity.activity.model.Activity;
import com.freemind.activity.activity.model.ActivityService;
import com.freemind.activity.registration.model.Registration;
import com.freemind.activity.registration.model.RegistrationService;
import com.freemind.activity.report.model.ActivityReportService;
import com.freemind.activity.util.PageUtils;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;
import com.freemind.login.security.membersecurity.MemberUserDetails;

import jakarta.validation.ConstraintViolationException;

@Controller
@RequestMapping("/member/activity/registration")
public class RegistrationController {

    @Autowired
    private RegistrationService regisSvc;
    
    @Autowired 
    private ActivityService activitySvc;
    
    @Autowired
    private ActivityReportService reportSvc;
    
    private static final int PAGE_SIZE = 3;
    
    @Autowired
    private MemberService memberSvc;
    
    @ModelAttribute("member")
    public Member currentMember(Authentication authentication) {
        // 訪客（未登入或匿名）時不放 member 進 model
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return memberSvc.findByAccount(authentication.getName());
    }

    // 一、我的報名清單
    @GetMapping("myRegistrations")
    public String myRegistrations(@RequestParam(value = "tab", defaultValue = "upcoming") String tab,
    								@RequestParam(value = "page", defaultValue = "1") Integer page,
    								@AuthenticationPrincipal MemberUserDetails userDetails,
                                  ModelMap model) {
        if (userDetails == null) {
            return "redirect:/front-end/login";
        }
    		
        Integer currentPage = page;
        
    		Member member = userDetails.getMember();
    		List<Registration> allList = regisSvc.getMyRegistrations(member);
    		
    	    List<Registration> filtered;
    	    // 判斷使用者要看哪一個tab, 並存進filtered變數
    	     // 審核中
    	    if ("pending".equals(tab)) {
	    	    	filtered = new ArrayList<>();
	    	    	for (Registration r : allList) {
	    	    	    if (r.getRegisStatus() == 0) {
	    	    	        filtered.add(r);
	    	    	    }
	    	    	}
	    	 // 歷史紀錄
    	    } else if ("history".equals(tab)) {
	    	    	filtered = new ArrayList<>();
	    	    	for (Registration r : allList) {
	    	    	    int status = r.getRegisStatus();
	    	    	    boolean isRejectedOrCancelled = (status == 2 || status == 3);
	    	    	    boolean isEndedOrCancelledActivity = ((status == 1 || status == 4) && (r.getActivity().isEnded() || r.getActivity().getActivityStatus() == 4));
	    	    	    
	    	    	    if (isRejectedOrCancelled || isEndedOrCancelledActivity) {
	    	    	        filtered.add(r);
	    	    	    }
	    	    	}
    	     // 即將參加
    	    } else {
    	        filtered = new ArrayList<>();
    	        for (Registration r : allList) {
    	            int status = r.getRegisStatus();
    	            if ((status == 1 || status == 4) && !r.getActivity().isEnded() && r.getActivity().getActivityStatus() != 4) {
    	                filtered.add(r);
    	            }
    	        }
    	    
    	    	// 篩完之後才排序:用 Collections.sort + 自訂比較規則
	    	    			// 排序filtered這個list, 排序規則(比較器)
    	    	Collections.sort(filtered, new Comparator<Registration>() {
    	    	    @Override
    	    	    public int compare(Registration r1, Registration r2) {
    	    	        return r1.getActivity().getActivityStart().compareTo(r2.getActivity().getActivityStart());
    	    	    }
    	    	});
    }
    	    
    	    // === 新增：對 filtered 做分頁切割 ===
    	    int totalPages = PageUtils.calculateTotalPages(filtered.size(), PAGE_SIZE);
    	    if (currentPage < 1) {
    	        currentPage = 1;
    	    } else if (currentPage > totalPages) {
    	        currentPage = totalPages;
    	    }

    	    int fromIndex = (currentPage - 1) * PAGE_SIZE;
    	    int toIndex = Math.min(fromIndex + PAGE_SIZE, filtered.size());
    	    List<Registration> pageData = fromIndex < toIndex  
    	            ? filtered.subList(fromIndex, toIndex) // subList頭包含尾不包含
    	            : new ArrayList<>(); // 如果完全沒資料, 回傳空清單
    	    
    	    
    	    model.addAttribute("regisListData", pageData); //把切好的這一頁資料放進model
    	    model.addAttribute("currentPage", currentPage);
    	    model.addAttribute("totalPages", totalPages);
    	    model.addAttribute("currentTab", tab);
    	    model.addAttribute("qs", "tab=" + tab);
    	    
    		
    		
    		Map<Integer, String> reportedActivityMap = reportSvc.getReportedActivityMap(member);
    	    model.addAttribute("reportedActivityMap", reportedActivityMap);
    		
    		return "front-end/member/activity/registration/myRegistrations";
    }
    
    // 二、送出報名
    @PostMapping("register")
    public String register(@RequestParam("activityId") Integer activityId,
    							@RequestParam("motivation") String motivation,
    							@RequestParam(value = "redirectUrl", defaultValue = "/member/activity/listAllActivity") String redirectUrl,
    							@AuthenticationPrincipal MemberUserDetails userDetails,
                           RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "請先登入");
            return "redirect:/front-end/login";
        }
    	
    		Member member = userDetails.getMember();
    		
    		 // 防護:只接受站內路徑,擋掉被竄改成外部網址的情況
    	    if (!redirectUrl.startsWith("/") || redirectUrl.startsWith("//")) {
    	        redirectUrl = "/member/activity/listAllActivity";
    	    }
    		
    		Activity activity = activitySvc.getOneActivity(activityId);
    		if (activity == null) {
    			redirectAttributes.addFlashAttribute("errorMessage", "活動不存在");
    			 return "redirect:" + redirectUrl;  
    		}
    		
    		try {
    		    regisSvc.register(member, activity, motivation);
    		    redirectAttributes.addFlashAttribute("successMessage", "報名成功,等待審核");
    		    return "redirect:/member/activity/registration/myRegistrations";
    		} catch (IllegalArgumentException | IllegalStateException e) {
    		    redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
    		    return "redirect:" + redirectUrl;
    		} catch (ConstraintViolationException e) {
    		    redirectAttributes.addFlashAttribute("errorMessage", "報名資料格式有誤,請確認填寫內容");
    		    return "redirect:" + redirectUrl;
    		}
    }
    
    // 三、取消報名
    @PostMapping("cancel")
    public String cancel(@RequestParam("regisId") Integer regisId,
                         @RequestParam("cancelReason") Integer cancelReason,
                         @RequestParam("cancelNote") String cancelNote,
                         @RequestParam(value = "tab", required = false) String tab,
                         @RequestParam(value = "page", required = false) Integer page,
                         @AuthenticationPrincipal MemberUserDetails userDetails,
                         RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "請先登入");
            return "redirect:/front-end/login";
        }
    	
    		try {
    			regisSvc.cancel(regisId, cancelReason, cancelNote, userDetails.getMember());
    			redirectAttributes.addFlashAttribute("successMessage", "已取消報名");
    		} catch (IllegalArgumentException | IllegalStateException e) {
    			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
    		}
    		String t = (tab != null) ? tab : "upcoming";
    	    int p = (page != null) ? page : 1;
    	    return "redirect:/member/activity/registration/myRegistrations?tab=" + t + "&page=" + p;
    }
    
    // 四、活動報名名單(給發起人檢視)
    @GetMapping("activityRegistrations")
    public String activityRegistrations(@RequestParam("activityId") Integer activityId,
    										@RequestParam(value = "tab", defaultValue = "pending") String tab,
    										@RequestParam(value = "page", defaultValue = "1") Integer page,
                                         @AuthenticationPrincipal MemberUserDetails userDetails,
                                         ModelMap model,
                                         RedirectAttributes redirectAttributes) {
    		if (userDetails == null) {
            return "redirect:/front-end/login";
        }
    	
    	    Activity activity = activitySvc.getOneActivity(activityId);
    	    List<Registration> allList;
    	    try {
    	        allList = regisSvc.getRegistrationsByActivity(activity, userDetails.getMember());
    	    } catch (IllegalArgumentException | IllegalStateException e) {
    	        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
    	        return "redirect:/member/activity/ownedActivities";
    	    }
    		
    	    List<Registration> filtered = new ArrayList<>();
    	    for (Registration r : allList) {
    	        int status = r.getRegisStatus();
    	        if ("pending".equals(tab) && status == 0) {
    	            filtered.add(r);
    	        } else if ("confirmed".equals(tab) && status == 1) {
    	            filtered.add(r);
    	        } else if ("waitlist".equals(tab) && status == 4) {
    	            filtered.add(r);
    	        } else if ("closed".equals(tab) && (status == 2 || status == 3)) {
    	            filtered.add(r);
    	        }
    	    }

    		
    		int totalPages = PageUtils.calculateTotalPages(filtered.size(), PAGE_SIZE);
    		Integer currentPage = page;
    		if (currentPage < 1) {
    	        currentPage = 1;
    	    } else if (currentPage > totalPages) {
    	        currentPage = totalPages;
    	    }

    	    int fromIndex = (currentPage - 1) * PAGE_SIZE;
    	    int toIndex = Math.min(fromIndex + PAGE_SIZE, filtered.size());
    	    List<Registration> pageData = fromIndex < toIndex 
    	            ? filtered.subList(fromIndex, toIndex) 
    	            : new ArrayList<>();
    		
    	    model.addAttribute("regisListData", pageData);
    	    model.addAttribute("activity", activity);
    	    model.addAttribute("currentPage", currentPage);
    	    model.addAttribute("totalPages", totalPages);
    	    model.addAttribute("currentTab", tab);
    	    model.addAttribute("qs", "activityId=" + activityId + "&tab=" + tab);
    		
        return "front-end/member/activity/registration/activityRegistrations";
    }
    
    // 五、審核活動報名(發起人)
    @PostMapping("approve")
    public String approve(@RequestParam("regisId") Integer regisId,
    						  @RequestParam("activityId") Integer activityId, 
    						  @RequestParam(value = "tab", required = false) String tab,
    						  @RequestParam(value = "page", required = false) Integer page,
    						  @AuthenticationPrincipal MemberUserDetails userDetails,
                          RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "請先登入");
            return "redirect:/front-end/login";
        }    	
    	
		try {
			regisSvc.approve(regisId, userDetails.getMember());
			redirectAttributes.addFlashAttribute("successMessage", "審核完成");
		} catch (IllegalArgumentException | IllegalStateException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
		}
		String t = (tab != null) ? tab : "pending";
		int p = (page != null) ? page : 1;
		return "redirect:/member/activity/registration/activityRegistrations?activityId=" + activityId + "&tab=" + t + "&page=" + p;
	}
    
    // 六、拒絕活動報名(發起人)
    @PostMapping("reject")
    public String reject(@RequestParam("regisId") Integer regisId,
                          @RequestParam("activityId") Integer activityId,
                          @RequestParam("rejectReason") Integer rejectReason,
                          @RequestParam("rejectNote") String rejectNote,
                          @RequestParam(value = "tab", required = false) String tab,
                          @RequestParam(value = "page", required = false) Integer page,
                          @AuthenticationPrincipal MemberUserDetails userDetails,
                          RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "請先登入");
            return "redirect:/front-end/login";
        }

        try {
            regisSvc.reject(regisId, rejectReason, rejectNote, userDetails.getMember());
            redirectAttributes.addFlashAttribute("successMessage", "已拒絕此報名申請");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        String t = (tab != null) ? tab : "pending";
        int p = (page != null) ? page : 1;
        return "redirect:/member/activity/registration/activityRegistrations?activityId=" + activityId + "&tab=" + t + "&page=" + p;
    }
    
    // 七、備取遞補為正取(發起人)
    @PostMapping("promote")
    public String promote(@RequestParam("regisId") Integer regisId,
                           @RequestParam("activityId") Integer activityId,
                           @RequestParam(value = "tab", required = false) String tab,
                           @RequestParam(value = "page", required = false) Integer page,
                           @AuthenticationPrincipal MemberUserDetails userDetails,
                           RedirectAttributes redirectAttributes) {
        if (userDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "請先登入");
            return "redirect:/front-end/login";
        }

        try {
            regisSvc.promoteToConfirmed(regisId, userDetails.getMember());
            redirectAttributes.addFlashAttribute("successMessage", "已將此筆報名遞補為正取");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        String t = (tab != null) ? tab : "pending";
        int p = (page != null) ? page : 1;
        return "redirect:/member/activity/registration/activityRegistrations?activityId=" + activityId + "&tab=" + t + "&page=" + p;
    }
    
    
    // 八、填寫評論
    @PostMapping("review")
    public String review(@RequestParam("regisId") Integer regisId,
                         @RequestParam("rating") Integer rating,
                         @RequestParam("reviewContent") String reviewContent,
                         @RequestParam(value = "tab", required = false) String tab,
                         @RequestParam(value = "page", required = false) Integer page,
                         @AuthenticationPrincipal MemberUserDetails userDetails,
                         RedirectAttributes redirectAttributes) {
    		if (userDetails == null) {
            redirectAttributes.addFlashAttribute("errorMessage", "請先登入");
            return "redirect:/front-end/login";
        }
    	
    		try {
    			regisSvc.review(regisId, rating, reviewContent, userDetails.getMember());
    			redirectAttributes.addFlashAttribute("successMessage", "已送出評論");
    		} catch (IllegalArgumentException | IllegalStateException e) {
    			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
    		}
    		String t = (tab != null) ? tab : "upcoming";
    		int p = (page != null) ? page : 1;
    		return "redirect:/member/activity/registration/myRegistrations?tab=" + t + "&page=" + p;
    }
}