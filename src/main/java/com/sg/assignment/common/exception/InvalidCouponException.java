package com.sg.assignment.common.exception;

public class InvalidCouponException extends AbstractException {

	private static final long serialVersionUID = -8657222753429168928L;

	@Override
	public String getErrorCode() {
		// TODO Auto-generated method stub
		return INVALID_COUPON_CODE;
	}
}
