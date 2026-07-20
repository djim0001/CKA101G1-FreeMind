package com.freemind.activity.activity.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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
import com.freemind.activity.registration.model.RegistrationService;
import com.freemind.activity.report.model.ActivityReportService;
import com.freemind.activity.util.PageUtils;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/admin/activity")
public class ActivityAdminController {

    @Autowired
    private ActivityService activitySvc;
    
    @Autowired
    private RegistrationService regisSvc;
    
    @Autowired
    private ActivityReportService reportSvc;
    
    private static final int PAGE_SIZE = 3;

    // 後台活動列表(唯一入口)
    @GetMapping("listActivities_ByCompositeQuery")
    public String listActivities_ByCompositeQuery(HttpServletRequest req,
                        @RequestParam(value = "page", required = false) Integer page,
                        ModelMap model) {
        Integer currentPage = (page == null) ? 1 : page;

        Map<String, String[]> map = req.getParameterMap();
        List<Activity> list = activitySvc.getAllForAdmin(map, currentPage);
        model.addAttribute("activityListData", list);
        model.addAttribute("pendingCountMap", regisSvc.getPendingCountMap(list));
        model.addAttribute("currentPage", currentPage);

        long total = activitySvc.getTotalCountForAdmin(map);
        model.addAttribute("totalPages", PageUtils.calculateTotalPages(total, PAGE_SIZE));
        model.addAttribute("totalCount", total);

        if (list.isEmpty()) {
            model.addAttribute("errorMessage", "查無符合條件的活動");
        }

        model.addAttribute("qs", buildQueryString(map, "page"));
        model.addAttribute("countPending", activitySvc.countByStatus(0));      // 待審核
        model.addAttribute("countApproved", activitySvc.countByStatus(1));     // 已審核/待發布
        model.addAttribute("countPublished", activitySvc.countByStatus(2));    // 已發布
        model.addAttribute("countReportPending", reportSvc.countByStatus(0)); // 問題待處理
        model.addAttribute("countReportProcessing", reportSvc.countByStatus(1)); // 問題處理中
        model.addAttribute("countReportDone", reportSvc.countByStatus(2));    // 問題已處理
        return "back-end/activity/activity/listAllActivity";
    }
    
    // 查詢表單送出：只負責把條件組成query string，轉址到下面的GET方法
    @PostMapping("listActivities_ByCompositeQuery")
    public String searchActivities(HttpServletRequest req) {
        String qs = buildQueryString(req.getParameterMap(), "currentPage", "page");
        String redirectUrl = "redirect:/admin/activity/listActivities_ByCompositeQuery?page=1";
        if (!qs.isEmpty()) {
            redirectUrl += "&" + qs;
        }
        return redirectUrl;
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
//    @GetMapping("activityImage")
//    public void activityImage(@RequestParam("activityId") Integer activityId, HttpServletResponse res)
//            throws IOException {
//        res.setContentType("image/jpeg");
//        Activity activity = activitySvc.getOneActivity(activityId);
//     
//
//        if (activity == null || activity.getPicture() == null) {
//            return;
//        }
//
//        String uploadDir = System.getProperty("user.dir") + "/uploads/activity-images/";
//        File imageFile = new File(uploadDir, activity.getPicture());
//
//        if (!imageFile.exists()) {
//            return;
//        }
//        res.setContentType("image/jpeg");
//        Files.copy(imageFile.toPath(), res.getOutputStream());
//    }
    
    // 詳情
    @GetMapping("listOneActivity")
    public String listOneActivity(@RequestParam("activityId") Integer activityId, ModelMap model) {
        Activity activity = activitySvc.getOneActivity(activityId);
        
        long pendingCount = regisSvc.countPendingByActivity(activity);
        model.addAttribute("pendingCount", pendingCount);
        
        model.addAttribute("activity", activity);
        return "back-end/activity/activity/listOneActivity";   
    }
    
 // 排程發布
    @PostMapping("schedulePublish")
    public String schedulePublish(@RequestParam("activityId") Integer activityId,
                                   @RequestParam("scheduledPublishAt") String scheduledPublishAt,
                                   RedirectAttributes redirectAttributes) {
        try {
            LocalDateTime scheduledAt = LocalDateTime.parse(scheduledPublishAt);
            activitySvc.schedulePublishActivity(activityId, scheduledAt);
            redirectAttributes.addFlashAttribute("successMessage", "已設定排程發布");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "排程時間格式有誤");
        }
        return "redirect:/admin/activity/listOneActivity?activityId=" + activityId;
    }
    
 // 把Map<String, String[]>轉成query string，可指定要排除的欄位(例如page/currentPage)
    private String buildQueryString(Map<String, String[]> params, String... excludeKeys) {
        StringBuilder qs = new StringBuilder();
        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            String key = entry.getKey();
            boolean excluded = false;
            for (String ex : excludeKeys) {
                if (key.equals(ex)) {
                    excluded = true;
                    break;
                }
            }
            if (excluded) {
                continue;
            }
            for (String value : entry.getValue()) {
                if (value == null || value.isEmpty()) {
                    continue;
                }
                if (qs.length() > 0) {
                    qs.append("&");
                }
                qs.append(URLEncoder.encode(key, StandardCharsets.UTF_8))
                  .append("=")
                  .append(URLEncoder.encode(value, StandardCharsets.UTF_8));
            }
        }
        return qs.toString();
    }

    
}