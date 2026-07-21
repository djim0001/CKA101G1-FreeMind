package com.freemind.activity.activity.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.activity.activity.model.Activity;
import com.freemind.activity.activity.model.ActivityService;
import com.freemind.activity.category.model.ActivityCat;
import com.freemind.activity.category.model.ActivityCatService;
import com.freemind.activity.follow.model.ActivityFollowService;
import com.freemind.activity.registration.model.Registration;
import com.freemind.activity.registration.model.RegistrationService;
import com.freemind.activity.util.PageUtils;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;
import com.freemind.login.security.membersecurity.MemberUserDetails;
import com.freemind.util.ImageUploadValidator;

import jakarta.servlet.http.HttpServletRequest;
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
    
    @Autowired
    private MemberService memberSvc;
    
    private static final int PAGE_SIZE = 3;
    
    @Value("${app.activity.image.max-size}")
    private DataSize maxImageSize;

    
    @ModelAttribute("member")
    public Member currentMember(Authentication authentication) {
        // 訪客（未登入或匿名）時不放 member 進 model
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return memberSvc.findByAccount(authentication.getName());
    }
    
 // 查詢表單送出：只負責把條件組成query string，轉址到下面的GET方法
    @PostMapping("listActivities_ByCompositeQuery")
    public String searchActivities(HttpServletRequest req) {
        String qs = buildQueryString(req.getParameterMap(), "currentPage", "page");
        String redirectUrl = "redirect:/member/activity/listActivities_ByCompositeQuery?page=1";
        if (!qs.isEmpty()) {
            redirectUrl += "&" + qs;
        }
        return redirectUrl;
    }

    // 活動列表(唯一入口)：無參數=顯示全部, 有查詢字串=依條件篩選
    @GetMapping("listActivities_ByCompositeQuery")
    public String listActivities_ByCompositeQuery(HttpServletRequest req,
                            @RequestParam(value = "page", required = false) Integer page,
                            @AuthenticationPrincipal MemberUserDetails userDetails,
                            ModelMap model) {
        Integer currentPage = (page == null) ? 1 : page;

        Map<String, String[]> map = req.getParameterMap();
        List<Activity> list = activitySvc.getAllForMember(map, currentPage);
        model.addAttribute("activityListData", list);
        model.addAttribute("pendingCountMap", regisSvc.getPendingCountMap(list));
        model.addAttribute("currentPage", currentPage);

        long total = activitySvc.getTotalCountForMember(map);
        model.addAttribute("totalPages", PageUtils.calculateTotalPages(total, PAGE_SIZE));
        model.addAttribute("totalCount", total);
        
        if (list.isEmpty()) {
            model.addAttribute("errorMessage", "查無符合條件的活動");
        }
        model.addAttribute("myRegisMap", buildMyRegistrationMap(userDetails));
        model.addAttribute("qs", buildQueryString(map, "page"));

        // 精選活動：沒有其他查詢條件時顯示
        boolean hasFilter = !map.isEmpty() && !(map.size() == 1 && map.containsKey("page"));
        if (!hasFilter) {
            model.addAttribute("featuredActivities", activitySvc.getFeaturedActivities(3));
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
    		if (userDetails == null) {
            return "redirect:/front-end/login";
        }
    	    		
    		LocalDateTime now = LocalDateTime.now();

    	    if (activity.getRegisStart() != null && !activity.getRegisStart().isAfter(now)) {
    	        result.rejectValue("regisStart", null, "報名開始時間，必須晚於現在時間");
    	    }
    	    if (activity.getRegisStart() != null && activity.getRegisEnd() != null
    	            && !activity.getRegisEnd().isAfter(activity.getRegisStart())) {
    	        result.rejectValue("regisEnd", null, "報名截止時間，必須晚於報名開始時間");
    	    }
    	    if (activity.getRegisEnd() != null && activity.getActivityStart() != null
    	            && !activity.getActivityStart().isAfter(activity.getRegisEnd())) {
    	        result.rejectValue("activityStart", null, "活動開始時間，必須晚於報名截止時間");
    	    }
    	    if (activity.getActivityStart() != null && activity.getActivityEnd() != null
    	            && !activity.getActivityEnd().isAfter(activity.getActivityStart())) {
    	        result.rejectValue("activityEnd", null, "活動結束時間，必須晚於活動開始時間");
    	    }
    		
    		if (result.hasErrors()) {
            return "front-end/member/activity/addActivity";
        }

        activity.setMember(userDetails.getMember());

        // 再把圖片設定好      
        if (!pictureFile.isEmpty()) {
            try {
                ImageUploadValidator.validateImageSize(pictureFile, maxImageSize);
            } catch (IllegalArgumentException ex) {
                model.addAttribute("errorMessage", ex.getMessage());
                return "front-end/member/activity/addActivity";
            }

            String savedFileName = saveUploadedPicture(pictureFile);
            activity.setPicture(savedFileName);
        }

        // 資料都準備齊全了，才呼叫一次Service
        try {
            activitySvc.addActivity(activity);
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "front-end/member/activity/addActivity";
        }

        return "redirect:/member/activity/insertSuccess";
    }
    
    @GetMapping("insertSuccess")
    public String insertSuccess() {
    	return "front-end/member/activity/addActivitySuccess";
    }
    
    @ModelAttribute("activityCatListData")
    public List<ActivityCat> activityCatListData() {
        return activityCatSvc.getAll();
    }
    
    
    @GetMapping("getOne_For_Update")
    public String getOneForUpdate(@RequestParam("activityId") Integer activityId, ModelMap model) {
        
    	Activity activity = activitySvc.getOneActivity(activityId);
        model.addAttribute("activity", activity);
        return "front-end/member/activity/update_activity_input";
    }
    
    @PostMapping("update")
    public String update(@Valid Activity activity, BindingResult result,
                          @RequestParam("pictureFile") MultipartFile pictureFile,
                          @AuthenticationPrincipal MemberUserDetails userDetails,
                          ModelMap model,
                          RedirectAttributes redirectAttributes) throws IOException {
    		if (userDetails == null) {
            return "redirect:/front-end/login";
        }
    		if (result.hasErrors()) {
            return "front-end/member/activity/update_activity_input";
        }

        String oldPictureFileName = null;
        if (!pictureFile.isEmpty()) {
        	 	try {
        	        ImageUploadValidator.validateImageSize(pictureFile, maxImageSize);
        	    } catch (IllegalArgumentException ex) {
        	        model.addAttribute("errorMessage", ex.getMessage());
        	        return "front-end/member/activity/update_activity_input";
        	    }
        	
            // 先記住(換照片前)資料庫裡原本的舊檔名，更新成功才刪除舊圖
            Activity existing = activitySvc.getOneActivity(activity.getActivityId());
            oldPictureFileName = existing.getPicture();

            String savedFileName = saveUploadedPicture(pictureFile);
            activity.setPicture(savedFileName);
        }
        
        
        try {
        		Activity dbActivity = activitySvc.getOneActivity(activity.getActivityId());
            if (!dbActivity.getMember().getMemberId().equals(userDetails.getMember().getMemberId())) {
                throw new IllegalStateException("無權操作此活動");
            }
            activitySvc.updateActivity(activity);
            
            // 更新成功後，才刪除舊圖片檔案
            if (oldPictureFileName != null) {
                deleteOldPicture(oldPictureFileName);
            }
        } catch (RuntimeException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "front-end/member/activity/update_activity_input";
        }

        redirectAttributes.addFlashAttribute("success", "修改成功");
        return "redirect:/member/activity/ownedActivities";
    }
    
    // 查看我發起的活動
    @GetMapping("ownedActivities")
    public String ownedActivities(HttpServletRequest req,
                        @RequestParam(value = "page", required = false) Integer page,
                        @AuthenticationPrincipal MemberUserDetails userDetails,
                        ModelMap model) {
        if (userDetails == null) {
            return "redirect:/front-end/login";
        }
        Integer memberId = userDetails.getMember().getMemberId();
        Integer currentPage = (page == null) ? 1 : page;

        Map<String, String[]> map = req.getParameterMap();
        List<Activity> list = activitySvc.getAllForOwner(map, memberId, currentPage);
        model.addAttribute("activityListData", list);
        model.addAttribute("currentPage", currentPage);

        long total = activitySvc.getTotalCountForOwner(map, memberId);
        model.addAttribute("totalPages", PageUtils.calculateTotalPages(total, PAGE_SIZE));
        model.addAttribute("totalCount", total);
        model.addAttribute("pendingCountMap", regisSvc.getPendingCountMap(list));
        model.addAttribute("qs", buildQueryString(map, "page"));

        return "front-end/member/activity/ownedActivities";
    }
 

    @PostMapping("ownedActivities_search")
    public String ownedActivitiesSearch(HttpServletRequest req, @AuthenticationPrincipal MemberUserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/front-end/login";
        }
        Map<String, String[]> map = req.getParameterMap();
        String qs = buildQueryString(map, "currentPage", "page");
        String redirectUrl = "redirect:/member/activity/ownedActivities?page=1";
        if (!qs.isEmpty()) {
            redirectUrl += "&" + qs;
        }
        return redirectUrl;
    }
  
    @PostMapping("cancel")
    public String cancel(@RequestParam("activityId") Integer activityId,
                         @RequestParam("cancelNote") String cancelNote,
                         @RequestParam(value = "currentPage", required = false) Integer currentPage,
                         @AuthenticationPrincipal MemberUserDetails userDetails,
                         RedirectAttributes redirectAttributes) {
    		if (userDetails == null) {
            return "redirect:/front-end/login";
        }
    	
    		try {
            // 先驗身分:這個活動是不是我的?
            Activity activity = activitySvc.getOneActivity(activityId);
            if (!activity.getMember().getMemberId().equals(userDetails.getMember().getMemberId())) {
                throw new IllegalStateException("無權操作此活動");
            }
            activitySvc.cancelActivity(activityId, cancelNote);
            redirectAttributes.addFlashAttribute("success", "取消成功");
        } catch (RuntimeException ex) {
        		redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

    		 int page = (currentPage != null) ? currentPage : 1;
    		 return "redirect:/member/activity/ownedActivities?page=" + page;
    }
    
    @PostMapping("postpone")
    public String postpone(@RequestParam("activityId") Integer activityId,
                            @RequestParam("postponeNote") String postponeNote,
                            @RequestParam(value = "currentPage", required = false) Integer currentPage,
                            @AuthenticationPrincipal MemberUserDetails userDetails,
                            RedirectAttributes redirectAttributes) {
    		if (userDetails == null) {
            return "redirect:/front-end/login";
        }
    	
    		try {
            Activity activity = activitySvc.getOneActivity(activityId);
            if (!activity.getMember().getMemberId().equals(userDetails.getMember().getMemberId())) {
                throw new IllegalStateException("無權操作此活動");
            }
            activitySvc.postponeActivity(activityId, postponeNote);
            redirectAttributes.addFlashAttribute("success", "延期成功");
        } catch (RuntimeException ex) {
        		redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

    		int page = (currentPage != null) ? currentPage : 1;
    		return "redirect:/member/activity/ownedActivities?page=" + page;
    }
    
    @PostMapping("confirmNewSchedule")
    public String confirmNewSchedule(@RequestParam("activityId") Integer activityId,
                                      @RequestParam("activityStart") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime activityStart,
                                      @RequestParam("activityEnd") @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime activityEnd,
                                      @RequestParam(value = "regisStart", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime regisStart,
                                      @RequestParam(value = "regisEnd", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime regisEnd,
                                      @RequestParam(value = "currentPage", required = false) Integer currentPage,
                                      @AuthenticationPrincipal MemberUserDetails userDetails,
                                      RedirectAttributes redirectAttributes) {
    		if (userDetails == null) {
            return "redirect:/front-end/login";
        }

    		try {
            Activity activity = activitySvc.getOneActivity(activityId);
            if (!activity.getMember().getMemberId().equals(userDetails.getMember().getMemberId())) {
                throw new IllegalStateException("無權操作此活動");
            }
            activitySvc.confirmNewSchedule(activityId, activityStart, activityEnd, regisStart, regisEnd);
            redirectAttributes.addFlashAttribute("success", "成功更新活動時間");
        } catch (Exception ex) {
        		redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        		redirectAttributes.addFlashAttribute("scheduleErrorActivityId", activityId);   // 記住是哪個活動的彈窗出錯
        }

    		int page = (currentPage != null) ? currentPage : 1;
    		return "redirect:/member/activity/ownedActivities?page=" + page;
    }
    
//    @GetMapping("activityImage")
//    public void activityImage(@RequestParam("activityId") Integer activityId, HttpServletResponse res)
//            throws IOException {
//     Activity activity = activitySvc.getOneActivity(activityId);
//
//     if (activity == null || activity.getPicture() == null) {
//         return;   // 沒有活動或沒有圖片，什麼都不寫，回應空白
//     }  
//
//     // 組出圖片在磁碟上的實際路徑
//     String uploadDir = System.getProperty("user.dir") + "/uploads/activity-images/";
//     File imageFile = new File(uploadDir, activity.getPicture());
//
//     if (!imageFile.exists()) {
//         return;   // 檔名有記錄，但實體檔案不存在（例如被誤刪），一樣回應空白
//     }
//     res.setContentType("image/jpeg");
//     Files.copy(imageFile.toPath(), res.getOutputStream()); // 讀取這個檔案 → 把讀到的內容寫進 response
//    }
    
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
        
        long pendingCount = regisSvc.countPendingByActivity(activity);
        model.addAttribute("pendingCount", pendingCount);
        
        model.addAttribute("activity", activity);
        model.addAttribute("reviewListData", regisSvc.getReviewsByActivity(activity));
        model.addAttribute("myRegisMap", buildMyRegistrationMap(userDetails));  
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
    
    // 建立「我有效報名的<活動ID,報名狀態>集合, 訪客則回傳空Map
    private Map<Integer, Integer> buildMyRegistrationMap(MemberUserDetails userDetails) {
        Map<Integer, Integer> map = new HashMap<>();
        if (userDetails == null) {
            return map;   // 訪客:回傳空Map, 所有活動一律顯示報名按鈕
        }
        
        // 撈出此會員的所有報名紀錄
        List<Registration> myRegis = regisSvc.getMyRegistrations(userDetails.getMember());
        for (Registration r : myRegis) {
            Integer status = r.getRegisStatus();
            if (status == 0 || status == 1 || status == 4) {   // 待審核、已報名成功(正取)、已報名成功(備取)
                map.put(r.getActivity().getActivityId(), status);
            }
        }
        return map;
    }
    
    // 把上傳的圖片真正寫進磁碟，回傳存檔用的檔名
    private String saveUploadedPicture(MultipartFile pictureFile) throws IOException {
        // 1. 從原始檔名取出副檔名
        String originalFilename = pictureFile.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 2. 產生一個不會撞名的新檔名：時間戳記 + 一小段隨機碼 + 副檔名
        String savedFileName = System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + extension;

        // 3. 組出資料夾的絕對路徑（專案根目錄 + uploads/activity-images/）
        String uploadDir = System.getProperty("user.dir") + "/uploads/activity-images/";
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();   // 資料夾不存在就自動建立
        }

        // 4. 把上傳的檔案真正寫進這個路徑
        File dest = new File(dir, savedFileName);  // 做一個代表某個檔案路徑的物件(資料夾名, 檔案名稱)
        pictureFile.transferTo(dest); // 把上傳的檔案寫入指定的磁碟位置，取代getBytes()

        // 5. 回傳這個檔名
        return savedFileName;
    }
    
    // 刪除舊圖
    private void deleteOldPicture(String fileName) {
        String uploadDir = System.getProperty("user.dir") + "/uploads/activity-images/";
        File oldFile = new File(uploadDir, fileName);
        if (oldFile.exists()) {
            oldFile.delete();
        }
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
