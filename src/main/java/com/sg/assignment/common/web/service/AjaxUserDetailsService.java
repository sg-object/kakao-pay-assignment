package com.sg.assignment.common.web.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.sg.assignment.common.model.SecurityUser;
import com.sg.assignment.user.service.UserService;

@Service
public class AjaxUserDetailsService implements UserDetailsService {

	@Autowired
	private UserService userService;

	@Override
	public UserDetails loadUserByUsername(String username) {
		// TODO Auto-generated method stub
		return Optional.ofNullable(userService.getUser(username)).filter(user -> user != null)
				.map(user -> new SecurityUser(user, getAuthorities()))
				.orElseThrow(() -> new UsernameNotFoundException(username));
	}

	private Collection<? extends GrantedAuthority> getAuthorities() {
		List<GrantedAuthority> auth = new ArrayList<GrantedAuthority>();
		auth.add(new SimpleGrantedAuthority("ROLE_USER"));
		return auth;
	}
}
