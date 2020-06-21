package com.sg.data.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class LocalDateTimeUtils {

	public static LocalDateTime nowUTC() {
		return LocalDateTime.now(ZoneOffset.UTC);
	}

	public static LocalDateTime plusNineHours() {
		return LocalDateTime.now(ZoneOffset.ofHours(9));
	}
	
	public static LocalDateTime ofInstant(Instant instant) {
		return LocalDateTime.ofInstant(instant, ZoneOffset.ofHours(9));
	}
}
