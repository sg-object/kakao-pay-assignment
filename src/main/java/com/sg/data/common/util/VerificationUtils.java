package com.sg.data.common.util;

import java.util.regex.Pattern;

public class VerificationUtils {

	public static boolean isNullOrBlank(String value) {
		return value == null || value.trim().isEmpty();
	}

	public static boolean isValidCoupon(String coupon) {
		Pattern pattern = Pattern.compile("[A-Z0-9]{5}-[A-Z0-9]{6}-[A-Z0-9]{8}");
		return isNullOrBlank(coupon) ? false : pattern.matcher(coupon).matches();
	}
}
