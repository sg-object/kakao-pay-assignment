package com.sg.assignment.common.exception;

public abstract class AbstractException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public final static String NOT_FOUND_USER_CODE = "0001";

	public static final String DUP_USER_CODE = "0002";

	public static final String VERIFICATION_CODE = "0003";

	public static final String JWT_ERR_CODE = "1001";

	public static final String INTERNAL_SERVER_ERROR = "9999";

	public abstract String getErrorCode();
}
