package hdfsmanager.util;

import java.io.*;
import java.util.Objects;

import org.apache.log4j.Logger;

import hdfsmanager.exception.UnexpectedException;

public final class IOUtil {

	private static final int EOF = -1;
	private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;
	private static final Logger log = Logger.getLogger(IOUtil.class);

	private IOUtil() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	public static long copy(InputStream is, OutputStream os) throws IOException {
		return copy(is, os, -1);
	}

	public static long copy(InputStream is, OutputStream os, long limit) throws IOException {
		return copyLarge(is, os, new byte[DEFAULT_BUFFER_SIZE], limit);
	}

	private static long copyLarge(InputStream input, OutputStream output, byte[] buffer, long limit)
			throws IOException {
		long count = 0;
		int n;
		while (EOF != (n = input.read(buffer))) {
			if (count + n > limit && limit > 0) {
				break;
			}
			count += n;
			output.write(buffer, 0, n);
		}
		return count;
	}

	public static InputStream readResource(File filePath) {
		Objects.requireNonNull(filePath);
		try {
			return new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			throw new UnexpectedException(e);
		}
	}
}
