package com.sg.data.common.exception;

public class NotFoundUsableCouponException extends AbstractException {

	private static final long serialVersionUID = -1487756032642716963L;

	@Override
	public String getErrorCode() {
		// TODO Auto-generated method stub
		return NOT_FOUND_USABLE_COUPON_CODE;
	}
}
