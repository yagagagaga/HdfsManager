package hdfsmanager.util;

import java.util.UUID;

public final class Topics {
	private Topics() {
		throw new IllegalStateException("工具类不能被初始化");
	}

	public static final String TASKS_TOPIC = UUID.randomUUID().toString();
	public static final String REFRESH_HDFS_PATH = UUID.randomUUID().toString();
}
