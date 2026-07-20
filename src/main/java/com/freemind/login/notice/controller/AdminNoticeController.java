package com.freemind.login.notice.controller;

import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.freemind.login.admin.model.Admin;
import com.freemind.login.admin.model.AdminService;
import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberRepository;
import com.freemind.login.notice.dto.PersonOption;
import com.freemind.login.notice.service.NoticeService;
import com.freemind.login.psychologist.entity.Psychologist;
import com.freemind.login.psychologist.repository.PsychologistRepository;

@Controller
@RequestMapping("/admin/notice")
public class AdminNoticeController {

	private final NoticeService noticeService;
	private final MemberRepository memberRepository;
	private final PsychologistRepository psychologistRepository;
	private final AdminService adminSvc;
	public AdminNoticeController(NoticeService noticeService,
			MemberRepository memberRepository, AdminService adminSvc,
			PsychologistRepository psychologistRepository) {
		this.noticeService = noticeService;
		this.memberRepository = memberRepository;
		this.adminSvc = adminSvc;
		this.psychologistRepository = psychologistRepository;
	}
	@ModelAttribute("admin")
    public Admin currentAdmin(Authentication authentication) {
        return adminSvc.findByAccount(authentication.getName());
    }

	@GetMapping
	public String managePage(
			@RequestParam(name = "role", defaultValue = "member") String role,
			@RequestParam(name = "keyword", required = false) String keyword,
			@ModelAttribute("admin") Admin admin,
			Model model) {

		boolean blank = (keyword == null || keyword.isBlank());
		List<PersonOption> people;

		if ("psych".equals(role)) {
			List<Psychologist> list = blank
					? psychologistRepository.findAll()
					: psychologistRepository
							.findByPsychAccountContainingOrNameContaining(keyword, keyword);
			people = list.stream()
					.map(p -> new PersonOption(p.getPsychId(), p.getPsychAccount(), p.getName()))
					.toList();
		} else {
			role = "member";
			List<Member> list = blank
					? memberRepository.findAll()
					: memberRepository.findByMemberAccountOrNameContaining(keyword, keyword);
			people = list.stream()
					.map(m -> new PersonOption(m.getMemberId(), m.getMemberAccount(),
							m.getName()))  
					.toList();
		}

		model.addAttribute("people", people);
		model.addAttribute("role", role);
		model.addAttribute("keyword", keyword);
		return "back-end/login/notice/adminNoticeManage"; 
	}

	@GetMapping("/history")
	public String historyPage(
			@RequestParam("role") String role, 
			@RequestParam("id") Integer id,
			@RequestParam(name = "type", required = false) Byte type,
			Model model,
			RedirectAttributes redirectAttributes) {

		if ("psych".equals(role)) {
			Psychologist p = psychologistRepository.findById(id).orElse(null);
			if (p == null) {
				redirectAttributes.addFlashAttribute("alertMessage", "找不到該心理師");
				return "redirect:/admin/notice?role=psych";
			}
			model.addAttribute("person", new PersonOption(p.getPsychId(), p.getPsychAccount(), p.getName()));
			model.addAttribute("psychNotices", noticeService.getPsychNotices(id, type));
		} else {
			Member m = memberRepository.findById(id).orElse(null);
			if (m == null) {
				redirectAttributes.addFlashAttribute("alertMessage", "找不到該會員");
				return "redirect:/admin/notice?role=member";
			}
			model.addAttribute("person", new PersonOption(m.getMemberId(), m.getMemberAccount(), m.getName()));
			model.addAttribute("memberNotices", noticeService.getMemberNotices(id, type));
		}

		model.addAttribute("role", role);
		model.addAttribute("type", type);
		return "back-end/login/notice/adminNoticeHistory";  
	}


	@GetMapping("/send")
	public String sendPage(Model model) {
		model.addAttribute("memberOptions", memberRepository.findAll());
		model.addAttribute("psychOptions", psychologistRepository.findAll());
		return "back-end/login/notice/adminSendNotice"; 
	}

	private Integer getLoginAdminId(Authentication auth) {
		if (auth != null && auth.getPrincipal() instanceof
				com.freemind.login.security.adminsecurity.AdminUserDetails ud) {
			return ud.getAdmin().getAdminId();
		}
		return null;
	}

	@PostMapping("/send")
	public String send(
			@RequestParam("targetRole") String targetRole,
			@RequestParam(name = "sendAll", defaultValue = "false") boolean sendAll,
			@RequestParam(name = "targetIds", required = false) List<Integer> targetIds,
			@RequestParam("noticeType") byte noticeType,
			@RequestParam("content") String content,
			Authentication auth,
			RedirectAttributes redirectAttributes) {

		if (content == null || content.isBlank()) {
			redirectAttributes.addFlashAttribute("alertMessage", "通知內容請勿空白");
			return "redirect:/admin/notice/send";
		}
		if (!sendAll && (targetIds == null || targetIds.isEmpty())) {
			redirectAttributes.addFlashAttribute("alertMessage", "請選擇收件對象(或勾選全部)");
			return "redirect:/admin/notice/send";
		}

		Integer adminId = getLoginAdminId(auth);

		int sentCount;
		if ("member".equals(targetRole)) {
			List<Integer> ids = sendAll
					? memberRepository.findAll().stream().map(Member::getMemberId).toList()
					: targetIds;
			noticeService.sendToMembers(ids, adminId, content, noticeType);
			sentCount = ids.size();
		} else if ("psych".equals(targetRole)) {
			List<Integer> ids = sendAll
					? psychologistRepository.findAll().stream().map(Psychologist::getPsychId).toList()
					: targetIds;
			noticeService.sendToPsychs(ids, adminId, content, noticeType);
			sentCount = ids.size();
		} else {
			redirectAttributes.addFlashAttribute("alertMessage", "收件身分不正確");
			return "redirect:/admin/notice/send";
		}

		redirectAttributes.addFlashAttribute("alertMessage", "已發送通知給 " + sentCount + " 人");
		return "redirect:/admin/notice/send";
	}
}