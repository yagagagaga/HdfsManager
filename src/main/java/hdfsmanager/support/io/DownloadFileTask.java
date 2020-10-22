package hdfsmanager.support.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import hdfsmanager.support.io.base.BaseFileTask;
import hdfsmanager.support.io.base.TaskType;

public class DownloadFileTask extends BaseFileTask {

	/**
	 * Hdfs文件路径
	 */
	private final Path srcFilePath;

	/**
	 * 本地存放该文件的路径
	 */
	private final File dstFilePath;

	/**
	 * 用于限制要下载的文件大小，如果该值小于等于0， 则表示没有限制
	 */
	private final long fileSizeLimit;

	/**
	 * Hdfs文件系统
	 */
	private final FileSystem fs;

	public DownloadFileTask(Path srcFilePath, File dstFilePath, long fileSizeLimit, FileSystem fs) {
		this.srcFilePath = srcFilePath;
		this.dstFilePath = dstFilePath;
		this.fileSizeLimit = fileSizeLimit;
		this.fs = fs;
	}

	@Override
	protected void before() throws IOException {

		if (!fs.exists(srcFilePath))
			throw new FileNotFoundException("找不到" + srcFilePath);

		String targetFilePath = dstFilePath + File.separator + srcFilePath.getName();
		File targetFile = new File(targetFilePath);
		if (targetFile.exists())
			throw new FileAlreadyExistsException("【" + dstFilePath.getPath() + "】文件已存在，如需覆写，请先删除原目录下的文件");

		File tmpFile = new File(targetFilePath + ".tmp");
		FileUtils.touch(tmpFile);

		FileStatus fileStatus = fs.getFileStatus(srcFilePath);
		this.setParameter(
				fs.open(srcFilePath), // 输入流
				new FileOutputStream(tmpFile), // 输出流
				srcFilePath.toString(), // 文件名
				fileSizeLimit <= 0 ? fileStatus.getLen() : fileSizeLimit, // 文件大小
				TaskType.DOWNLOAD_FILE // 任务类型
		);
	}

	@Override
	public void exception() {
		// 暂时不需要用到这个方法
	}

	/**
	 * 下载时文件会被添加 .tmp 后缀，下载完成后要把文件 的 .tmp 后缀给移除掉。
	 */
	@Override
	protected void finish() {
		String fileName = srcFilePath.getName();
		File target = new File(dstFilePath + File.separator + fileName);
		File tmpFile = new File(target + ".tmp");
		tmpFile.renameTo(target);
	}
}