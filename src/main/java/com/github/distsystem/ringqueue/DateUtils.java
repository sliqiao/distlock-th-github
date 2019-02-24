package com.github.distsystem.ringqueue;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @function 日期工具类，该类仅供本包使用，不对外提供
 * @date 2019年2月24日 上午9:27:20
 * @author 李桥
 * @version 1.0
 */
class DateUtils {
	private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";

	public static String now() {
		LocalDateTime time = LocalDateTime.now();
		return time.format(DateTimeFormatter.ofPattern(DEFAULT_PATTERN));
	}

	public static String plusSeconds(int seconds) {
		LocalDateTime time = LocalDateTime.now();
		time.plusSeconds(seconds);
		return time.format(DateTimeFormatter.ofPattern(DEFAULT_PATTERN));
	}
}