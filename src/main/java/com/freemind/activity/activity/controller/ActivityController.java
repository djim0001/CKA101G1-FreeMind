package com.freemind.activity.activity.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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

import jakarta.servlet.http.HttpServletRequest;
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
        model.addAttribute("activityListData", null);  // 一開始還沒查詢，先給空的
        return "front-end/activity/listAllActivity";
    }
    
    @PostMapping("listActivities_ByCompositeQuery")  // 這次使用者送出的整個HTTP請求
    public String listActivities_ByCompositeQuery(HttpServletRequest req, ModelMap model) {
        // 把使用者這次送出的所有表單欄位，全部轉換成一個Map<String, String[]>
    		Map<String, String[]> map = req.getParameterMap();
        List<Activity> list = activitySvc.getAllForMember(map);
        model.addAttribute("activityListData", list);
        
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

        // 簡化處理：暫時用傳入的memberId，之後要改成從登入狀態取得
        Member member = new Member();
        member.setMemberId(memberId);
        activity.setMember(member);

        if (!pictureFile.isEmpty()) {
            activity.setPicture(pictureFile.getBytes());
        }

        activitySvc.addActivity(activity);
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
    		System.out.println("=== 除錯：picture是否為空 = " + pictureFile.isEmpty());
        System.out.println("=== 除錯：picture檔名 = " + pictureFile.getOriginalFilename());
    	
    		if (result.hasErrors()) {
    			result.getAllErrors().forEach(error -> System.out.println("=== 驗證錯誤: " + error.toString()));
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
        List<Activity> list = activitySvc.getAll();
        model.addAttribute("activityListData", list);
        model.addAttribute("success", "- (修改成功)");
        return "front-end/activity/listAllActivity";
    }
    
    // 查看我發起的活動
    @GetMapping("ownedActivities")
    public String ownedActivities(ModelMap model) {
        // 簡化處理：暫時手動指定memberId，之後改成從登入狀態取得
        Integer memberId = 1;  // 之後要換成真正登入者的ID
        
        Map<String, String[]> emptyMap = new HashMap<>();
        List<Activity> list = activitySvc.getAllForOwner(emptyMap, memberId);
        model.addAttribute("activityListData", list);
        return "front-end/activity/ownedActivities";
    }

    @PostMapping("ownedActivities_search")
    public String ownedActivitiesSearch(HttpServletRequest req, ModelMap model) {
        Integer memberId = 1;  // 之後要換成真正登入者的ID
        
        Map<String, String[]> map = req.getParameterMap();
        List<Activity> list = activitySvc.getAllForOwner(map, memberId);
        model.addAttribute("activityListData", list);
        return "front-end/activity/ownedActivities";
    }
    
}
