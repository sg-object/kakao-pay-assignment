package com.sg.data.common.exception;

public class IssueCouponException extends AbstractException {

	private static final long serialVersionUID = -8316324344912033981L;

	@Override
	public String getErrorCode() {
		// TODO Auto-generated method stub
		return ISSUE_COUPON_ERR_CODE;
	}
}
