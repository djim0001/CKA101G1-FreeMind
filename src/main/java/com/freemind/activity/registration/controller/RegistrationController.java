package com.freemind.activity.registration.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.freemind.activity.registration.model.Registration;
import com.freemind.activity.registration.model.RegistrationRepository;
import com.freemind.activity.registration.model.RegistrationService;
import com.freemind.login.member.model.Member;
import com.freemind.login.security.membersecurity.MemberUserDetails;

@Controller
@RequestMapping("/member/activity/registration")
public class RegistrationController {

    @Autowired
    private RegistrationService regisService;

    // 一、我的報名清單
    @GetMapping("myRegistrations")
    public String myRegistrations(@AuthenticationPrincipal MemberUserDetails userDetails,
                                  ModelMap model) {
      
    		Member member = userDetails.getMember();
    		List<Registration> list = regisService.getMyRegistrations(member);
    		model.addAttribute("regisListData", list);
    		return "front-end/member/activity/registration/myRegistrations";
    }
}