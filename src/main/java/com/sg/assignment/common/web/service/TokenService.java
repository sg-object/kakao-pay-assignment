package com.sg.assignment.common.web.service;

import java.io.UnsupportedEncodingException;
import org.springframework.stereotype.Service;
import com.sg.assignment.common.jwt.ClaimField;
import com.sg.assignment.common.jwt.JwtInfo;
import com.sg.assignment.common.model.SecurityUser;
import io.jsonwebtoken.Jwts;

@Service
public class TokenService {

	public String getJwtToken(SecurityUser securityUser) {
		try {
			return Jwts.builder().setIssuer(JwtInfo.ISSUER)
					.claim(ClaimField.id.name(), securityUser.getUsername())
					.claim(ClaimField.roles.name(),
							securityUser.getAuthorities().stream().map(role -> role.getAuthority())
									.toArray(String[]::new))
					// .setExpiration(JodaTimeUtil.nowAfterHoursToDate(JwtInfo.EXPIRES_LIMIT).toDate())
					.signWith(JwtInfo.ALGORITHM, JwtInfo.getSecretKey()).compact();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException();
		}
	}
}
