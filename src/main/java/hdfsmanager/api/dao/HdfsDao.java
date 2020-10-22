package hdfsmanager.api.dao;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;

import hdfsmanager.support.io.DownloadDirTask;
import hdfsmanager.support.io.DownloadFileTask;
import hdfsmanager.support.io.UploadDirTask;
import hdfsmanager.support.io.UploadFileTask;
import org.apache.hadoop.fs.permission.FsPermission;

public interface HdfsDao {
	List<FileStatus> listAllFiles(String path, boolean recursive, boolean includeDir) throws IOException;

	List<FileStatus> listAllFiles(Path path, boolean recursive, boolean includeDir) throws IOException;

	void movefile(String src, String dst) throws IOException;

	void movefile(Path src, Path dst) throws IOException;

	void copyFile(String src, String dst) throws IOException;

	void rename(String src, String dst) throws IOException;

	void rename(Path src, Path dst) throws IOException;

	void delete(String path) throws IOException;

	void delete(Path path) throws IOException;

	void mkdir(Path target) throws IOException;

	UploadFileTask uploadFileToHdfs(String localFilePath, String hdfsFilePath);

	UploadFileTask uploadFileToHdfs(String localFilePath, String hdfsFilePath, String basePath);

	UploadFileTask uploadFileToHdfs(File localFilePath, Path hdfsFilePath);

	UploadDirTask uploadDirToHdfs(String localFilePath, String hdfsFilePath);

	UploadDirTask uploadDirToHdfs(File localFilePath, Path hdfsFilePath);

	DownloadFileTask downloadFileFromHdfs(String hdfsFilePath, String localFilePath);

	DownloadFileTask downloadFileFromHdfs(String hdfsFilePath, String localFilePath, String basePath);

	DownloadFileTask downloadFileFromHdfs(Path hdfsFilePath, File localFilePath);

	DownloadFileTask download5MFileFromHdfs(String hdfsFilePath, String localFilePath);

	DownloadFileTask download5MFileFromHdfs(String hdfsFilePath, String localFilePath, String basePath);

	DownloadFileTask download5MFileFromHdfs(Path hdfsFilePath, File localFilePath);

	DownloadDirTask downloadDirFromHdfs(String hdfsDirPath, String localDirPath);

	DownloadDirTask downloadDirFromHdfs(Path hdfsDirPath, File localDirPath);

	void addSuccessFlag(Path path) throws IOException;

	void addSuccessFlag(String path) throws IOException;

	long getFileOrDirSize(String path);

	long getFileOrDirSize(Path path);

	byte[] readFile(Path path, long limit);

	void concat(String savedFilePath, String... otherFilePaths);

	boolean isDirectory(Path dst) throws IOException;

	void changePermission(Path path, FsPermission p) throws IOException;

	void changeOwner(Path path, String owner, String group) throws IOException;
}
