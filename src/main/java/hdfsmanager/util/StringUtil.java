package hdfsmanager.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

public final class StringUtil {

	private StringUtil() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	public static String mkString(Collection<?> o, String delimiter) {
		if (o.isEmpty())
			return "";

		StringBuilder sb = new StringBuilder();
		Iterator<?> itr = o.iterator();

		while (true) {
			sb.append(itr.next().toString());
			if (itr.hasNext()) {
				sb.append(delimiter);
			} else {
				break;
			}
		}

		return sb.toString();
	}

	public static <T> String mkString(T[] array, String delimiter) {
		return mkString(Arrays.asList(array), delimiter);
	}
}
