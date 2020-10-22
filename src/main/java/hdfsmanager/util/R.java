package hdfsmanager.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import hdfsmanager.exception.UnexpectedException;
import sun.misc.Unsafe;

/**
 * 反射工具类
 * <hr>
 * 请注意：该类的大多数方法不会抛受检异常，但会抛运行时异常。如果调用方式正确的话，是不可能抛异常的，所以如果抛异常的话，那么要改代码改到不抛异常为止
 */
@SuppressWarnings("all")
public final class R {

	private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(R.class);

	private R() {
		throw new IllegalStateException("工具类不允许被实例化");
	}

	public static Class load(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static <T> T of(Class<T> c) {
		try {
			return c.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	/**
	 * 深拷贝传入的对象
	 * <p>
	 * 要拷贝的对象应该有一个空构造器，这样才能深拷贝成功，否则会抛出运行时异常
	 * {@link InstantiationException}和{@link NoSuchMethodException}
	 *
	 * @param t 要拷贝的对象
	 * @return 一个深拷贝的副本
	 */
	@SuppressWarnings("unchecked")
	public static <T> T clone(T t) {
		final Class<T> c = (Class<T>) t.getClass();
		final T newT = of(c);
		final Field[] declaredFields = c.getDeclaredFields();
		for (Field f : declaredFields) {
			if (Modifier.isFinal(f.getModifiers()) ||
					Modifier.isStatic(f.getModifiers()))
				continue;
			setFieldValue(newT, f, getFieldValue(t, f));
		}
		return newT;
	}

	/**
	 * 反射调用方法
	 * <hr>
	 * 请注意：该方法不会抛受检异常，但会抛运行时异常。如果调用方式正确的话，是不可能抛异常的，所以如果抛异常的话，那么要改代码改到不抛异常为止
	 */
	@SuppressWarnings("unchecked")
	public static <T> T invoke(Object invoker, String methodName, Object... args) {
		Class[] classes = new Class[args.length];
		for (int i = 0; i < classes.length; i++) {
			classes[i] = args[i].getClass();
		}

		try {
			Method method = getMethod(invoker.getClass(), methodName, classes);
			method.setAccessible(true);
			return (T) method.invoke(invoker, args);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	/**
	 * 反射调用类静态方法
	 * <hr>
	 * 请注意：该方法不会抛受检异常，但会抛运行时异常。如果调用方式正确的话，是不可能抛异常的，所以如果抛异常的话，那么要改代码改到不抛异常为止
	 */
	@SuppressWarnings("unchecked")
	public static <T, N> N invoke(Class<T> c, String methodName, Object... args) {
		Class[] classes = new Class[args.length];
		for (int i = 0; i < classes.length; i++) {
			classes[i] = args[i].getClass();
		}

		try {
			Method method = getMethod(c, methodName, classes);
			method.setAccessible(true);
			return (N) method.invoke(null, args);
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public static Method getMethod(Class<?> c, String methodName, Class<?>... parameters) {
		if (c == null || methodName == null)
			throw new IllegalArgumentException("不允许传入空指针");

		try {
			return c.getDeclaredMethod(methodName, parameters);
		} catch (NoSuchMethodException e) {
			try {
				return c.getDeclaredMethod(methodName, replaceWrapClassToPrimitive(parameters));
			} catch (NoSuchMethodException e1) {
				return getMethod(c.getSuperclass(), methodName, parameters);
			}
		}
	}

	public static <Ret> Ret getAnnotationValue(Class<?> srcClass, Class<? extends Annotation> annoClass, String methodName) {
		final Annotation annotation = srcClass.getAnnotation(annoClass);
		return R.invoke(annotation, methodName);
	}

	@SuppressWarnings("unchecked")
	public static <A extends Annotation> A getAnnotation(Class<?> aClass, Class<? extends Annotation> annoClass) {
		return (A) aClass.getAnnotation(annoClass);
	}

	private static Class<?>[] replaceWrapClassToPrimitive(Class<?>[] parameters) {
		for (int i = 0; i < parameters.length; i++) {
			Class c = parameters[i];
			if (isBaseWrapType(c)) {
				parameters[i] = caseToPrimitiveClass(c);
			}
		}
		return parameters;
	}

	public static boolean isBaseWrapType(Class className) {
		return className.equals(java.lang.Integer.class) ||
				className.equals(java.lang.Byte.class) ||
				className.equals(java.lang.Long.class) ||
				className.equals(java.lang.Double.class) ||
				className.equals(java.lang.Float.class) ||
				className.equals(java.lang.Character.class) ||
				className.equals(java.lang.Short.class) ||
				className.equals(java.lang.Boolean.class);
	}

	public static Class caseToPrimitiveClass(Class wrappedClass) {
		final String wrappedClassName = wrappedClass.getName();
		switch (wrappedClassName) {
		case "java.lang.Integer":
			return int.class;
		case "java.lang.Byte":
			return byte.class;
		case "java.lang.Long":
			return long.class;
		case "java.lang.Double":
			return double.class;
		case "java.lang.Float":
			return float.class;
		case "java.lang.Character":
			return char.class;
		case "java.lang.Short":
			return short.class;
		case "java.lang.Boolean":
			return boolean.class;
		default:
			throw new IllegalArgumentException("你必需传入基本数据类型的封装类型");
		}
	}

	public static Object getFieldValue(Object owner, Field f) {
		f.setAccessible(true);
		try {
			return f.get(owner);
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public static boolean hasAnnocation(Field f, Class<? extends Annotation> annoClass) {
		return f.getAnnotation(annoClass) != null;
	}

	public static <T> Object getFieldValue(Class<T> c, String fieldName) {
		try {
			final Field f = c.getDeclaredField(fieldName);
			f.setAccessible(true);
			return f.get(null);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public static void setFieldValue(Object owner, Field f, Object value) {
		f.setAccessible(true);
		switch (f.getType().getName()) {
		case "int":
			setInt(owner, f, value);
			break;
		case "long":
			setLong(owner, f, value);
			break;
		case "byte":
			setByte(owner, f, value);
			break;
		case "short":
			setShort(owner, f, value);
			break;
		case "float":
			setFloat(owner, f, value);
			break;
		case "double":
			setDouble(owner, f, value);
			break;
		case "char":
			setChar(owner, f, value);
			break;
		case "boolean":
			setBoolean(owner, f, value);
			break;
		default:
			set(owner, f, value);
		}
	}

	public static void setInt(Object owner, Field f, Object value) {
		int target;
		if (value instanceof Integer) {
			target = (Integer) value;
		} else {
			target = Integer.valueOf(value.toString());
		}
		try {
			f.setInt(owner, target);
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void setLong(Object owner, Field f, Object value) {
		long target;
		if (value instanceof Long) {
			target = (Long) value;
		} else {
			target = Long.valueOf(value.toString());
		}
		try {
			f.setLong(owner, target);
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void setByte(Object owner, Field f, Object value) {
		byte target;
		if (value instanceof Byte) {
			target = (Byte) value;
		} else {
			target = Byte.valueOf(value.toString());
		}
		try {
			f.setByte(owner, target);
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void setShort(Object owner, Field f, Object value) {
		short target;
		if (value instanceof Short) {
			target = (Short) value;
		} else {
			target = Short.valueOf(value.toString());
		}
		try {
			f.setShort(owner, target);
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void setBoolean(Object owner, Field f, Object value) {
		boolean target;
		if (value instanceof Boolean) {
			target = (Boolean) value;
		} else {
			target = Boolean.valueOf(value.toString());
		}
		try {
			f.setBoolean(owner, target);
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void setFloat(Object owner, Field f, Object value) {
		float target;
		if (value instanceof Float) {
			target = (Float) value;
		} else {
			target = Float.valueOf(value.toString());
		}
		try {
			f.setFloat(owner, target);
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void setDouble(Object owner, Field f, Object value) {
		double target;
		if (value instanceof Double) {
			target = (Double) value;
		} else {
			target = Double.valueOf(value.toString());
		}
		try {
			f.setDouble(owner, target);
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void setChar(Object owner, Field f, Object value) {
		char target;
		if (value instanceof Character) {
			target = (Character) value;
		} else {
			final String s = value.toString();
			if (s.length() == 1) {
				target = s.toCharArray()[0];
			} else {
				throw new IllegalArgumentException("你传入的不是一个字符");
			}
		}
		try {
			f.setChar(owner, target);
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void set(Object owner, Field f, Object value) {
		try {
			final String fType = f.getType().getName();
			if (fType.equals(String.class.getName())) {
				f.set(owner, value.toString());
				return;
			}
			final String vType = value.getClass().getName();
			if (!f.getType().isAssignableFrom(value.getClass())) {
				throw new IllegalArgumentException("字段类型是" + fType + "，而传入的值的类型是" + vType + "，两者不一致");
			}

			f.set(owner, value);
		} catch (IllegalAccessException e) {
			log.error(e.getMessage(), e);
		}
	}

	public static void changeModifiers(Field f, int modifiers) {
		try {
			Field modifiersField = f.getClass().getDeclaredField("modifiers");
			modifiersField.setAccessible(true);
			setFieldValue(f, modifiersField, modifiers);
		} catch (NoSuchFieldException e) {
			throw new IllegalStateException(e.getMessage(), e);
		}
	}

	public static Unsafe getUnsafe() {
		return (Unsafe) R.getFieldValue(Unsafe.class, "theUnsafe");
	}

	@SuppressWarnings("unchecked")
	public static <T> T newInstanceUnsafely(Class<T> c) {
		try {
			return (T) getUnsafe().allocateInstance(c);
		} catch (InstantiationException e) {
			throw new UnexpectedException(e);
		}
	}
}