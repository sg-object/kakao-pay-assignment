package com.sg.data.common.web.filter;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.RequestMatcher;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sg.data.user.model.User;

public class AjaxAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

	public AjaxAuthenticationFilter(RequestMatcher requestMatcher) {
		// TODO Auto-generated constructor stub
		super(requestMatcher);
	}

	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		// TODO Auto-generated method stub
		if (isJson(request)) {
			User user = new ObjectMapper().readValue(request.getReader(), User.class);
			UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(user.getId(),
					user.getPassword());
			return getAuthenticationManager().authenticate(authentication);
		} else {
			throw new AccessDeniedException("Don't use content type for " + request.getContentType());
		}
	}

	private boolean isJson(HttpServletRequest request) {
		// return MediaType.APPLICATION_JSON_VALUE.equalsIgnoreCase(request.getContentType());
		return true;
	}
}
