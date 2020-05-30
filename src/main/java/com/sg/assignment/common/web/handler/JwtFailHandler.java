package com.sg.assignment.common.web.handler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sg.assignment.common.exception.AbstractException;
import com.sg.assignment.common.service.MessageService;

@Component
public class JwtFailHandler implements AuthenticationFailureHandler {

	@Autowired
	private MessageService messageService;

	@Override
	public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException exception) throws IOException, ServletException {
		// TODO Auto-generated method stub
		response.setCharacterEncoding("UTF-8");
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.getWriter().write(new ObjectMapper()
				.writeValueAsString(messageService.getExceptionResponse(AbstractException.JWT_ERR_CODE)));
	}
}
