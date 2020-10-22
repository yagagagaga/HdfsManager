package hdfsmanager.support.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.hadoop.fs.FileAlreadyExistsException;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import hdfsmanager.support.io.base.BaseFileTask;
import hdfsmanager.support.io.base.TaskType;
import hdfsmanager.util.IOUtil;

public class UploadFileTask extends BaseFileTask {

	/**
	 * 本地文件路径
	 */
	private final File srcFilePath;

	/**
	 * 存放该文件的Hdfs目录
	 */
	private final Path dstFilePath;

	/**
	 * Hdfs文件系统
	 */
	private final FileSystem fs;

	public UploadFileTask(File srcFilePath, Path dstFilePath, FileSystem fs) {
		this.srcFilePath = srcFilePath;
		this.dstFilePath = dstFilePath;
		this.fs = fs;
	}

	@Override
	protected void before() throws IOException {
		if (!srcFilePath.exists())
			throw new FileNotFoundException("找不到" + srcFilePath);

		String fileName = srcFilePath.getName();

		Path targetPath = new Path(dstFilePath + "/" + fileName);
		if (fs.exists(targetPath))
			throw new FileAlreadyExistsException("【" + dstFilePath + "】Hdfs上已存在该文件，如需覆写，请先手动删除");

		this.setParameter(
				IOUtil.readResource(srcFilePath), // 输入流
				fs.create(targetPath), // 输出流
				srcFilePath.getPath(), // 文件名
				srcFilePath.length(), // 文件大小
				TaskType.UPLOAD_FILE // 任务类型
		);
	}

	@Override
	protected void exception() {
		// 暂时不实现该方法
	}

	@Override
	protected void finish() {
		// 暂时不实现该方法
	}
}