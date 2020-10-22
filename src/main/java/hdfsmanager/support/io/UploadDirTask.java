package hdfsmanager.support.io;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.fs.Path;

import hdfsmanager.dao.HdfsDao;
import hdfsmanager.support.io.base.BaseDirTask;
import hdfsmanager.support.io.base.TaskType;

public class UploadDirTask extends BaseDirTask {

	/**
	 * 本地源目录路径
	 */
	private final File srcDirPath;

	/**
	 * hdfs目录路径
	 */
	private final Path dstDirPath;

	/**
	 * Hdfs文件系统
	 */
	private final HdfsDao hdfs;

	public UploadDirTask(
			File srcDirPath,
			Path dstDirPath,
			HdfsDao hdfs) {
		this.srcDirPath = srcDirPath;
		this.dstDirPath = dstDirPath;
		this.hdfs = hdfs;
		this.taskType = TaskType.UPLOAD_DIR;
		this.taskName = srcDirPath.toString();
	}

	@Override
	protected void before() throws IOException {

		if (!srcDirPath.isDirectory() || !hdfs.isDirectory(dstDirPath))
			throw new IllegalArgumentException("你必须选中一个目录，不能选用文件");
		Collection<File> allFiles = FileUtils.listFiles(srcDirPath, null, true);
		allFiles.forEach(f -> getSubTasks()
				.add(
						hdfs.uploadFileToHdfs(f.getPath(),
								dstDirPath.toString().replaceAll("\\\\", "/"),
								srcDirPath.getParent())));
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
