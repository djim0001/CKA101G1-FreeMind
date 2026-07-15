package com.freemind.login.security.membersecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.freemind.login.member.model.Member;
import com.freemind.login.member.model.MemberService;

@Service
public class MemberUserDetailsService implements UserDetailsService {

    @Autowired
    private MemberService memberService;

//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Member member = memberService.findByAccount(username);
//        if (member == null) {
//            throw new UsernameNotFoundException("會員帳號不存在: " + username);
//        }
//        return new MemberUserDetails(member);
//    }
    //加入email作為帳號
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 先當帳號查，查不到且長得像信箱再當信箱查
        Member member = memberService.findByAccount(username);
        if (member == null && username.contains("@")) {
            member = memberService.findByEmail(username);
        }
        if (member == null) {
            throw new UsernameNotFoundException("會員帳號或信箱不存在: " + username);
        }
        return new MemberUserDetails(member);
    }

}
