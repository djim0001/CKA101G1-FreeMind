package com.freemind.activity.activity.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.freemind.activity.activity.model.Activity;
import com.freemind.activity.activity.model.ActivityService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin/activity")
public class ActivityAdminController {

    @Autowired
    ActivityService activitySvc;

    // 後台查詢
    @GetMapping("listAllActivity")
    public String listAllActivity(ModelMap model) {
        Map<String, String[]> emptyMap = new HashMap<>();
        Integer currentPage = 1;

        List<Activity> list = activitySvc.getAllForAdmin(emptyMap, currentPage);
        model.addAttribute("activityListData", list);
        model.addAttribute("currentPage", currentPage);

        long total = activitySvc.getTotalCountForAdmin(emptyMap);
        int pageSize = 3;
        int totalPages = (int) (total % pageSize == 0 ? (total / pageSize) : (total / pageSize + 1));
        if (totalPages == 0) {
            totalPages = 1;
        }
        model.addAttribute("totalPages", totalPages);

        return "back-end/activity/activity/listAllActivity";
    }

    @PostMapping("listActivities_ByCompositeQuery")
    public String listActivities_ByCompositeQuery(HttpServletRequest req,
                        @RequestParam(value = "currentPage", required = false) Integer currentPage,
                        ModelMap model) {
        if (currentPage == null) {
            currentPage = 1;
        }

        Map<String, String[]> map = req.getParameterMap();
        List<Activity> list = activitySvc.getAllForAdmin(map, currentPage);
        model.addAttribute("activityListData", list);
        model.addAttribute("currentPage", currentPage);

        long total = activitySvc.getTotalCountForAdmin(map);
        int pageSize = 3;
        int totalPages = (int) (total % pageSize == 0 ? (total / pageSize) : (total / pageSize + 1));
        if (totalPages == 0) {
            totalPages = 1;
        }
        model.addAttribute("totalPages", totalPages);

        if (list.isEmpty()) {
            model.addAttribute("errorMessage", "查無符合條件的活動");
        }

        return "back-end/activity/activity/listAllActivity";
    }
    
    
    // 後台審核申請活動
    @PostMapping("approve")
    public String approve(@RequestParam("activityId") String activityId, ModelMap model) {
        try {
            activitySvc.approveActivity(Integer.valueOf(activityId));
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }
        
        Map<String, String[]> emptyMap = new HashMap<>();
        Integer currentPage = 1;
        List<Activity> list = activitySvc.getAllForAdmin(emptyMap, currentPage);
        model.addAttribute("activityListData", list);
        model.addAttribute("currentPage", currentPage);
        
        long total = activitySvc.getTotalCountForAdmin(emptyMap);
        int pageSize = 3;
        int totalPages = (int) (total % pageSize == 0 ? (total / pageSize) : (total / pageSize + 1));
        if (totalPages == 0) {
            totalPages = 1;
        }
        model.addAttribute("totalPages", totalPages);
        
        return "back-end/activity/activity/listAllActivity";
    }
    
    // 後台退回申請活動
    @PostMapping("reject")
    public String reject(@RequestParam("activityId") String activityId,
                          @RequestParam("rejectReason") Integer rejectReason,
                          @RequestParam("rejectNote") String rejectNote,
                          ModelMap model) {
        try {
            activitySvc.rejectActivity(Integer.valueOf(activityId), rejectReason, rejectNote);
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }

        Map<String, String[]> emptyMap = new HashMap<>();
        Integer currentPage = 1;
        List<Activity> list = activitySvc.getAllForAdmin(emptyMap, currentPage);
        model.addAttribute("activityListData", list);
        model.addAttribute("currentPage", currentPage);
        
        long total = activitySvc.getTotalCountForAdmin(emptyMap);
        int pageSize = 3;
        int totalPages = (int) (total % pageSize == 0 ? (total / pageSize) : (total / pageSize + 1));
        if (totalPages == 0) {
            totalPages = 1;
        }
        model.addAttribute("totalPages", totalPages);
        
        return "back-end/activity/activity/listAllActivity";
    }
    
    // 後台手動發布活動
    @PostMapping("publish")
    public String publish(@RequestParam("activityId") String activityId, ModelMap model) {
        try {
            activitySvc.publishActivity(Integer.valueOf(activityId));
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
        }

        Map<String, String[]> emptyMap = new HashMap<>();
        Integer currentPage = 1;
        List<Activity> list = activitySvc.getAllForAdmin(emptyMap, currentPage);
        model.addAttribute("activityListData", list);
        model.addAttribute("currentPage", currentPage);
        
        long total = activitySvc.getTotalCountForAdmin(emptyMap);
        int pageSize = 3;
        int totalPages = (int) (total % pageSize == 0 ? (total / pageSize) : (total / pageSize + 1));
        if (totalPages == 0) {
            totalPages = 1;
        }
        model.addAttribute("totalPages", totalPages);
        
        
        return "back-end/activity/activity/listAllActivity";
    }
    
    
}