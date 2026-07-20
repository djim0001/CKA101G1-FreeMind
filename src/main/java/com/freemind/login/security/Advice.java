package com.freemind.login.security;

import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.freemind.login.notice.service.NoticeService;
import com.freemind.login.psychologist.dto.PsychologistSelfRes;
import com.freemind.login.psychologist.service.PsychologistService;
import com.freemind.login.security.membersecurity.MemberUserDetails;
import com.freemind.login.security.psychologistsecurity.PsychUserDetails;

import jakarta.servlet.http.HttpSession;
@ControllerAdvice
public class Advice {

	
	 private final PsychologistService psychologistService;
	    private final NoticeService noticeService;   

	    public Advice(PsychologistService psychologistService,
	                  NoticeService noticeService) {
	        this.psychologistService = psychologistService;
	        this.noticeService = noticeService;
	    }



	@ModelAttribute("psych")
	public PsychologistSelfRes getPsych(Authentication auth, HttpSession session) {
		if (auth == null || !(auth.getPrincipal() instanceof PsychUserDetails ud)) {
			return null;
		}
		
		session.setAttribute("psychId", ud.getPsychologist().getPsychId());
		return psychologistService.getPsychSelf(ud.getPsychologist().getPsychId());
	}
	
    // 新增:每頁都補鈴鐺要的兩個值,依角色抓對應資料
	@ModelAttribute
    public void injectBellData(Authentication auth, Model model) {
        if (auth == null) return;
        Object principal = auth.getPrincipal();

        if (principal instanceof PsychUserDetails ud) {
            Integer psychId = ud.getPsychologist().getPsychId();
            model.addAttribute("unreadCount", noticeService.countPsychUnread(psychId));
            model.addAttribute("recentNotices",
                    noticeService.getPsychNotices(psychId, null)
                                 .stream().limit(5).toList());

        } else if (principal instanceof MemberUserDetails md) {
            Integer memberId = md.getMember().getMemberId();   
            model.addAttribute("unreadCount", noticeService.countMemberUnread(memberId));
            model.addAttribute("recentNotices",
                    noticeService.getMemberNotices(memberId, null)
                                 .stream().limit(5).toList());
        }
    }

}

