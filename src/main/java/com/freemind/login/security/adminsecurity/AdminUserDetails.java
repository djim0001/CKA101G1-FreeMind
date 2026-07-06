package com.freemind.login.security.adminsecurity;

import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.freemind.login.admin.model.Admin;

public class AdminUserDetails implements UserDetails {

    private static final long serialVersionUID = 1L;

    private Admin admin;

    public AdminUserDetails(Admin admin) {
        this.admin = admin;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 將 permissions 轉換為 Spring Security 的 GrantedAuthority
        return admin.getPermissions().stream()
                .map(p -> new SimpleGrantedAuthority("ROLE_" + p.getPermName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return admin.getAdminPassword();
    }

    @Override
    public String getUsername() {
        return admin.getAdminAccount();
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
        return admin.getAccountStatus() == 1;
    }

    public Admin getAdmin() {
        return admin;
    }

}
