package com.freemind.login.security.membersecurity;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.freemind.login.member.model.Member;

public class MemberUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private Member member;

    public MemberUserDetails(Member member) {
        this.member = member;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Member 不需要權限，只給予 ROLE_MEMBER
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_MEMBER"));
    }

    @Override
    public String getPassword() {
        return member.getMemberPassword();
    }

    @Override
    public String getUsername() {
        return member.getMemberAccount();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 只有 accountStatus=1 才算 enabled
        return member.getAccountStatus() == 1;
    }

    public Member getMember() {
        return member;
    }

}
