package com.sg.assignment.common.model;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
@JsonInclude(Include.NON_NULL)
public class SecurityUser extends User {

	private static final long serialVersionUID = -3531439484732724601L;

	public SecurityUser(com.sg.assignment.user.model.User user, Collection<? extends GrantedAuthority> authorities) {
		// TODO Auto-generated constructor stub
		super(user.getId(), user.getPassword(), authorities);
	}
}
