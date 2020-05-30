package com.sg.assignment.common.util;

public class VerificationUtils {

	public static boolean isNullOrBlank(String value) {
		return value == null || value.trim().isEmpty();
	}
}
