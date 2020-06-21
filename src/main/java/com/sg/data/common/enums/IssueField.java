package com.sg.data.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum IssueField {

	COLLECTION("collection"), USE_YN("useYn"), EXPIRE_YN("expireYn"), COUPONS("coupons"),
	COUPONS_ID(IssueField.COUPONS.getField() + "." + CommonField._ID.getField()),
	COUPONS_COUPON(IssueField.COUPONS.getField() + "." + CouponField.COUPON.getField()),
	COUPONS_EXPIRE_YN(IssueField.COUPONS.getField() + "." + IssueField.EXPIRE_YN.getField()),
	COUPONS_ISSUE_DATE(IssueField.COUPONS.getField() + "." + CouponField.ISSUE_DATE.getField()),
	COUPONS_EXPIRE_DATE(IssueField.COUPONS.getField() + "." + CouponField.EXPIRE_DATE.getField()),
	UPDATE_USE_YN(IssueField.COUPONS.getField() + ".$." + IssueField.USE_YN.getField()),
	UPDATE_EXPIRE_YN(IssueField.COUPONS.getField() + ".$." + IssueField.EXPIRE_YN.getField());

	@Getter
	private String field;
}
