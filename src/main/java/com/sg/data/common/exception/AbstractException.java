package com.sg.data.common.exception;

public abstract class AbstractException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public final static String NOT_FOUND_USER_CODE = "0001";

	public static final String DUP_USER_CODE = "0002";

	public static final String VERIFICATION_CODE = "0003";

	public static final String JWT_ERR_CODE = "1001";

	public static final String INVALID_COUPON_CODE = "2001";

	public static final String USED_COUPON_CODE = "2002";

	public static final String NOT_FOUND_OR_EXPIRED_COUPON_CODE = "2003";

	public static final String NOT_FOUND_COUPON_CODE = "2004";

	public static final String CANCELED_COUPON_CODE = "2005";

	public static final String NOT_FOUND_USABLE_COUPON_CODE = "2006";
	
	public static final String ISSUE_COUPON_ERR_CODE = "2007";

	public static final String INTERNAL_SERVER_ERROR = "9999";

	public abstract String getErrorCode();
}
