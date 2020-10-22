package hdfsmanager.util;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.vavr.Tuple2;

/**
 * 事件循环工具类，可以提交一个任务，指定触发间隔，定时执行
 */
public final class EventLoop {

	private static final ExecutorService POOL = Executors.newFixedThreadPool(2);
	private static final List<Tuple2<Runnable, Long>> PENDING_TASKS = new ArrayList<>();

	private static final long SLOTS_IN_ONE_LOOP = 1000L;
	private static final List<List<Runnable>> TIME_RING = new ArrayList<>((int) SLOTS_IN_ONE_LOOP);

	private static final DoubleBuffer<Runnable> TASK_TO_RUN = new DoubleBuffer<>();

	private EventLoop() {
		throw new IllegalStateException("工具类不能实例化");
	}

	static {
		for (int i = 0; i < SLOTS_IN_ONE_LOOP; i++) {
			TIME_RING.add(new ArrayList<>());
		}

		// 普通的事件循环
		POOL.submit(() -> {
			int waitTime = 1;
			// noinspection InfiniteLoopStatement
			while (true) {
				List<Runnable> currentList = TASK_TO_RUN.getCurrentList();
				if (currentList.isEmpty()) {
					// 此处睡眠是为了防止进程饥饿
					waitTime = (waitTime << 1) % 127;
					sleep(waitTime);
				} else {
					currentList.forEach(Runnable::run);
					currentList.clear();
				}
				TASK_TO_RUN.switchList();
			}
		});

		// 重复任务的事件循环
		POOL.submit(() -> {
			// noinspection InfiniteLoopStatement
			while (true) {
				long l = System.currentTimeMillis() + 1000L;
				runTasks();
				addPendingTasksToTimeRing();
				sleep(l - System.currentTimeMillis());
			}
		});
	}

	private static void runTasks() {
		int countToSleep = 0;
		for (List<Runnable> tasks : EventLoop.TIME_RING) {
			if (tasks.isEmpty()) {
				countToSleep++;
			} else {
				long targetTimeMillis = System.currentTimeMillis() + countToSleep;
				tasks.forEach(Runnable::run);
				sleep(targetTimeMillis - System.currentTimeMillis());
			}
		}
	}

	private static void sleep(long millis) {
		if (millis <= 0)
			return;
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new IllegalStateException(e);
		}
	}

	private static void addPendingTasksToTimeRing() {
		PENDING_TASKS.forEach(t2 -> {
			Long howMuchTimeInOneLoop = t2._2;
			int waitingTimeEveryTrigger = (int) (SLOTS_IN_ONE_LOOP / howMuchTimeInOneLoop);
			for (int i = waitingTimeEveryTrigger; i <= SLOTS_IN_ONE_LOOP; i += waitingTimeEveryTrigger) {
				List<Runnable> tasks = TIME_RING.get(i % (int) SLOTS_IN_ONE_LOOP);
				tasks.add(t2._1);
			}
		});
		PENDING_TASKS.clear();
	}

	public synchronized static void submit(Runnable task, long howMuchTimeInOneLoop) {
		PENDING_TASKS.add(new Tuple2<>(task, howMuchTimeInOneLoop));
	}

	public synchronized static void submit(Runnable task) {
		TASK_TO_RUN.getReadyList().add(task);
	}

	private static final class DoubleBuffer<T> {
		private List<T> currentList = new ArrayList<>();
		private List<T> readyList = new ArrayList<>();

		private synchronized void switchList() {
			List<T> tmp = currentList;
			currentList = readyList;
			readyList = tmp;
		}

		private List<T> getCurrentList() {
			return currentList;
		}

		private List<T> getReadyList() {
			return readyList;
		}
	}
}
