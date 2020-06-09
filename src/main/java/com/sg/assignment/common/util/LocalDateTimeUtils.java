package com.sg.assignment.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class LocalDateTimeUtils {

	public static LocalDateTime now() {
		return LocalDateTime.now(ZoneOffset.UTC);
	}

	public static LocalDateTime ofInstant(Instant instant) {
		return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
	}
}
