package hdfsmanager.model;

import static hdfsmanager.util.Topics.TASKS_TOPIC;

import java.util.*;

import hdfsmanager.api.Model;
import hdfsmanager.support.io.base.BaseTask;
import hdfsmanager.util.EventLoop;
import hdfsmanager.util.MessageBus;

public class DownUploadTaskModel extends Model {

	private final List<BaseTask> dataBuffer = new ArrayList<>();

	@Override
	public void initialize() {
		final MessageBus.Consumer<BaseTask> consumer = MessageBus.consumer(TASKS_TOPIC);
		// 定时刷新界面
		EventLoop.submit(this::notifyObservers, 30);
		// 实时拉取任务
		new Thread(() -> {
			while (true) {
				BaseTask task = consumer.pollUntilDone();
				dataBuffer.add(task);
			}
		}).start();
	}

	public int getDataSize() {
		return dataBuffer.size();
	}

	public synchronized List<BaseTask> getDataBuffer() {
		List<BaseTask> ret = new ArrayList<>(dataBuffer);
		dataBuffer.clear();
		return ret;
	}
}
