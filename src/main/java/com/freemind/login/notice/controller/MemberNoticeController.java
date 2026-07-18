package com.freemind.login.notice.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;
import com.freemind.login.notice.service.NoticeService;
import com.freemind.login.psychologist.dto.PsychologistSelfRes;
@Controller
@RequestMapping("/member/notice")
public class MemberNoticeController {

	private final NoticeService noticeService;
	private final MemberService memberSvc;
	
	public MemberNoticeController(NoticeService noticeService , MemberService memberSvc) {
		this.noticeService = noticeService;
		this.memberSvc = memberSvc;
	}
	@ModelAttribute("member")
    public Member currentMember(Authentication authentication) {
        // 訪客（未登入或匿名）時不放 member 進 model
        if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return memberSvc.findByAccount(authentication.getName());
    }
	@GetMapping
	public String noticePage(@RequestParam(name = "type", required = false) Byte type, @ModelAttribute("member") Member member , Model model) {
		Integer memberId = member.getMemberId();
		model.addAttribute("notices", noticeService.getMemberNotices(memberId, type));
		model.addAttribute("unreadCount", noticeService.countMemberUnread(memberId));
		model.addAttribute("type" , type);
		return "front-end/member/notice/notice";
	}

	
	@PostMapping("/{noticeId}/read")
	public String markRead(@PathVariable Integer noticeId ,@RequestParam(name ="type" , required = false) Byte type, @ModelAttribute("member") Member member){
		Integer memberId = member.getMemberId();
		noticeService.markMemberNoticeRead(noticeId,memberId);
		return "redirect:/member/notice" + (type != null ? "?type=" + type : "");
	}
	
	@PostMapping("/readAll")
	public String markAllRead(@RequestParam(name = "type" , required = false)Byte type,@ModelAttribute("member") Member member) {
		Integer memberId = member.getMemberId();
		noticeService.markAllMemberRead(memberId);
		return "redirect:/member/notice" + (type != null ? "?type=" + type : "" );
	}
}
