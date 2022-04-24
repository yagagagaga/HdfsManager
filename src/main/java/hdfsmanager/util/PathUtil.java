package hdfsmanager.util;

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
			if (name.startsWith("/"))
				return new Path(parent + name.substring(1));
			else
				return new Path(parent + name);
		} else {
			if (name.startsWith("/"))
				return new Path(parent + name);
			else
				return new Path(parent + "/" + name);
		}
	}

	public static Path completedPath(String hdfsUrl, String path) {
		if (path.startsWith(hdfsUrl)) {
			return new Path(path);
		} else {
			return addPath(hdfsUrl, path);
		}
	}

	public static boolean isEquals(Path currentPath, Path updatePath) {
		String r1 = currentPath.toUri().getPath();
		String r2 = updatePath.toUri().getPath();
		return r1.equals(r2);
	}

	public static boolean isEquals(String currentPath, String updatePath) {
		return isEquals(new Path(currentPath), new Path(updatePath));
	}
}
