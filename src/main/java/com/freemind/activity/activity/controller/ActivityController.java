package com.freemind.activity.activity.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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
import com.freemind.login.member.model.Member;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/activity")
public class ActivityController {

    @Autowired
    ActivityService activitySvc;
    
    @Autowired
    ActivityCatService activityCatSvc;

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

        return "front-end/activity/listAllActivity";
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
        
        return "front-end/activity/listAllActivity";
    }
    
    @GetMapping("addActivity")
    public String addActivity(ModelMap model) {
        Activity activity = new Activity();
        model.addAttribute("activity", activity);
        return "front-end/activity/addActivity";
    }
     
    @PostMapping("insert")
    public String insert(@Valid Activity activity, BindingResult result,
                          @RequestParam("memberId") Integer memberId,
                          @RequestParam("pictureFile") MultipartFile pictureFile,
                          ModelMap model) throws IOException {
        if (result.hasErrors()) {
            return "front-end/activity/addActivity";
        }

        // 先把member設定好
        Member member = new Member();
        member.setMemberId(memberId);
        activity.setMember(member);

        // 再把圖片設定好
        if (!pictureFile.isEmpty()) {
            activity.setPicture(pictureFile.getBytes());
        }

        // 資料都準備齊全了，才呼叫一次Service
        try {
            activitySvc.addActivity(activity);
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "front-end/activity/addActivity";
        }

        model.addAttribute("success", "- (新增成功，待審核)");
        return "front-end/activity/addActivitySuccess";
    }
    
    @ModelAttribute("activityCatListData")
    public List<ActivityCat> activityCatListData() {
        return activityCatSvc.getAll();
    }
    
    @PostMapping("getOne_For_Update")
    public String getOneForUpdate(@RequestParam("activityId") String activityId, ModelMap model) {
        
    	Activity activity = activitySvc.getOneActivity(Integer.valueOf(activityId));
        model.addAttribute("activity", activity);
        return "front-end/activity/update_activity_input";
    }
    
    @PostMapping("update")
    public String update(@Valid Activity activity, BindingResult result,
                          @RequestParam("pictureFile") MultipartFile pictureFile,
                          ModelMap model) throws IOException {
        if (result.hasErrors()) {
            return "front-end/activity/update_activity_input";
        }

        if (!pictureFile.isEmpty()) {
            activity.setPicture(pictureFile.getBytes());
        }

        try {
            activitySvc.updateActivity(activity);
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "front-end/activity/update_activity_input";
        }

        Integer memberId = 1;
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
        return "front-end/activity/ownedActivities";
    }
    
    // 查看我發起的活動
    @GetMapping("ownedActivities")
    public String ownedActivities(ModelMap model) {
        // 簡化處理：暫時手動指定memberId，之後改成從登入狀態取得
        Integer memberId = 1;  // 之後要換成真正登入者的ID
        
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
        return "front-end/activity/ownedActivities";
    }
 

    @PostMapping("ownedActivities_search")
    public String ownedActivitiesSearch(HttpServletRequest req,
    				@RequestParam(value = "currentPage", required = false) Integer currentPage, 
    				ModelMap model) {
        Integer memberId = 1;  // 之後要換成真正登入者的ID
        
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
        return "front-end/activity/ownedActivities";
    }
  
    @PostMapping("cancel")
    public String cancel(@RequestParam("activityId") String activityId,
                         @RequestParam("cancelNote") String cancelNote,
                         ModelMap model) {
        try {
            activitySvc.cancelActivity(Integer.valueOf(activityId), cancelNote);
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }

        Integer memberId = 1;  // 之後要換成真正登入者的ID
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
        
        return "front-end/activity/ownedActivities";
    }
    
    @PostMapping("postpone")
    public String postpone(@RequestParam("activityId") String activityId,
                            @RequestParam("postponeNote") String postponeNote,
                            ModelMap model) {
        try {
            activitySvc.postponeActivity(Integer.valueOf(activityId), postponeNote);
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }

        Integer memberId = 1;
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
        
        return "front-end/activity/ownedActivities";
    }
    
    @PostMapping("confirmNewSchedule")
    public String confirmNewSchedule(@RequestParam("activityId") String activityId,
                                      @RequestParam("activityStart") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime activityStart,
                                      @RequestParam("activityEnd") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime activityEnd,
                                      @RequestParam(value = "regisStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime regisStart,
                                      @RequestParam(value = "regisEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime regisEnd,
                                      ModelMap model) {
        try {
            activitySvc.confirmNewSchedule(Integer.valueOf(activityId), activityStart, activityEnd, regisStart, regisEnd);
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }

        Integer memberId = 1;
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
        
        return "front-end/activity/ownedActivities";
    }
    
    @GetMapping("activityImage")
    public void activityImage(@RequestParam("activityId") String activityId, HttpServletResponse res)
            throws IOException {
        res.setContentType("image/jpeg");
        ServletOutputStream out = res.getOutputStream();

        Activity activity = activitySvc.getOneActivity(Integer.valueOf(activityId));

        if (activity != null && activity.getPicture() != null) {
            out.write(activity.getPicture());
        }
    }
    
    @GetMapping("listOneActivity")
    public String listOneActivity(@RequestParam("activityId") String activityId, ModelMap model) {
        Activity activity = activitySvc.getOneActivity(Integer.valueOf(activityId));
        model.addAttribute("activity", activity);
        return "front-end/activity/listOneActivity";
    }
    
    // 活動模組首頁(記得前面要加斜線)
    @GetMapping("/activityIndex")
    public String activityIndex() {
        return "front-end/activity/activityIndex";
    }
    
    @GetMapping("select_page")
    public String selectPage(ModelMap model) {
        return "front-end/activity/select_page";
    }
}
