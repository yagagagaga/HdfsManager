package hdfsmanager.util;

import java.util.HashMap;
import java.util.Map;

public final class Cache {

	private Cache() {
		throw new IllegalStateException("工具类不能实例化");
	}

	private static final Map<Object, Object> CACHE_FIELD = new HashMap<>();

	@SuppressWarnings("unchecked")
	public static <T> T get(Object key) {
		return (T) CACHE_FIELD.get(key);
	}

	public static void set(Object key, Object value) {
		CACHE_FIELD.put(key, value);
	}
}
