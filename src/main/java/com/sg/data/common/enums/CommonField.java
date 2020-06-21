package com.sg.data.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CommonField {

	_ID("_id"), SET("$set"), SET_ON_INSERT("$setOnInsert"), ADD_TO_SET("$addToSet"), UNSET("$unset");

	@Getter
	private String field;
}
