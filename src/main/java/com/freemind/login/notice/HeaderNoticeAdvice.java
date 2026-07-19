package com.freemind.login.notice;

import java.util.List;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;
import com.freemind.login.notice.repository.MemberNoticeRepository;
import com.freemind.login.notice.repository.PsychologistNoticeRepository;
import com.freemind.login.security.psychologistsecurity.PsychUserDetails;

@ControllerAdvice
public class HeaderNoticeAdvice {

    private final MemberNoticeRepository memberNoticeRepo;
    private final PsychologistNoticeRepository psychNoticeRepo;
    private final MemberService memberSvc;

    public HeaderNoticeAdvice(MemberNoticeRepository memberNoticeRepo,
                              PsychologistNoticeRepository psychNoticeRepo,
                              MemberService memberSvc) {
        this.memberNoticeRepo = memberNoticeRepo;
        this.psychNoticeRepo = psychNoticeRepo;
        this.memberSvc = memberSvc;
    }

    @ModelAttribute
    public void addNotice(Authentication auth, Model model) {
        if (auth == null || auth instanceof AnonymousAuthenticationToken) {
            return;   // 訪客不塞,樣板用 th:if 判斷即可
        }

        List<?> list = null;
        long unread = 0;

        if (auth.getPrincipal() instanceof PsychUserDetails ud) {
            Integer id = ud.getPsychologist().getPsychId();
            list   = psychNoticeRepo.findTop5ByPsychIdOrderByCreatedAtDesc(id);
            unread = psychNoticeRepo.countByPsychIdAndIsReadFalse(id);
        } else {
            // 會員:依你會員登入的實際 principal 型別調整
            Member member = memberSvc.findByAccount(auth.getName());
            if (member != null) {
                Integer id = member.getMemberId();
                list   = memberNoticeRepo.findTop5ByMemberIdOrderByCreatedAtDesc(id);
                unread = memberNoticeRepo.countByMemberIdAndIsReadFalse(id);
            }
        }

        model.addAttribute("notifications", list);
        model.addAttribute("unreadCount", unread);
        model.addAttribute("hasUnread", unread > 0);
    }
}
