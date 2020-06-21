package com.sg.data.common.web.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.sg.data.common.jwt.JwtAuthenticationToken;
import com.sg.data.common.jwt.JwtInfo;
import com.sg.data.common.util.VerificationUtils;

public class JwtAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	public JwtAuthenticationFilter(RequestMatcher requestMatcher) {
		// TODO Auto-generated constructor stub
		super(requestMatcher);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		// TODO Auto-generated method stub
		String token = request.getHeader(JwtInfo.TOKEN_NAME);
		if (VerificationUtils.isNullOrBlank(token)) {
			throw new BadCredentialsException("Bad credentials");
		} else {
			return getAuthenticationManager().authenticate(new JwtAuthenticationToken(token));
		}
	}

	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		// TODO Auto-generated method stub
		SecurityContext context = SecurityContextHolder.createEmptyContext();
		context.setAuthentication(authResult);
		SecurityContextHolder.setContext(context);
		chain.doFilter(request, response);
	}

	@Override
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException, ServletException {
		// TODO Auto-generated method stub
		SecurityContextHolder.clearContext();
		getFailureHandler().onAuthenticationFailure(request, response, failed);
	}
}
