package com.sg.assignment.common.web.provider;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import com.sg.assignment.common.web.service.AjaxUserDetailsService;

@Component
public class AjaxAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private AjaxUserDetailsService ajaxUserDetailsService;

	@Override
	protected void additionalAuthenticationChecks(UserDetails userDetails,
			UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
		// TODO Auto-generated method stub
		Optional.ofNullable(authentication.getCredentials())
				.filter(password -> passwordEncoder.matches(password.toString(), userDetails.getPassword()))
				.orElseThrow(() -> new BadCredentialsException("Bad credentials"));
	}

	@Override
	protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication)
			throws AuthenticationException {
		// TODO Auto-generated method stub
		return ajaxUserDetailsService.loadUserByUsername(username);
	}
}
