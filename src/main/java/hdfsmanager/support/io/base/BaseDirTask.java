package hdfsmanager.support.io.base;

import java.util.ArrayList;
import java.util.List;

import hdfsmanager.exception.UnexpectedException;
import io.vavr.Tuple;
import io.vavr.Tuple2;

public abstract class BaseDirTask extends BaseTask {

	/**
	 * 子任务
	 */
	private final List<BaseFileTask> subTasks = new ArrayList<>();

	private int countCompletedTasks = 0;
	private int countErrorTasks = 0;

	@Override
	protected final void doExecute() {
		while (true) {

			// 如果任务还没准备好的话，就不能遍历子任务，
			// 防止List被多线程修改和遍历导致 ConcurrentModificationException
			if (this.getStatus().getLevel() < TaskStatus.READY.getLevel())
				continue;

			int countCompletedTasksTmp = 0;
			int countErrorTasksTmp = 0;

			for (BaseFileTask task : subTasks) {
				Tuple2<Integer, Integer> completedAndError = doExecute0(task);
				countCompletedTasksTmp += completedAndError._1;
				countErrorTasksTmp += completedAndError._2;
			}

			this.countCompletedTasks = countCompletedTasksTmp;
			this.countErrorTasks = countErrorTasksTmp;
			this.progress = (countCompletedTasksTmp + countErrorTasksTmp) / (double) subTasks.size();

			if (countCompletedTasks + countErrorTasks == subTasks.size()) {
				if (countErrorTasks > 0)
					throw new UnexpectedException("有" + countErrorTasks + "个任务出现异常");
				else
					return;
			}
		}
	}

	private Tuple2<Integer, Integer> doExecute0(BaseFileTask task) {
		int countCompletedTasksTmp = 0;
		int countErrorTasksTmp = 0;
		switch (this.getStatus()) {
		case PAUSE:
			task.pause();
			break;
		case PROCEED:
			task.proceed();
			break;
		case SUSPEND:
		case ERROR:
			task.suspend();
			break;
		default:
			throw new IllegalStateException("不存在这个状态");
		}

		TaskStatus subTaskStatus = task.getStatus();
		if (subTaskStatus == TaskStatus.FINISH) {
			countCompletedTasksTmp++;
		} else if (subTaskStatus == TaskStatus.ERROR) {
			countErrorTasksTmp++;
		}
		return Tuple.of(countCompletedTasksTmp, countErrorTasksTmp);
	}

	public List<BaseFileTask> getSubTasks() {
		return this.subTasks;
	}

	public int getCountCompletedTasks() {
		return this.countCompletedTasks;
	}

	public int getCountErrorTasks() {
		return this.countErrorTasks;
	}
}
