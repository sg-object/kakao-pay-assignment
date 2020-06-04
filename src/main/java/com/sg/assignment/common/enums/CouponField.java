package com.sg.assignment.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum CouponField {

	COUPON("coupon"), CREATE_DATE("createDate"), EXPIRE_DATE("expireDate"), ISSUE_YN("issueYn"), USER_ID("userId"),
	ISSUE_DATE("issueDate"), NOTIFY_YN("notifyYn");

	@Getter
	private String field;
}
