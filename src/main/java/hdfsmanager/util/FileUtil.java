package hdfsmanager.util;

import static org.apache.commons.io.FileUtils.ONE_KB;

import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

public final class FileUtil {

	private FileUtil() {
		throw new IllegalStateException("工具类不能实例化");
	}

	/**
	 * 计算文件大小
	 */
	public static String transformFileLengthToHumanString(long len) {
		String[] unit = { " B", "KB", "MB", "GB", "TB", "PB", "EB" };
		double ret = len;
		int idx = 0;
		while (ret >= ONE_KB && idx <= 6) {
			ret /= ONE_KB;
			idx++;
		}
		return String.format("%.2f %s", ret, unit[idx]);
	}

	public static List<String> readFileAsLine(File file) {
		try (LineNumberReader r = new LineNumberReader(new FileReader(file))) {
			return r.lines()
					.filter(s -> !s.isEmpty())
					.collect(Collectors.toList());
		} catch (IOException ignored) {
			// ignored
			ignored.printStackTrace();
		}
		return Lists.newArrayList();
	}

	public static void writeToFile(Collection<String> strings, String filePath) {
		if (filePath.isEmpty())
			return;
		writeToFile(strings, new File(filePath));
	}

	public static void writeToFile(Collection<String> strings, File file) {
		if (strings.isEmpty())
			return;

		try (PrintWriter pw = new PrintWriter(file)) {
			for (String str : strings) {
				pw.println(str);
			}
		} catch (FileNotFoundException ignored) {
			// ignored
		}
	}
}
