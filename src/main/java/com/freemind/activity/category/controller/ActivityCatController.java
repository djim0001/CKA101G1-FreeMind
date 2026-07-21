package com.freemind.activity.category.controller;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.activity.category.model.ActivityCat;
import com.freemind.activity.category.model.ActivityCatService;
import com.freemind.login.admin.model.Admin;
import com.freemind.login.admin.model.AdminService;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Controller
@RequestMapping("/admin/activityCat")
public class ActivityCatController {

	@Autowired
	private ActivityCatService activityCatSvc;
	
	@Autowired
	private AdminService adminSvc;
	
	@ModelAttribute("admin")
	public Admin currentAdmin(Authentication authentication) {
	    // 訪客（未登入或匿名）時不放 admin 進 model
	    if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
	        return null;
	    }
	    return adminSvc.findByAccount(authentication.getName());
	}
	
	@GetMapping("listAllActivityCat")
	public String listAllActivityCat(@RequestParam(value = "keyword", required = false) String keyword, ModelMap model) {
	    List<ActivityCat> list;
	    if (keyword != null && !keyword.trim().isEmpty()) {
	        list = activityCatSvc.getByNameLike(keyword);
	        if (list.isEmpty()) {
	            model.addAttribute("errorMessage", "查無符合「" + keyword + "」的分類資料");
	        }
	        model.addAttribute("keyword", keyword);
	    } else {
	        list = activityCatSvc.getAll();
	    }
	    model.addAttribute("activityCatListData", list);
	    return "back-end/activity/activityCat/listAllActivityCat";
	}
	
	// 顯示新增表單的空白頁面
//	@GetMapping("addActivityCat")
//	public String addActivityCat(ModelMap model) {
//	    ActivityCat activityCat = new ActivityCat();  // 建立一個空的ActivityCat物件，放進model
//	    model.addAttribute("activityCat", activityCat);
//	    return "back-end/activity/activityCat/addActivityCat";
//	}
	
	@PostMapping("insert")         // 驗證的ActivityCat物件、驗證結果的容器、顯示給使用者看的資料袋子
	public String insert(@Valid ActivityCat activityCat, BindingResult result, RedirectAttributes redirectAttributes) {
	    if (result.hasErrors()) {
	        StringBuilder sb = new StringBuilder();
	        result.getFieldErrors().forEach(e -> sb.append(e.getDefaultMessage()).append("<br>"));
	        redirectAttributes.addFlashAttribute("errorMessage", sb.toString());
	        redirectAttributes.addFlashAttribute("openAddModal", true);
	        return "redirect:/admin/activityCat/listAllActivityCat";
	    }
	    activityCatSvc.addActivityCat(activityCat); // 呼叫Service的addActivityCat方法，把這個已經驗證過的物件，交給Service處理（Service再呼叫Repository的save()存進資料庫）
	    redirectAttributes.addFlashAttribute("success", "新增成功");
	    return "redirect:/admin/activityCat/listAllActivityCat";
	}
	
	
	// 查詢單筆，帶入修改表單頁面
//	@PostMapping("getOne_For_Update")
//	// 把前端表單或URL裡叫 activityCatId 的資料，抓出來並賦值給後端的 Java 變數 String activityCatId
//	public String getOneForUpdate(@RequestParam("activityCatId") String activityCatId, ModelMap model) {
//		// 把查到的結果，存進一個新的變數裡(並轉成Integer)
//		ActivityCat activityCat = activityCatSvc.getOneActivityCat(Integer.valueOf(activityCatId));
//		model.addAttribute("activityCat", activityCat);
//		return "back-end/activity/activityCat/update_activityCat_input";
//	}

	@PostMapping("update")
	public String update(@Valid ActivityCat activityCat, BindingResult result, RedirectAttributes redirectAttributes) {
	    if (result.hasErrors()) {
	        StringBuilder sb = new StringBuilder();
	        result.getFieldErrors().forEach(e -> sb.append(e.getDefaultMessage()).append("<br>"));
	        redirectAttributes.addFlashAttribute("errorMessage", sb.toString());
	        return "redirect:/admin/activityCat/listAllActivityCat";
	    }
	    activityCatSvc.updateActivityCat(activityCat);
	    redirectAttributes.addFlashAttribute("success", "修改成功");
	    return "redirect:/admin/activityCat/listAllActivityCat";
	}
	
	@PostMapping("delete")
	public String delete(@RequestParam("activityCatId") String activityCatId, RedirectAttributes redirectAttributes) {
	    activityCatSvc.deleteActivityCat(Integer.valueOf(activityCatId));
	    redirectAttributes.addFlashAttribute("success", "刪除成功");
	    return "redirect:/admin/activityCat/listAllActivityCat";
	}
	
	// 模糊搜尋(使用方法級別驗證)
	@PostMapping("searchByName")
	public String searchByName(
	    @NotEmpty(message = "搜尋關鍵字: 請勿空白")
	    @Size(max = 50, message = "搜尋關鍵字: 長度不能超過{max}")
	    @RequestParam("keyword") String keyword,
	    RedirectAttributes redirectAttributes) {
		String encodedKeyword = java.net.URLEncoder.encode(keyword, StandardCharsets.UTF_8);
	    return "redirect:/admin/activityCat/listAllActivityCat?keyword=" + encodedKeyword;
	}
	
	// 方法級別驗證的「異常處理」
	@ExceptionHandler(value = { HandlerMethodValidationException.class, ConstraintViolationException.class })
	public ModelAndView handleError(Exception e, Model model) {

	    StringBuilder strBuilder = new StringBuilder();

	    if (e instanceof HandlerMethodValidationException ex) {
	        ex.getParameterValidationResults().forEach(result -> {
	            result.getResolvableErrors().forEach(error ->
	                strBuilder.append(error.getDefaultMessage()).append("<br>")
	            );
	        });
	    } else if (e instanceof ConstraintViolationException ex) {
	        ex.getConstraintViolations().forEach(violation ->
	            strBuilder.append(violation.getMessage()).append("<br>")
	        );
	    }

	    List<ActivityCat> list = activityCatSvc.getAll();
	    model.addAttribute("activityCatListData", list);

	    return new ModelAndView("back-end/activity/activityCat/listAllActivityCat", "errorMessage", "請修正以下錯誤:<br>" + strBuilder.toString());
	}
}
