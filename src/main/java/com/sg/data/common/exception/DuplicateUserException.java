package com.sg.data.common.exception;

public class DuplicateUserException extends AbstractException {

	private static final long serialVersionUID = -948336279121474463L;

	@Override
	public String getErrorCode() {
		// TODO Auto-generated method stub
		return DUP_USER_CODE;
	}
}
