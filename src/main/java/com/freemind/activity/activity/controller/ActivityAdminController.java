package com.freemind.activity.activity.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.activity.activity.model.Activity;
import com.freemind.activity.activity.model.ActivityService;
import com.freemind.activity.util.PageUtils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/admin/activity")
public class ActivityAdminController {

    @Autowired
    ActivityService activitySvc;
    
    private static final int PAGE_SIZE = 3;

    // 後台查詢
    @GetMapping("listAllActivity")
    public String listAllActivity(ModelMap model) {
        Map<String, String[]> emptyMap = new HashMap<>();
        Integer currentPage = 1;

        List<Activity> list = activitySvc.getAllForAdmin(emptyMap, currentPage);
        model.addAttribute("activityListData", list);
        model.addAttribute("currentPage", currentPage);

        long total = activitySvc.getTotalCountForAdmin(emptyMap);
        model.addAttribute("totalPages", PageUtils.calculateTotalPages(total, PAGE_SIZE));

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
        model.addAttribute("totalPages", PageUtils.calculateTotalPages(total, PAGE_SIZE));

        if (list.isEmpty()) {
            model.addAttribute("errorMessage", "查無符合條件的活動");
        }

        return "back-end/activity/activity/listAllActivity";
    }
    
    
    // 後台審核申請活動
    @PostMapping("approve")
    public String approve(@RequestParam("activityId") Integer activityId,
                           RedirectAttributes redirectAttributes) {
        try {
            activitySvc.approveActivity(activityId);
            redirectAttributes.addFlashAttribute("successMessage", "審核通過");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/activity/listOneActivity?activityId=" + activityId;
    }
    
    // 後台退回申請活動
    @PostMapping("reject")
    public String reject(@RequestParam("activityId") Integer activityId,
                          @RequestParam("rejectReason") Integer rejectReason,
                          @RequestParam("rejectNote") String rejectNote,
                          RedirectAttributes redirectAttributes) {
        try {
            activitySvc.rejectActivity(activityId, rejectReason, rejectNote);
            redirectAttributes.addFlashAttribute("successMessage", "退回成功");
        } catch (RuntimeException ex) {
        		redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/activity/listOneActivity?activityId=" + activityId;
    }
    
    // 後台手動發布活動
    @PostMapping("publish")
    public String publish(@RequestParam("activityId") Integer activityId,
    		 				 RedirectAttributes redirectAttributes) {
        try {
            activitySvc.publishActivity(activityId);
            redirectAttributes.addFlashAttribute("successMessage", "發布成功");
        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/admin/activity/listOneActivity?activityId=" + activityId;
    }
    
    // 後台導覽頁
    @GetMapping("activityAdminIndex")
    public String activityAdminIndex() {
        return "back-end/activity/activityAdminIndex";
    }
    
    // 圖片
    @GetMapping("activityImage")
    public void activityImage(@RequestParam("activityId") Integer activityId, HttpServletResponse res)
            throws IOException {
        res.setContentType("image/jpeg");
        Activity activity = activitySvc.getOneActivity(activityId);
     

        if (activity == null || activity.getPicture() == null) {
            return;
        }

        String uploadDir = System.getProperty("user.dir") + "/uploads/activity-images/";
        File imageFile = new File(uploadDir, activity.getPicture());

        if (!imageFile.exists()) {
            return;
        }
        res.setContentType("image/jpeg");
        Files.copy(imageFile.toPath(), res.getOutputStream());
    }
    
    // 詳情
    @GetMapping("listOneActivity")
    public String listOneActivity(@RequestParam("activityId") Integer activityId, ModelMap model) {
        Activity activity = activitySvc.getOneActivity(activityId);
        model.addAttribute("activity", activity);
        return "back-end/activity/activity/listOneActivity";   
    }
    
}