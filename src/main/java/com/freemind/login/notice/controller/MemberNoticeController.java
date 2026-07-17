package com.freemind.login.notice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.freemind.login.notice.service.NoticeService;
@Controller
@RequestMapping("/member/notice")
public class MemberNoticeController {

	private final NoticeService noticeService;
	
	public MemberNoticeController(NoticeService noticeService) {
		this.noticeService = noticeService;
	}
	
	@GetMapping
	public String noticePage(@RequestParam(name = "type", required = false) Byte type, @SessionAttribute("memberId") Integer memberId , Model model) {
		model.addAttribute("notices", noticeService.getMemberNotices(memberId, type));
		model.addAttribute("unreadCount", noticeService.countMemberUnread(memberId));
		model.addAttribute("type" , type);
		return "front-end/member/notice/notice";
	}

	
	@PostMapping("/{noticeId}/read")
	public String markRead(@PathVariable Integer noticeId ,@RequestParam(name ="type" , required = false) Byte type, @SessionAttribute("memberId") Integer memberId){
		noticeService.markMemberNoticeRead(noticeId,memberId);
		return "redirect:/member/notice" + (type != null ? "?type=" + type : "");
	}
	
	@PostMapping("/readAll")
	public String markAllRead(@RequestParam(name = "type" , required = false)Byte type,@SessionAttribute("memberId") Integer memberId) {
		noticeService.markAllMemberRead(memberId);
		return "redirect:/member/notice" + (type != null ? "?type=" + type : "" );
	}
}
