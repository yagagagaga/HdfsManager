package hdfsmanager.api.dao.impl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import hdfsmanager.util.PathUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.log4j.Logger;

import hdfsmanager.api.dao.HdfsDao;
import hdfsmanager.exception.UnexpectedException;
import hdfsmanager.support.io.DownloadDirTask;
import hdfsmanager.support.io.DownloadFileTask;
import hdfsmanager.support.io.UploadDirTask;
import hdfsmanager.support.io.UploadFileTask;
import hdfsmanager.util.IOUtil;

public class HdfsDaoImpl implements HdfsDao {

	private static final Logger log = Logger.getLogger(HdfsDaoImpl.class);
	private final FileSystem fs;
	private final Configuration conf;

	public HdfsDaoImpl(String url, String user) {
		conf = new Configuration();

		try {
			fs = FileSystem.get(URI.create(url), conf, user);
		} catch (IOException | InterruptedException e) {
			throw new UnexpectedException(e);
		}
	}

	/**
	 * 直接将path封装为Hadoop的Path对象，然后调用{@link #listAllFiles(Path, boolean, boolean)}
	 *
	 * @param path       路径
	 * @param recursive  是否递归
	 * @param includeDir 是否包含文件夹
	 * @return 类型为FileStatus的列表
	 */
	@Override
	public List<FileStatus> listAllFiles(String path, boolean recursive, boolean includeDir) throws IOException {
		return listAllFiles(new Path(path), recursive, includeDir);
	}

	/**
	 * 列出当前文件夹下的所有文件
	 *
	 * @param path       路径
	 * @param recursive  是否递归
	 * @param includeDir 是否包含文件夹
	 * @return 类型为FileStatus的列表
	 */
	@Override
	public List<FileStatus> listAllFiles(Path path, boolean recursive, boolean includeDir) throws IOException {
		return listAllFiles0(
				path,
				recursive,
				f -> includeDir || f.isFile() // => includeDir ? true : f.isFile()
		);
	}

	/**
	 * 列出所有文件
	 *
	 * @param path      hdfs的路径
	 * @param recursive 是否递归
	 * @param filter    过滤器
	 * @return 类型为FileStatus的列表
	 * @throws IOException 异常由调用方处理
	 */
	private List<FileStatus> listAllFiles0(
			Path path,
			boolean recursive,
			Predicate<FileStatus> filter) throws IOException {
		List<FileStatus> fileStatuses = new ArrayList<>(Arrays.asList(fs.listStatus(path)));
		List<FileStatus> result = new ArrayList<>(fileStatuses);

		if (recursive) {
			for (FileStatus fileStatus : fileStatuses) {
				if (fileStatus.isDirectory()) {
					// noinspection ConstantConditions
					result.addAll(listAllFiles0(fileStatus.getPath(), recursive, filter));
				}
			}
		}

		CollectionUtils.filter(result, o -> {
			// noinspection SimplifiableIfStatement
			if (o instanceof FileStatus) {
				return filter.test((FileStatus) o);
			}
			return true;
		});
		return result;
	}

	/**
	 * 将文件移到另一个路径
	 *
	 * @param src 源文件路径
	 * @param dst 目的路径
	 * @throws IOException 异常由调用者处理
	 */
	@Override
	public void movefile(String src, String dst) throws IOException, IllegalArgumentException {
		Path src0 = new Path(src);
		Path dst0 = new Path(dst);
		movefile(src0, dst0);
	}

	@Override
	public void movefile(Path src, Path dst) throws IOException {
		if (fs.exists(dst)) {
			fs.rename(src, PathUtil.addPath(dst, src.getName()));
		} else {
			fs.rename(src, dst);
		}
	}

	/**
	 * 将文件拷贝到另一个路径
	 *
	 * @param src 源文件路径
	 * @param dst 目的路径
	 * @throws IOException 异常由调用者处理
	 */
	@Override
	public void copyFile(String src, String dst) throws IOException {
		Path src0 = new Path(src);
		Path dst0 = new Path(dst);
		FileUtil.copy(fs, src0, fs, dst0, false, conf);
	}

	/**
	 * 重命名文件（夹）
	 *
	 * @param src 源文件（夹）
	 * @param dst 目标文件（夹）
	 * @throws IOException 异常由用户自己的处理
	 */
	@Override
	public void rename(String src, String dst) throws IOException, IllegalArgumentException {
		Path src0 = new Path(src);
		Path dst0 = new Path(dst);
		rename(src0, dst0);
	}

	@Override
	public void rename(Path src, Path dst) throws IOException {
		if (!src.getParent().equals(dst.getParent()))
			throw new IllegalArgumentException("输入的原文件和目的文件不在同一个目录下");

		movefile(src, dst);
	}

	@Override
	public void delete(String path) throws IOException {
		delete(new Path(path));
	}

	@Override
	public void delete(Path path) throws IOException {
		fs.delete(path, true);
	}

	@Override
	public void mkdir(Path target) throws IOException {
		fs.mkdirs(target);
	}

	/**
	 * 上传本地文件到Hdfs上
	 *
	 * @param localFilePath 本地文件路径
	 * @param hdfsFilePath  存放上传文件的hdfs目录路径
	 */
	@Override
	public UploadFileTask uploadFileToHdfs(
			final String localFilePath,
			final String hdfsFilePath) {
		return uploadFileToHdfs(new File(localFilePath), new Path(hdfsFilePath));
	}

	/**
	 * 上传本地文件到Hdfs上
	 *
	 * @param localFilePath 本地文件路径
	 * @param hdfsFilePath  存放上传文件的hdfs目录路径
	 * @param basePath      基准路径，详细说明见{@link #downloadFileFromHdfs(String, String, String)}
	 */
	@Override
	public UploadFileTask uploadFileToHdfs(
			final String localFilePath,
			final String hdfsFilePath,
			final String basePath) {
		return uploadFileToHdfs(localFilePath,
				hdfsFilePath + new Path(localFilePath.replace(basePath, "")).getParent());
	}

	/**
	 * 上传本地文件到Hdfs上
	 *
	 * @param localFilePath 本地文件路径
	 * @param hdfsFilePath  存放上传文件的hdfs目录路径
	 */
	@Override
	public UploadFileTask uploadFileToHdfs(
			final File localFilePath,
			final Path hdfsFilePath) {
		UploadFileTask task = new UploadFileTask(localFilePath, hdfsFilePath, fs);
		task.execute();
		return task;
	}

	/**
	 * 从本地上传目录到Hdfs上
	 *
	 * @param localFilePath 本地目录路径
	 * @param hdfsFilePath  Hdfs目录路径
	 */
	@Override
	public UploadDirTask uploadDirToHdfs(
			final String localFilePath,
			final String hdfsFilePath) {
		return uploadDirToHdfs(new File(localFilePath), new Path(hdfsFilePath));
	}

	/**
	 * 从本地上传目录到Hdfs上
	 *
	 * @param localFilePath 本地目录路径
	 * @param hdfsFilePath  Hdfs目录路径
	 */
	@Override
	public UploadDirTask uploadDirToHdfs(
			final File localFilePath,
			final Path hdfsFilePath) {
		UploadDirTask task = new UploadDirTask(localFilePath, hdfsFilePath, this);
		task.execute();
		return task;
	}

	/**
	 * 从Hdfs上下载文件到本地上
	 *
	 * @param hdfsFilePath  源文件，Hdfs文件路径
	 * @param localFilePath 存放目的文件的本地目录路径
	 */
	@Override
	public DownloadFileTask downloadFileFromHdfs(
			final String hdfsFilePath,
			final String localFilePath) {
		return downloadFileFromHdfs(new Path(hdfsFilePath), new File(localFilePath));
	}

	/**
	 * 从Hdfs上下载文件到本地上
	 *
	 * @param hdfsFilePath  源文件，Hdfs文件路径
	 * @param localFilePath 存放目的文件的本地目录路径
	 * @param basePath      基准路径，比如，假设hdfs源文件的路径是{@code /foo/bar/foobar.txt}；
	 *                      本地文件的存放路径是{@code C:\\User\Admin\Download}；
	 *                      假设存入的基准路径是{@code /foo}；
	 *                      那么文件最后在本地的存放路径为{@code C:\\User\Admin\Download\bar\foobar.txt}。
	 */
	@Override
	public DownloadFileTask downloadFileFromHdfs(
			final String hdfsFilePath,
			final String localFilePath,
			final String basePath) {
		Path src = new Path(hdfsFilePath);
		String hdfsFilePathWithoutUrl = hdfsFilePath.replace(basePath, "");
		Path hdfsFileParentPath = new Path(hdfsFilePathWithoutUrl).getParent();
		String downToTargetFilePath = localFilePath + File.separator + hdfsFileParentPath;
		File dstDir = new File(downToTargetFilePath);
		return downloadFileFromHdfs(src, dstDir);
	}

	/**
	 * 从Hdfs上下载文件到本地上
	 *
	 * @param hdfsFilePath  源文件，Hdfs文件路径
	 * @param localFilePath 存放目的文件的本地目录路径
	 */
	@Override
	public DownloadFileTask downloadFileFromHdfs(
			final Path hdfsFilePath,
			final File localFilePath) {
		DownloadFileTask task = new DownloadFileTask(hdfsFilePath, localFilePath, -1, fs);
		task.execute();
		return task;
	}

	/**
	 * 从Hdfs上下载文件的一部分（5M大小）到本地上
	 *
	 * @param hdfsFilePath  源文件，Hdfs文件路径
	 * @param localFilePath 存放目的文件的本地目录路径
	 */
	@Override
	public DownloadFileTask download5MFileFromHdfs(
			final String hdfsFilePath,
			final String localFilePath) {
		return download5MFileFromHdfs(new Path(hdfsFilePath), new File(localFilePath));
	}

	/**
	 * 从Hdfs上下载文件的一部分（5M大小）到本地上
	 *
	 * @param hdfsFilePath  源文件，Hdfs文件路径
	 * @param localFilePath 存放目的文件的本地目录路径
	 * @param basePath      基准路径，详细说明见{@link #downloadFileFromHdfs(String, String, String)}
	 */
	@Override
	public DownloadFileTask download5MFileFromHdfs(
			final String hdfsFilePath,
			final String localFilePath,
			final String basePath) {
		return download5MFileFromHdfs(hdfsFilePath,
				localFilePath + new Path(hdfsFilePath.replace(basePath, "")).getParent());
	}

	/**
	 * 从Hdfs上下载文件的一部分（5M大小）到本地上
	 *
	 * @param hdfsFilePath  源文件，Hdfs文件路径
	 * @param localFilePath 存放目的文件的本地目录路径
	 */
	@Override
	public DownloadFileTask download5MFileFromHdfs(
			final Path hdfsFilePath,
			final File localFilePath) {
		DownloadFileTask task = new DownloadFileTask(
				hdfsFilePath,
				localFilePath,
				5242880, // => 5 * 1024 * 1024
				fs);
		task.execute();
		return task;
	}

	/**
	 * 从Hdfs上下载目录到本地上
	 *
	 * @param hdfsDirPath  源目录，Hdfs文件路径
	 * @param localDirPath 目的目录，本地文件路径
	 */
	@Override
	public DownloadDirTask downloadDirFromHdfs(
			final String hdfsDirPath,
			final String localDirPath) {
		return downloadDirFromHdfs(new Path(hdfsDirPath), new File(localDirPath));
	}

	/**
	 * 从Hdfs上下载目录到本地上
	 *
	 * @param hdfsDirPath  源目录，Hdfs文件路径
	 * @param localDirPath 目的目录，本地文件路径
	 */
	@Override
	public DownloadDirTask downloadDirFromHdfs(
			final Path hdfsDirPath,
			final File localDirPath) {
		DownloadDirTask task = new DownloadDirTask(hdfsDirPath, localDirPath, this);
		task.execute();
		return task;
	}

	/**
	 * 在当前目录添加一个 _SUCCESS 文件
	 */
	@Override
	public void addSuccessFlag(Path path) throws IOException {
		addSuccessFlag(path.toString());
	}

	/**
	 * 在当前目录添加一个 _SUCCESS 文件
	 */
	@Override
	public void addSuccessFlag(String path) throws IOException {
		FSDataOutputStream os = null;
		try {
			os = fs.create(PathUtil.addPath(path, "_SUCCESS"));
		} finally {
			IOUtils.closeQuietly(os);
		}
	}

	/**
	 * 将路径封装成{@code org.apache.hadoop.fs.Path}对象，然后调用{@link #getFileOrDirSize(Path)}
	 *
	 * @see #getFileOrDirSize(Path)
	 */
	@Override
	public long getFileOrDirSize(String path) {
		return getFileOrDirSize(new Path(path));
	}

	/**
	 * 获取文件或目录的大小
	 *
	 * @param path hdfs文件或目录的路径
	 * @return 文件或目录的大小，单位是字节
	 */
	@Override
	public long getFileOrDirSize(Path path) {
		try {
			return fs.getContentSummary(path).getLength();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			return 0;
		}
	}

	@Override
	public byte[] readFile(Path path, long limit) {
		byte[] read = new byte[0];
		try (InputStream is = fs.open(path); ByteArrayOutputStream os = new ByteArrayOutputStream()) {
			IOUtil.copy(is, os, limit);
			read = os.toByteArray();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		return read;
	}

	/**
	 * 合并文件
	 */
	public void concat(final String savedFilePath, final String... otherFilePaths) {
		Objects.requireNonNull(savedFilePath);
		Objects.requireNonNull(otherFilePaths);
		if (otherFilePaths.length < 1)
			return;

		Path[] psrcs = new Path[otherFilePaths.length];
		for (int i = 0, len = otherFilePaths.length; i < len; i++) {
			psrcs[i] = new Path(otherFilePaths[i]);
		}
		try {
			Path trg = new Path(savedFilePath);
			fs.concat(trg, psrcs);
			StringBuilder msg = new StringBuilder("合并完成！已将");
			for (String s : otherFilePaths) {
				msg.append("\n\t\t").append(s);
			}
			msg.append("\n\t合并到\n\t\t").append(savedFilePath);
			log.info(msg.toString());
		} catch (IOException e) {
			log.error(e);
		}
	}

	@Override
	public boolean isDirectory(Path dst) throws IOException {
		return fs.isDirectory(dst);
	}

	public boolean exists(Path f) throws IOException {
		return fs.exists(f);
	}

	@Override
	public void changePermission(Path path, FsPermission p) throws IOException {
		fs.setPermission(path, p);
	}

	@Override
	public void changeOwner(Path path, String owner, String group) throws IOException {
		fs.setOwner(path, owner, group);
	}
}