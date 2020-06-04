package com.sg.assignment.common.exception;

public class NotFoundOrExpiredCouponException extends AbstractException {

	private static final long serialVersionUID = -3575974387323484219L;

	@Override
	public String getErrorCode() {
		// TODO Auto-generated method stub
		return NOT_FOUND_OR_EXPIRED_COUPON_CODE;
	}
}
