package com.freemind.login.security.psychologistsecurity;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.freemind.login.psychologist.entity.Psychologist;

public class PsychUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;

	private Psychologist psychologist;

	public PsychUserDetails(Psychologist psychologist) {
		this.psychologist = psychologist;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_PSYCH"));
	}

	@Override
	public String getPassword() {
		return psychologist.getPsychPassword();
	}

	@Override
	public String getUsername() {
		return psychologist.getPsychAccount();
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
		// 只看帳號狀態:0未啟用/2停權 都不能登入
		// 注意:hasPracticeLicense 不在這裡擋!
		// 沒執業許可的心理師要能登入補資料、等審核,只是不會出現在會員搜尋(search()已過濾)
		return psychologist.getAccountStatus() == 1;
	}

	public Psychologist getPsychologist() {
		return psychologist;
	}
}
