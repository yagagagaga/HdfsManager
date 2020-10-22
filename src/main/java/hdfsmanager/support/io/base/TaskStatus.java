package hdfsmanager.support.io.base;

/**
 * 封装了任务的状态，一个任务只会有一个状态，要么是创建状态， 要么是准备中，要么是暂停，要么是执行中，要么是完成，要么是异常。
 * 用户手动结束任务，也属于异常的一种。
 */
public enum TaskStatus {

	CREATE(0), READY(1), PAUSE(2), PROCEED(3), FINISH(4), SUSPEND(5), ERROR(6);

	final private int level;

	TaskStatus(int level) {
		this.level = level;
	}

	public int getLevel() {
		return this.level;
	}
}
