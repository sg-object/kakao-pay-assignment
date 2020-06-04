package com.sg.assignment.common.exception;

public class CanceledCouponException extends AbstractException {

	private static final long serialVersionUID = 5179659887406566156L;

	@Override
	public String getErrorCode() {
		// TODO Auto-generated method stub
		return CANCELED_COUPON_CODE;
	}
}
