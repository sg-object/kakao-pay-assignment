package com.sg.assignment.common.web.provider;

import java.util.ArrayList;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import com.sg.assignment.common.jwt.ClaimField;
import com.sg.assignment.common.jwt.JwtAuthenticationToken;
import com.sg.assignment.common.jwt.JwtInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;

@Component
public class JwtAuthenticationProvider implements AuthenticationProvider {

	@Override
	public Authentication authenticate(Authentication authentication) {
		// TODO Auto-generated method stub
		String jwt = authentication.getCredentials().toString();
		try {
			Jws<Claims> claims = Jwts.parser().setSigningKey(JwtInfo.getSecretKey()).parseClaimsJws(jwt);
			return getJwtAuthenticationToken(claims.getBody());
		} catch (ExpiredJwtException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	//@SuppressWarnings("unchecked")
	private JwtAuthenticationToken getJwtAuthenticationToken(Claims body) {
		String id = body.get(ClaimField.id.name()).toString();
		/*List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		((ArrayList<String>) body.get(ClaimField.roles.name())).forEach(role -> {
			authorities.add(new SimpleGrantedAuthority(role));
		});*/
		return new JwtAuthenticationToken(id, id, new ArrayList<GrantedAuthority>());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		// TODO Auto-generated method stub
		return JwtAuthenticationToken.class.isAssignableFrom(authentication);
	}
}
