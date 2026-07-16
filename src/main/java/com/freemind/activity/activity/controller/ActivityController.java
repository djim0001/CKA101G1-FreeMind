package com.freemind.activity.activity.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.freemind.activity.activity.model.Activity;
import com.freemind.activity.activity.model.ActivityService;
import com.freemind.activity.category.model.ActivityCat;
import com.freemind.activity.category.model.ActivityCatService;
import com.freemind.activity.follow.model.ActivityFollowService;
import com.freemind.activity.registration.model.RegistrationService;
import com.freemind.login.security.membersecurity.MemberUserDetails;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/member/activity")
public class ActivityController {

    @Autowired
    private ActivityService activitySvc;
    
    @Autowired
    private ActivityCatService activityCatSvc;
    
    @Autowired
    private RegistrationService regisSvc;
    
    @Autowired
    private ActivityFollowService followSvc;

    @GetMapping("listAllActivity")
    public String listAllActivity(ModelMap model) {
        Map<String, String[]> emptyMap = new HashMap<>();
        Integer currentPage = 1;

        List<Activity> list = activitySvc.getAllForMember(emptyMap, currentPage);
        model.addAttribute("activityListData", list);
        model.addAttribute("currentPage", currentPage);

        long total = activitySvc.getTotalCountForMember(emptyMap);
        int pageSize = 3;
        int totalPages = (int) (total % pageSize == 0 ? (total / pageSize) : (total / pageSize + 1));
        if (totalPages == 0) {
            totalPages = 1;
        }
        model.addAttribute("totalPages", totalPages);

        return "front-end/member/activity/listAllActivity";
    }
    
    @PostMapping("listActivities_ByCompositeQuery")  // 這次使用者送出的整個HTTP請求
    public String listActivities_ByCompositeQuery(HttpServletRequest req,
    						@RequestParam(value = "currentPage", required = false) Integer currentPage,
    						ModelMap model) {
        if (currentPage == null) {
            currentPage = 1;
        }
        
        // 把使用者這次送出的所有表單欄位，全部轉換成一個Map<String, String[]>
    		Map<String, String[]> map = req.getParameterMap();
        List<Activity> list = activitySvc.getAllForMember(map, currentPage);
        model.addAttribute("activityListData", list);
        model.addAttribute("currentPage", currentPage);
        
        long total = activitySvc.getTotalCountForMember(map);
        int pageSize = 3;
        int totalPages = (int) (total % pageSize == 0 ? (total / pageSize) : (total / pageSize + 1));
        if (totalPages == 0) {
            totalPages = 1;
        }
        model.addAttribute("totalPages", totalPages);
        
        if (list.isEmpty()) {
            model.addAttribute("errorMessage", "查無符合條件的活動");
        }
        
        return "front-end/member/activity/listAllActivity";
    }
    
    @GetMapping("addActivity")
    public String addActivity(ModelMap model) {
        Activity activity = new Activity();
        model.addAttribute("activity", activity);
        return "front-end/member/activity/addActivity";
    }
     
    @PostMapping("insert")
    public String insert(@Valid Activity activity, BindingResult result,
                          @RequestParam("pictureFile") MultipartFile pictureFile,
                          @AuthenticationPrincipal MemberUserDetails userDetails,
                          ModelMap model) throws IOException {
        if (result.hasErrors()) {
            return "front-end/member/activity/addActivity";
        }

        activity.setMember(userDetails.getMember());

        // 再把圖片設定好
        if (!pictureFile.isEmpty()) {
            activity.setPicture(pictureFile.getBytes());
        }

        // 資料都準備齊全了，才呼叫一次Service
        try {
            activitySvc.addActivity(activity);
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "front-end/member/activity/addActivity";
        }

        model.addAttribute("success", "- (新增成功，待審核)");
        return "front-end/member/activity/addActivitySuccess";
    }
    
    @ModelAttribute("activityCatListData")
    public List<ActivityCat> activityCatListData() {
        return activityCatSvc.getAll();
    }
    
    @PostMapping("getOne_For_Update")
    public String getOneForUpdate(@RequestParam("activityId") Integer activityId, ModelMap model) {
        
    	Activity activity = activitySvc.getOneActivity(activityId);
        model.addAttribute("activity", activity);
        return "front-end/member/activity/update_activity_input";
    }
    
    @PostMapping("update")
    public String update(@Valid Activity activity, BindingResult result,
                          @RequestParam("pictureFile") MultipartFile pictureFile,
                          @AuthenticationPrincipal MemberUserDetails userDetails,
                          ModelMap model) throws IOException {
        if (result.hasErrors()) {
            return "front-end/member/activity/update_activity_input";
        }

        if (!pictureFile.isEmpty()) {
            activity.setPicture(pictureFile.getBytes());
        }

        try {
        		Activity dbActivity = activitySvc.getOneActivity(activity.getActivityId());
            if (!dbActivity.getMember().getMemberId().equals(userDetails.getMember().getMemberId())) {
                throw new IllegalStateException("無權操作此活動");
            }
            activitySvc.updateActivity(activity);
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "front-end/member/activity/update_activity_input";
        }

        Integer memberId = userDetails.getMember().getMemberId();
        Map<String, String[]> emptyMap = new HashMap<>();
        Integer currentPage = 1;
        List<Activity> list = activitySvc.getAllForOwner(emptyMap, memberId, currentPage);
        model.addAttribute("activityListData", list);
        model.addAttribute("currentPage", currentPage);

        long total = activitySvc.getTotalCountForOwner(emptyMap, memberId);
        int pageSize = 3;
        int totalPages = (int) (total % pageSize == 0 ? (total / pageSize) : (total / pageSize + 1));
        if (totalPages == 0) {
            totalPages = 1;
        }
        model.addAttribute("totalPages", totalPages);

        model.addAttribute("success", "- (修改成功)");
        return "front-end/member/activity/ownedActivities";
    }
    
    // 查看我發起的活動
    @GetMapping("ownedActivities")
    public String ownedActivities(@AuthenticationPrincipal MemberUserDetails userDetails, ModelMap model) {
    		Integer memberId = userDetails.getMember().getMemberId();
        
        Map<String, String[]> emptyMap = new HashMap<>();
        Integer currentPage = 1;
        
        List<Activity> list = activitySvc.getAllForOwner(emptyMap, memberId, currentPage);
        model.addAttribute("activityListData", list);
        model.addAttribute("currentPage", currentPage);
        
        long total = activitySvc.getTotalCountForOwner(emptyMap, memberId);
        int pageSize = 3;
        int totalPages = (int) (total % pageSize == 0 ? (total / pageSize) : (total / pageSize + 1));
        if (totalPages == 0) {
            totalPages = 1;
        }
        model.addAttribute("totalPages", totalPages);
        return "front-end/member/activity/ownedActivities";
    }
 

    @PostMapping("ownedActivities_search")
    public String ownedActivitiesSearch(HttpServletRequest req,
    				@RequestParam(value = "currentPage", required = false) Integer currentPage, 
    				@AuthenticationPrincipal MemberUserDetails userDetails,
    				ModelMap model) {
    		Integer memberId = userDetails.getMember().getMemberId();
        
        if (currentPage == null) {
            currentPage = 1;
        }
        
        Map<String, String[]> map = req.getParameterMap();
        List<Activity> list = activitySvc.getAllForOwner(map, memberId, currentPage);
        model.addAttribute("activityListData", list);
        model.addAttribute("currentPage", currentPage);
        
        long total = activitySvc.getTotalCountForOwner(map, memberId);
        int pageSize = 3;
        int totalPages = (int) (total % pageSize == 0 ? (total / pageSize) : (total / pageSize + 1));
        if (totalPages == 0) {
            totalPages = 1;
        }
        model.addAttribute("totalPages", totalPages);
        
        if (list.isEmpty()) {
            model.addAttribute("errorMessage", "查無符合條件的活動");
        }
        return "front-end/member/activity/ownedActivities";
    }
  
    @PostMapping("cancel")
    public String cancel(@RequestParam("activityId") Integer activityId,
                         @RequestParam("cancelNote") String cancelNote,
                         @AuthenticationPrincipal MemberUserDetails userDetails,
                         ModelMap model) {
        try {
            // 先驗身分:這個活動是不是我的?
            Activity activity = activitySvc.getOneActivity(activityId);
            if (!activity.getMember().getMemberId().equals(userDetails.getMember().getMemberId())) {
                throw new IllegalStateException("無權操作此活動");
            }
            activitySvc.cancelActivity(activityId, cancelNote);
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }

        Integer memberId = userDetails.getMember().getMemberId();
        Map<String, String[]> emptyMap = new HashMap<>();
        Integer currentPage = 1;
        List<Activity> list = activitySvc.getAllForOwner(emptyMap, memberId, currentPage);
        model.addAttribute("activityListData", list);
        model.addAttribute("currentPage", currentPage);
        
        long total = activitySvc.getTotalCountForOwner(emptyMap, memberId);
        int pageSize = 3;
        int totalPages = (int) (total % pageSize == 0 ? (total / pageSize) : (total / pageSize + 1));
        if (totalPages == 0) {
            totalPages = 1;
        }
        model.addAttribute("totalPages", totalPages);
        
        return "front-end/member/activity/ownedActivities";
    }
    
    @PostMapping("postpone")
    public String postpone(@RequestParam("activityId") Integer activityId,
                            @RequestParam("postponeNote") String postponeNote,
                            @AuthenticationPrincipal MemberUserDetails userDetails,
                            ModelMap model) {
        try {
            Activity activity = activitySvc.getOneActivity(activityId);
            if (!activity.getMember().getMemberId().equals(userDetails.getMember().getMemberId())) {
                throw new IllegalStateException("無權操作此活動");
            }
            activitySvc.postponeActivity(activityId, postponeNote);
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }

        Integer memberId = userDetails.getMember().getMemberId();
        Map<String, String[]> emptyMap = new HashMap<>();
        Integer currentPage = 1;
        List<Activity> list = activitySvc.getAllForOwner(emptyMap, memberId, currentPage);
        model.addAttribute("activityListData", list);
        model.addAttribute("currentPage", currentPage);
        
        long total = activitySvc.getTotalCountForOwner(emptyMap, memberId);
        int pageSize = 3;
        int totalPages = (int) (total % pageSize == 0 ? (total / pageSize) : (total / pageSize + 1));
        if (totalPages == 0) {
            totalPages = 1;
        }
        model.addAttribute("totalPages", totalPages);
        
        return "front-end/member/activity/ownedActivities";
    }
    
    @PostMapping("confirmNewSchedule")
    public String confirmNewSchedule(@RequestParam("activityId") Integer activityId,
                                      @RequestParam("activityStart") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime activityStart,
                                      @RequestParam("activityEnd") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime activityEnd,
                                      @RequestParam(value = "regisStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime regisStart,
                                      @RequestParam(value = "regisEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime regisEnd,
                                      @AuthenticationPrincipal MemberUserDetails userDetails,
                                      ModelMap model) {
        try {
            Activity activity = activitySvc.getOneActivity(activityId);
            if (!activity.getMember().getMemberId().equals(userDetails.getMember().getMemberId())) {
                throw new IllegalStateException("無權操作此活動");
            }
            activitySvc.confirmNewSchedule(activityId, activityStart, activityEnd, regisStart, regisEnd);
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }

        Integer memberId = userDetails.getMember().getMemberId();
        Map<String, String[]> emptyMap = new HashMap<>();
        Integer currentPage = 1;
        List<Activity> list = activitySvc.getAllForOwner(emptyMap, memberId, currentPage);
        model.addAttribute("activityListData", list);
        model.addAttribute("currentPage", currentPage);
        
        long total = activitySvc.getTotalCountForOwner(emptyMap, memberId);
        int pageSize = 3;
        int totalPages = (int) (total % pageSize == 0 ? (total / pageSize) : (total / pageSize + 1));
        if (totalPages == 0) {
            totalPages = 1;
        }
        model.addAttribute("totalPages", totalPages);
        
        return "front-end/member/activity/ownedActivities";
    }
    
    @GetMapping("activityImage")
    public void activityImage(@RequestParam("activityId") Integer activityId, HttpServletResponse res)
            throws IOException {
        res.setContentType("image/jpeg");
        ServletOutputStream out = res.getOutputStream();

        Activity activity = activitySvc.getOneActivity(activityId);

        if (activity != null && activity.getPicture() != null) {
            out.write(activity.getPicture());
        }
    }
    
    @GetMapping("listOneActivity")
    public String listOneActivity(@RequestParam("activityId") Integer activityId,
    								@AuthenticationPrincipal MemberUserDetails userDetails,
    								ModelMap model) {
        Activity activity = activitySvc.getOneActivity(activityId);
        
        // 訪客不查關注狀態
        if (userDetails != null) {
            model.addAttribute("isFollowing",
                followSvc.isFollowing(userDetails.getMember().getMemberId(), activityId));
        }
        
        model.addAttribute("activity", activity);
        model.addAttribute("reviewListData", regisSvc.getReviewsByActivity(activity));
        return "front-end/member/activity/listOneActivity";
    }
    
    // 活動模組首頁
    @GetMapping("/activityIndex")
    public String activityIndex() {
        return "front-end/member/activity/activityIndex";
    }
    
    @GetMapping("select_page")
    public String selectPage(ModelMap model) {
        return "front-end/member/activity/select_page";
    }
}
