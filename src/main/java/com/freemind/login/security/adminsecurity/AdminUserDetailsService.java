package com.freemind.login.security.adminsecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.freemind.login.admin.model.Admin;
import com.freemind.login.admin.model.AdminService;

@Service
public class AdminUserDetailsService implements UserDetailsService {

    @Autowired
    private AdminService adminService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Admin admin = adminService.findByAccount(username);
        if (admin == null) {
            throw new UsernameNotFoundException("管理員帳號不存在: " + username);
        }
        return new AdminUserDetails(admin);
    }

}
