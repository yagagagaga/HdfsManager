package hdfsmanager.support.io.base;

import java.util.Date;

public abstract class Task {

	/**
	 * 任务名称
	 */
	protected String taskName;

	/**
	 * 任务的描述信息
	 */
	protected String description;

	/**
	 * 任务的完成进度，范围从0.0到1.0
	 */
	protected double progress;

	/**
	 * 任务类型
	 */
	protected TaskType taskType = TaskType.UNKNOWN_TYPE;

	/**
	 * 任务开始执行的时间
	 */
	protected Date startTime;

	/**
	 * 任务的完成时间
	 */
	protected Date finishTime;

	// -----------------定义【任务】有以下行为-----------------------------

	/**
	 * 让任务开始执行
	 */
	public abstract void execute();

	/**
	 * 让任务中止
	 */
	public abstract void suspend();

	/**
	 * 让任务从暂停状态恢复到继续状态
	 */
	public abstract void proceed();

	/**
	 * 让任务从继续状态切换到暂停状态
	 */
	public abstract void pause();

	/**
	 * 阻塞当前线程，直到任务完成
	 */
	public abstract void waitUntilDone();

	public String getTaskName() {
		return this.taskName;
	}

	public String getDescription() {
		return this.description;
	}

	public double getProgress() {
		return this.progress;
	}

	public TaskType getTaskType() {
		return this.taskType;
	}

	public Date getStartTime() {
		return this.startTime;
	}

	public Date getFinishTime() {
		return this.finishTime;
	}
}
