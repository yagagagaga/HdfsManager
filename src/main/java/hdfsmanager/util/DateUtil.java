package hdfsmanager.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public final class DateUtil {

	private DateUtil() {
		throw new IllegalStateException("工具类不能实例化");
	}

	public static String format(Date date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}

	public static String format(long timestamp, String pattern) {
		return format(new Date(timestamp), pattern);
	}
}
