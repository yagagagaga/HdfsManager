package hdfsmanager.util;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.Path;

public final class PathUtil {
	private PathUtil() {
		throw new IllegalStateException("工具类不能初始化");
	}

	public static Path addPath(Path parent, String name) {
		String p = parent.toString();
		return addPath(p, name);
	}

	public static Path addPath(String parent, String name) {
		if (parent.endsWith("/")) {
			return new Path(parent + name);
		} else {
			return new Path(parent + "/" + name);
		}
	}

	public static boolean isEquals(Path currentPath, Path updatePath) {
		String r1 = currentPath.toUri().getPath();
		String r2 = updatePath.toUri().getPath();
		return r1.equals(r2);
	}
}
