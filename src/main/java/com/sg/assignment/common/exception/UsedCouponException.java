package com.sg.assignment.common.exception;

public class UsedCouponException extends AbstractException {

	private static final long serialVersionUID = -7507293843192822446L;

	@Override
	public String getErrorCode() {
		// TODO Auto-generated method stub
		return USED_COUPON_CODE;
	}
}
