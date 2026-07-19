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
@RequestMapping("/psych/notice")
public class PsychNoticeController {
	
	private final NoticeService noticeService;
	
	public PsychNoticeController(NoticeService noticeService) {
		this.noticeService = noticeService;
	}
	
	@GetMapping
	public String noticePage(@RequestParam(name = "type" , required = false) Byte type , @SessionAttribute("psychId") Integer psychId , Model model) {
		model.addAttribute("notices", noticeService.getPsychNotices(psychId,type));
		model.addAttribute("unreadCount", noticeService.countPsychUnread(psychId));
		model.addAttribute("type" , type);
		return "front-end/psych/notice/notice";
	}

	
	@PostMapping("/{noticeId}/read")
	public String markRead(@PathVariable Integer noticeId ,@RequestParam(name ="type" , required = false) Byte type, @SessionAttribute("psychId") Integer psychId){
		noticeService.markPsychNoticeRead(noticeId,psychId);
		return "redirect:/psych/notice" + (type != null ? "?type=" + type : "");
	}
	
	@PostMapping("/readAll")
	public String markAllRead(@RequestParam(name = "type" , required = false)Byte type,@SessionAttribute("psychId") Integer psychId) {
		noticeService.markAllPsychRead(psychId);
		return "redirect:/psych/notice" + (type != null ? "?type=" + type : "" );
	}
	
}
