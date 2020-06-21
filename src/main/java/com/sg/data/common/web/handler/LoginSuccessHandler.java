package com.sg.data.common.web.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sg.data.common.jwt.JwtInfo;
import com.sg.data.common.model.Claim;
import com.sg.data.common.model.SecurityUser;
import com.sg.data.common.web.service.TokenService;

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	@Autowired
	private TokenService tokenService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws ServletException, IOException {
		// TODO Auto-generated method stub
		SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
		response.setStatus(HttpStatus.OK.value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		Map<String, Object> values = new HashMap<>();
		values.put(JwtInfo.TOKEN_NAME, tokenService.createJWT(new Claim(securityUser)));
		new ObjectMapper().writeValue(response.getWriter(), values);
	}
}
