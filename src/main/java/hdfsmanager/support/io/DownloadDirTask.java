package hdfsmanager.support.io;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;

import hdfsmanager.api.dao.HdfsDao;
import hdfsmanager.api.dao.impl.HdfsDaoImpl;
import hdfsmanager.support.io.base.BaseDirTask;
import hdfsmanager.support.io.base.TaskType;

public class DownloadDirTask extends BaseDirTask {

	/**
	 * hdfs源目录路径
	 */
	private final Path srcDirPath;

	/**
	 * 本地目录路径
	 */
	private final File dstDirPath;

	/**
	 * Hdfs文件系统
	 */
	private final HdfsDao hdfs;

	public DownloadDirTask(
			Path srcDirPath,
			File dstDirPath,
			HdfsDao hdfs) {
		this.srcDirPath = srcDirPath;
		this.dstDirPath = dstDirPath;
		this.hdfs = hdfs;
		this.taskType = TaskType.DOWNLOAD_DIR;
		this.taskName = srcDirPath.toString();
	}

	@Override
	protected void before() throws IOException {

		if (!dstDirPath.isDirectory() || !hdfs.isDirectory(srcDirPath))
			throw new IllegalArgumentException("你必须选中一个目录，不能选用文件");

		List<FileStatus> allFiles = hdfs.listAllFiles(srcDirPath, true, false);
		for (FileStatus f : allFiles) {
			getSubTasks().add(hdfs.downloadFileFromHdfs(f.getPath().toString(), dstDirPath.toString(), srcDirPath.getParent().toString()));
		}
	}

	@Override
	protected void exception() {
		// 暂时不实现该方法
	}

	@Override
	protected void finish() {
		// 暂时不实现该方法
	}

	@Override
	protected void releaseResources() {
		// 暂时不实现该方法
	}
}
