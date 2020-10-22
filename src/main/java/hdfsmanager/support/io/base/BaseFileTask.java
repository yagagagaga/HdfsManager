package hdfsmanager.support.io.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;

public abstract class BaseFileTask extends BaseTask {

	private InputStream is;
	private OutputStream os;
	private long fileSize;

	/**
	 * 该方法封装了输入流写到输出流的操作
	 *
	 * @throws IOException 输入流写到输出流出现的异常
	 */
	@Override
	protected final void doExecute() throws IOException {
		int len;
		byte[] buffer = new byte[8192]; // = 1024 * 8
		long progressTmp = 0;
		// 只有未完成、没有异常的任务可以继续进行流的转换
		while (true) {
			// 如果没有开始，就是处于暂停状态
			if (this.getStatus() == TaskStatus.PAUSE)
				continue;
			if (this.getStatus() == TaskStatus.SUSPEND)
				break;

			// 如果只想下载某个文件的一部分，通过限定 fileSize 的大小来限定
			if (progressTmp >= fileSize) {
				progress = 1;
				return;
			}

			if ((len = is.read(buffer)) != -1) {
				os.write(buffer, 0, len);
				progressTmp += len;
				progress = progressTmp / (double) fileSize;
			} else {
				progress = 1;
			}
		}
	}

	/**
	 * 释放输入流和输出流，无论任务是否出现异常，都会在finally块里 调用此方法。
	 */
	protected final void releaseResources() {
		IOUtils.closeQuietly(is);
		IOUtils.closeQuietly(os);
	}

	/**
	 * 用来设置任务的一些属性
	 *
	 * @param is       输入流
	 * @param os       输出流
	 * @param taskName 任务名称
	 * @param fileSize 文件的大小
	 * @param taskType 任务类型
	 */
	protected void setParameter(
			InputStream is,
			OutputStream os,
			String taskName,
			long fileSize,
			TaskType taskType) {
		this.is = is;
		this.os = os;
		this.taskName = taskName;
		this.fileSize = fileSize;
		this.taskType = taskType;
	}
}