package hdfsmanager.support.io.base;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

public abstract class BaseTask extends Task implements Runnable {
	private static final ExecutorService DEFAULT_EXECUTOR_SERVICE = Executors
			.newFixedThreadPool(Math.min(Runtime.getRuntime().availableProcessors(), 10));
	private static final Logger log = Logger.getLogger(BaseTask.class);

	/**
	 * 任务的状态
	 */
	private volatile TaskStatus status = TaskStatus.CREATE;

	/**
	 * 任务开始执行之前，子类可以实现此方法进行一些操作
	 *
	 * @throws IOException 该方法如果抛出异常，则任务会被异常打断
	 */
	protected abstract void before() throws IOException;

	/**
	 * 该方法用于封装任务执行的所有细节
	 *
	 * @throws IOException 该方法如果抛出异常，则任务会被异常打断
	 */
	protected abstract void doExecute() throws IOException;

	/**
	 * 当任务发生异常时，会调用此方法，子类可以实现此方法 在任务执行过程中出现异常时进行一些处理。
	 */
	protected abstract void exception();

	/**
	 * 当任务完成之后，会调用此方法，即使任务在执行过程中出现了异常， 此方法也会被调用，且该方法不允许抛出异常。
	 */
	protected abstract void finish();

	/**
	 * 无论任务是否完成或异常，任务结束后，都会调用该方法进行资源回收， 该方法不允许抛出异常。
	 */
	protected abstract void releaseResources();

	@Override
	public final void execute() {
		DEFAULT_EXECUTOR_SERVICE.execute(this);
	}

	@Override
	public final void pause() {
		if (this.status == TaskStatus.PROCEED)
			this.status = TaskStatus.PAUSE;
	}

	@Override
	public final void proceed() {
		if (this.status == TaskStatus.PAUSE)
			this.status = TaskStatus.PROCEED;
	}

	@Override
	public final void suspend() {
		this.status = TaskStatus.SUSPEND;
	}

	@Override
	public void waitUntilDone() {
		while (true) {
			if (this.status == TaskStatus.FINISH ||
					this.status == TaskStatus.ERROR)
				return;

			try {
				TimeUnit.MILLISECONDS.sleep(200);
			} catch (InterruptedException e) {
				log.error(e.getMessage(), e);
				Thread.currentThread().interrupt();
			}
		}
	}

	@Override
	public final void run() {
		this.startTime = new Date();
		try {
			this.status = TaskStatus.READY;
			before();
			this.status = TaskStatus.PROCEED;
			doExecute();
			if (this.status == TaskStatus.PROCEED)
				this.status = TaskStatus.FINISH;
		} catch (Exception e) {
			this.status = TaskStatus.ERROR;
			log.error(e.getMessage(), e);
			exception();
		} finally {
			releaseResources();
			this.finishTime = new Date();
			finish();
		}
	}

	public TaskStatus getStatus() {
		return this.status;
	}
}
