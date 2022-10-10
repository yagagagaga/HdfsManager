package hdfsmanager.util;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * 基于生产者消费者模型实现的消息总线，用于解耦
 */
public final class MessageBus {
	private static final Map<String, BlockingQueue<Object>> TOPIC_TO_QUEUE_MAP = new HashMap<>();

	private MessageBus() {
		throw new IllegalStateException("工具类不能创建实例");
	}

	private synchronized static BlockingQueue<Object> getOrCreateQueue(String topic) {
		if (org.apache.commons.lang3.StringUtils.isEmpty(topic)) {
			throw new NullPointerException("传入的 topic 不能为空");
		}

		BlockingQueue<Object> queue = TOPIC_TO_QUEUE_MAP.get(topic);
		if (queue == null) {
			queue = new LinkedBlockingQueue<>(20);
		}
		TOPIC_TO_QUEUE_MAP.put(topic, queue);
		return queue;
	}

	public synchronized static <T> Producer<T> producer(String topic) {
		BlockingQueue<Object> queue = getOrCreateQueue(topic);
		return new Producer<>(queue);
	}

	public synchronized static <T> Consumer<T> consumer(String topic) {
		BlockingQueue<Object> queue = getOrCreateQueue(topic);
		return new Consumer<>(queue);
	}

	public static class Producer<T> {
		private final BlockingQueue<Object> queue;

		public Producer(BlockingQueue<Object> queue) {
			this.queue = queue;
		}

		public void offer(T o) {
			Objects.requireNonNull(o, "不能发送null");
			queue.offer(o);
		}
	}

	public static class Consumer<T> {
		private final BlockingQueue<Object> queue;

		public Consumer(BlockingQueue<Object> queue) {
			this.queue = queue;
		}

		public T pollUntilDone() {
			try {
				// noinspection unchecked
				return (T) queue.poll(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		public Optional<T> poll(long waitTimeInMilliseconds) {
			try {
				@SuppressWarnings("unchecked")
				T res = (T) queue.poll(waitTimeInMilliseconds, TimeUnit.MILLISECONDS);
				if (res == null) {
					return Optional.empty();
				}
				return Optional.of(res);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}

		public Optional<T> poll() {
			return poll(1L);
		}
	}
}
