package hdfsmanager.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class PropertiesUtils {

	private static Logger log = LoggerFactory.getLogger(PropertiesUtils.class);

	private PropertiesUtils() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	/**
	 *
	 * @param classPath 配置文件在类路径下的地址
	 * @return Properties对象或<tt>NULL</tt>
	 */
	public static Properties getProperties(String classPath) {
		checkNonNull(classPath);
		checkNonBlank(classPath);

		Properties res = new Properties();
		try (InputStream is = PropertiesUtils.class.getResourceAsStream(classPath)) {
			res.load(is);
			return res;
		} catch (IOException e) {
			log.error("读取properties文件出现异常", e);
		}
		throw new IllegalStateException("从配置文件【" + classPath + "】中加载配置失败！");
	}

	/**
	 * 获取properties文件指定的key对应的value值，获取不到就返回默认值。Properties中有getOrDefault方法，但仅限于JDK1.8。
	 * 
	 * @param p          Properties
	 * @param key        Properties文件的key
	 * @param defaultVal 获取不到key时返回的默认值
	 * @param <T>        类型根据传入默认值来推断
	 * @return value值或默认值
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getOrDefault(Properties p, String key, T defaultVal) {
		checkNonNull(p, key, defaultVal);
		checkNonBlank(key);

		String value = p.getProperty(key);
		if (value == null)
			return defaultVal;

		if (defaultVal instanceof Integer) {
			return (T) new Integer(value);
		} else if (defaultVal instanceof Long) {
			return (T) new Long(value);
		} else if (defaultVal instanceof Double) {
			return (T) new Double(value);
		} else if (defaultVal instanceof Float) {
			return (T) new Float(value);
		} else if (defaultVal instanceof Boolean) {
			return (T) new Boolean(value);
		} else if (defaultVal instanceof Short) {
			return (T) new Short(value);
		} else if (defaultVal instanceof Byte) {
			return (T) new Byte(value);
		} else if (defaultVal instanceof String) {
			return (T) value;
		}
		throw new IllegalStateException("无法解析非基础类型的数据");
	}

	public static void checkNonNull(Object... args) {
		for (Object obj : args) {
			Objects.requireNonNull(obj);
		}
	}

	public static void checkNonBlank(String... args) {
		for (String s : args) {
			if (s.length() == 0)
				throw new IllegalArgumentException("不能传入空字符串");
		}
	}
}
