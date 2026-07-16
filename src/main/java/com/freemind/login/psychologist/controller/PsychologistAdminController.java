package com.freemind.login.psychologist.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.login.admin.model.Admin;
import com.freemind.login.admin.model.AdminService;
import com.freemind.login.psychologist.dto.PsychologistAdminRes;
import com.freemind.login.psychologist.entity.Psychologist;
import com.freemind.login.psychologist.repository.PsychologistRepository;
import com.freemind.login.psychologist.service.PsychologistService;
import com.freemind.login.security.adminsecurity.AdminUserDetails;

@Controller
@RequestMapping("/admin/psych")
public class PsychologistAdminController {

	private final PsychologistService psychologistService;
	private final AdminService adminSvc;
	private final PsychologistRepository r;
	
	public PsychologistAdminController(PsychologistService psychologistService , AdminService adminSvc, PsychologistRepository r) {
		this.psychologistService = psychologistService;
		this.adminSvc = adminSvc;
		this.r =r;
	}

	
	@ModelAttribute("admin")
    public Admin currentAdmin(Authentication authentication) {
        return adminSvc.findByAccount(authentication.getName());
    }
	
	
	@GetMapping({"", "/"})
	public String adminList(Model model,
			@RequestParam(required = false) String name,
			@RequestParam(required = false) String gender,
			@RequestParam(required = false) String psychLoc,
			@RequestParam(required = false) Integer minFee,
			@RequestParam(required = false) Integer maxFee,
			@RequestParam(required = false) String expertiseName,
			@RequestParam(required = false) Integer accountStatus,
			@RequestParam(required = false) Boolean hasPracticeLicense,
			@RequestParam(required = false) String orderBy,
			@ModelAttribute("admin") Admin admin
//			@SessionAttribute(name = "adminId", required = false) Integer adminId
			) {
		Integer adminId = admin.getAdminId();
		if (adminId == null) {
			 System.out.println("沒抓到管理員");
			model.addAttribute("errorMessage", "*請先登入");
			return "back-end/login";  
		}

		List<PsychologistAdminRes> psychList = psychologistService.adminSearch(
				name, gender, psychLoc, minFee, maxFee, expertiseName,
				accountStatus, hasPracticeLicense,
				orderBy);
		model.addAttribute("psychList", psychList);

		model.addAttribute("name", name);
		model.addAttribute("gender", gender);
		model.addAttribute("psychLoc", psychLoc);
		model.addAttribute("accountStatus", accountStatus);
		model.addAttribute("hasPracticeLicense", hasPracticeLicense);
		return "back-end/psych/psychList";
	}

	@GetMapping("/{psychId}")
	public String adminDetail(Model model,
			@PathVariable Integer psychId,
			@ModelAttribute("admin") Admin admin,
//			@AuthenticationPrincipal AdminUserDetails userDetails,
//			@SessionAttribute(name = "adminId", required = false) Integer adminId,
			RedirectAttributes redirectAttributes) {
		
		Integer adminId = admin.getAdminId(); 	
//		if (userDetails == null || userDetails.getAdmin() == null) {
//	        model.addAttribute("errorMessage", "*請先登入");
//	        return "back-end/login";
//	    }
		if (adminId == null) {
			model.addAttribute("errorMessage", "*請先登入");
			return "back-end/login";
		}
		try {
			PsychologistAdminRes psych = psychologistService.getAdmin(psychId);
			model.addAttribute("psych", psych);
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("alertMessage", e.getMessage());
			return "redirect:/admin/psych";
		}
		return "back-end/psych/adminDetail";
	}

	@PostMapping("/{psychId}/license")
	public String updateLicense(
			@PathVariable Integer psychId,
			@RequestParam("approved") boolean approved,
//			@SessionAttribute(name = "adminId", required = false) Integer adminId,
			@ModelAttribute("admin") Admin admin,
			Model model,
			RedirectAttributes redirectAttributes) {

		Integer adminId = admin.getAdminId();
		
		if (adminId == null) {
			model.addAttribute("errorMessage", "*請先登入");
			return "back-end/login";
		}

		try {
			psychologistService.updateLicense(psychId, approved);
			redirectAttributes.addFlashAttribute("alertMessage",
					approved ? "已通過執業許可" : "已撤銷執業許可,該心理師未來的預約已全數取消");
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("alertMessage", e.getMessage());
		}
		return "redirect:/admin/psych/" + psychId;
	}
	
	@PostMapping("/{psychId}/status")
	public String updateStatus(
			@PathVariable Integer psychId,
			@RequestParam("status") Integer status,
			@ModelAttribute("admin") Admin admin,
//			@SessionAttribute(name = "adminId", required = false) Integer adminId,
			Model model,
			RedirectAttributes redirectAttributes) {
		Integer adminId = admin.getAdminId(); 
		Psychologist psych = r.findById(psychId).orElseThrow();
		System.out.println("accountStatus = " + psych.getAccountStatus());
		if (adminId == null) {
			model.addAttribute("errorMessage", "*請先登入");
			return "back-end/login";
		}

		try {
			psychologistService.updateAccountStatus(psychId, status);
			String msg = switch (status) {
				case 1 -> "帳號已啟用";
				case 2 -> "帳號已停權,該心理師未來的預約已全數取消";
				default -> "帳號已退回未啟用";
			};
			redirectAttributes.addFlashAttribute("alertMessage", msg);
		} catch (IllegalArgumentException e) {
			redirectAttributes.addFlashAttribute("alertMessage", e.getMessage());
		}
		return "redirect:/admin/psych/" + psychId;
	}
}