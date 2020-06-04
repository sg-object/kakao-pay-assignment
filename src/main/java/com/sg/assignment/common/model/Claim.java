package com.sg.assignment.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class Claim {

	private String id;

	public Claim(SecurityUser securityUser) {
		this.id = securityUser.getUsername();
	}
}
