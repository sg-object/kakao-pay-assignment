package com.sg.assignment.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum UserField {

	LOGIN_ID("loginId"), PASSWORD("password");

	@Getter
	private String field;
}
