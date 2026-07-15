package com.freemind.login.psychologist.controller;

import java.io.File;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.login.psychologist.dto.ConflictOrderRes;
import com.freemind.login.psychologist.dto.ExpertiseRes;
import com.freemind.login.psychologist.dto.PsychologistPasswordReq;
import com.freemind.login.psychologist.dto.PsychologistSelfRes;
import com.freemind.login.psychologist.dto.PsychologistUpdateReq;
import com.freemind.login.psychologist.service.ExpertiseService;
import com.freemind.login.psychologist.service.PsychologistService;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/psych/profile")
public class PsychologistSelfController {

	private final PsychologistService psychologistService;
	private final ExpertiseService expertiseService;

	@Value("${psych.upload.dir}")
	private String uploadDir;

	public PsychologistSelfController(PsychologistService psychologistService,
			ExpertiseService expertiseService) {
		this.psychologistService = psychologistService;
		this.expertiseService = expertiseService;
	}

	@ModelAttribute("expertiseOptions")
	public List<ExpertiseRes> expertiseOptions() {
		return expertiseService.getAllExpertise();
	}

	@GetMapping
	public String profilePage() {
		return "front-end/psych/psych/profile";
	}

	@GetMapping("/edit")
	public String editPage(
			@ModelAttribute(name = "psych", binding = false) PsychologistSelfRes psych,
			Model model) {

		if (!model.containsAttribute("updateReq")) {
			PsychologistUpdateReq req = new PsychologistUpdateReq();
			req.setPhoneNumber(psych.getPhoneNumber());
			req.setEmail(psych.getEmail());
			req.setPsychLoc(psych.getPsychLoc());
			req.setPsychFee(psych.getPsychFee());
			req.setWeeklyAvailability(psych.getWeeklyAvailability());
			req.setBankAccount(psych.getBankAccount());
			req.setExpertiseIds(psych.getExpertiseList().stream()
					.map(e -> e.getExpertiseId()).toList());
			model.addAttribute("updateReq", req);
		}

		return "front-end/psych/psych/edit";
	}

	@PostMapping("/edit")
	public String update(
			@SessionAttribute("psychId") Integer psychId,
			@Valid @ModelAttribute("updateReq") PsychologistUpdateReq updateReq,
			BindingResult bindingResult,
			@RequestParam(name = "profilePicFile", required = false) MultipartFile profilePicFile,
			Model model,
			RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			return "front-end/psych/psych/edit";
		}

		if (profilePicFile != null && !profilePicFile.isEmpty()) {
			try {
				File dir = new File(uploadDir);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				String originFileName = profilePicFile.getOriginalFilename();
				String ext = "";
				if (originFileName != null && originFileName.contains(".")) {
					ext = originFileName.substring(originFileName.lastIndexOf("."));
				}
				String newFileName = UUID.randomUUID().toString() + ext;
				profilePicFile.transferTo(new File(dir, newFileName).toPath());
				updateReq.setProfilePic(newFileName); // DB 只存檔名
			} catch (Exception e) {
				model.addAttribute("errorMessage", "照片上傳失敗:" + e.getMessage());
				return "front-end/psych/psych/edit";
			}
		}

		try {
			List<ConflictOrderRes> conflicts = psychologistService.updateSelf(psychId, updateReq);

			if (!conflicts.isEmpty()) {
				model.addAttribute("conflicts", conflicts);
				return "front-end/psych/psych/edit";
			}

			redirectAttributes.addFlashAttribute("successMessage", "資料更新成功");
			return "redirect:/psych/profile";

		} catch (IllegalArgumentException e) {           
			model.addAttribute("errorMessage", e.getMessage());
			return "front-end/psych/psych/edit";
		}                                               
	}        

	@GetMapping("/password")
	public String passwordPage(Model model) {
		if (!model.containsAttribute("passwordReq")) {
			model.addAttribute("passwordReq", new PsychologistPasswordReq());
		}
		return "front-end/psych/psych/password";
	}

	@PostMapping("/password")
	public String changePassword(
			@SessionAttribute("psychId") Integer psychId,
			@Valid @ModelAttribute("passwordReq") PsychologistPasswordReq passwordReq,
			BindingResult bindingResult,
			HttpSession session,
			Model model) {

		if (bindingResult.hasErrors()) {
			return "front-end/psych/psych/password";
		}

		try {
			psychologistService.changePassword(psychId, passwordReq);
		} catch (IllegalArgumentException e) {
			model.addAttribute("errorMessage", e.getMessage());
			return "front-end/psych/psych/password";
		}

		session.invalidate();
		return "redirect:/psych/psychologistLogin?pwchanged";
	}
}