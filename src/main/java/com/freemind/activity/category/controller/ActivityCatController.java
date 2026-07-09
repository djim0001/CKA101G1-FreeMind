package com.freemind.activity.category.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.ModelAndView;

import com.freemind.activity.category.model.ActivityCat;
import com.freemind.activity.category.model.ActivityCatService;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

@Controller
@RequestMapping("/activityCat")   // 這個class底下所有方法的網址，都會以/activityCat開頭
public class ActivityCatController {

	@Autowired
	ActivityCatService activityCatSvc;
	
	@GetMapping("listAllActivityCat")
	public String listAllActivityCat(ModelMap model) {  //Spring幫忙準備的「資料袋子」，把資料放進去，Thymeleaf頁面才能拿到這些資料顯示出來
		List<ActivityCat> list = activityCatSvc.getAll();
		model.addAttribute("activityCatListData", list);
		return "back-end/activity/activityCat/listAllActivityCat";
	}
	
	// 顯示新增表單的空白頁面
	@GetMapping("addActivityCat")
	public String addActivityCat(ModelMap model) {
	    ActivityCat activityCat = new ActivityCat();  // 建立一個空的ActivityCat物件，放進model
	    model.addAttribute("activityCat", activityCat);
	    return "back-end/activity/activityCat/addActivityCat";
	}
	
	// 表單送出，真正執行新增
	@PostMapping("insert")        // 驗證的ActivityCat物件、驗證結果的容器、顯示給使用者看的資料袋子
	public String insert(@Valid ActivityCat activityCat, BindingResult result, ModelMap model) { 
		if (result.hasErrors()) {
			return "back-end/activity/activityCat/addActivityCat";
		}
		activityCatSvc.addActivityCat(activityCat); // 呼叫Service的addActivityCat方法，把這個已經驗證過的物件，交給Service處理（Service再呼叫Repository的save()存進資料庫）
		List<ActivityCat> list = activityCatSvc.getAll();  // 新增完成後，重新查一次「全部的分類清單」
		model.addAttribute("activityCatListData", list);  //把這份最新清單，放進「資料袋子」並取名activityCatListData
		model.addAttribute("success", "- (新增成功)");
		return "back-end/activity/activityCat/listAllActivityCat";
	}
	
	// 查詢單筆，帶入修改表單頁面
	@PostMapping("getOne_For_Update")
	// 把前端表單或URL裡叫 activityCatId 的資料，抓出來並賦值給後端的 Java 變數 String activityCatId
	public String getOneForUpdate(@RequestParam("activityCatId") String activityCatId, ModelMap model) {
		// 把查到的結果，存進一個新的變數裡(並轉成Integer)
		ActivityCat activityCat = activityCatSvc.getOneActivityCat(Integer.valueOf(activityCatId));
		model.addAttribute("activityCat", activityCat);
		return "back-end/activity/activityCat/update_activityCat_input";
	}

	// 真正執行修改
	@PostMapping("update")
	public String update(@Valid ActivityCat activityCat, BindingResult result, ModelMap model) {
		if (result.hasErrors()) {
			return "back-end/activity/activityCat/update_activityCat_input";
		}
		activityCatSvc.updateActivityCat(activityCat);
		List<ActivityCat> list = activityCatSvc.getAll();
		model.addAttribute("activityCatListData", list);
		model.addAttribute("success", "- (修改成功)");
		return "back-end/activity/activityCat/listAllActivityCat";
	}
	// 刪除
	@PostMapping("delete")
	public String delete(@RequestParam("activityCatId") String activityCatId, ModelMap model) {
	    activityCatSvc.deleteActivityCat(Integer.valueOf(activityCatId));   // 單純執行刪除，不用接收回傳值

	    List<ActivityCat> list = activityCatSvc.getAll();   // 刪除後，重新查一次最新清單
	    model.addAttribute("activityCatListData", list);    // 放進資料袋子，準備給列表頁顯示
	    model.addAttribute("success", "- (刪除成功)");
	    return "back-end/activity/activityCat/listAllActivityCat";
	}
	
	// 模糊搜尋(使用方法級別驗證)
	@PostMapping("searchByName")
	public String searchByName(
	    @NotEmpty(message = "搜尋關鍵字: 請勿空白")
	    @Size(max = 50, message = "搜尋關鍵字: 長度不能超過{max}")
	    @RequestParam("keyword") String keyword, ModelMap model) {
		List<ActivityCat> list = activityCatSvc.getByNameLike(keyword);
		model.addAttribute("activityCatListData", list);
		model.addAttribute("keyword", keyword);
		
	    if (list.isEmpty()) {
	        model.addAttribute("errorMessage", "查無符合「" + keyword + "」的分類資料");
	    }
	    
	    
		return "back-end/activity/activityCat/listAllActivityCat";
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
