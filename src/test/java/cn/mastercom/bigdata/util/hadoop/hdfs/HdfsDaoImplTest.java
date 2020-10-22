package cn.mastercom.bigdata.util.hadoop.hdfs;

import hdfsmanager.api.dao.HdfsDao;
import hdfsmanager.api.dao.impl.HdfsDaoImpl;
import hdfsmanager.support.io.DownloadDirTask;
import hdfsmanager.support.io.DownloadFileTask;
import hdfsmanager.support.io.UploadDirTask;
import hdfsmanager.support.io.UploadFileTask;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@Ignore
public class HdfsDaoImplTest {

	private static final Logger log = Logger.getLogger(HdfsDaoImplTest.class);
	private final String HDFS_URL = "hdfs://localhost:9000/";
	private File tmpFile;
	private File tmpDir;

	private HdfsDao ho;

	@Before
	public void setUp() throws Exception {
		ho = new HdfsDaoImpl(HDFS_URL, System.getenv("USERNAME"));
		tmpFile = File.createTempFile("test-", ".tmp");
		tmpDir = new File(tmpFile.getParentFile().toPath().toString() + "/test-tmpdir");
		FileUtils.forceMkdir(tmpDir);
		for (int i = 0; i < 30; i++) {
			File srcFile = File.createTempFile("test-" + i + "-", ".tmp");
			File dstFile = new File(tmpDir.toPath().toString() + File.separator + srcFile.getName());
			FileUtils.moveFile(srcFile, dstFile);
		}
	}

	@After
	public void tearDown() throws Exception {
		FileUtils.deleteQuietly(tmpFile);
		FileUtils.deleteDirectory(tmpDir);
	}

	@Test
	public void listAllFiles() {
		try {
			List<FileStatus> list = ho.listAllFiles("/", false, true);
			for (FileStatus fs : list) {
				assertEquals(HDFS_URL, fs.getPath().getParent().toString());
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			fail();
		}
	}

	@Test
	public void movefile() {

	}

	@Test
	public void copyFile() {
	}

	@Test
	public void rename() {
	}

	private static void assertExistsThenDelete(HdfsDaoImpl ho, Path f) {
		try {
			assert ho.exists(f);
			ho.delete(f);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			fail();
		}
	}

	@Test
	public void uploadFileToHdfs() {
		Path f = new Path("/zmk2/" + tmpFile.getName());
		UploadFileTask task = ho.uploadFileToHdfs(tmpFile, f.getParent());
		task.waitUntilDone();
		assertExistsThenDelete((HdfsDaoImpl) ho, f);
	}

	@Test
	public void a() {
		UploadFileTask task = ho.uploadFileToHdfs(new File("/Users/zhangminke/Downloads/作业连接.docx"), new Path("/作业连接.docx"));
		task.waitUntilDone();
	}

	@Test
	public void uploadDirToHdfs() {
		Path f = new Path("/zmk2/" + tmpDir.getName());
		UploadDirTask task = ho.uploadDirToHdfs(tmpDir, f.getParent());
		task.waitUntilDone();
		assertExistsThenDelete((HdfsDaoImpl) ho, f);
	}

	private static void assertExistsThenDelete(File f) {
		try {
			assert f.exists();
			FileUtils.deleteQuietly(f);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			fail();
		}
	}

	private static List<FileStatus> getTestFiles(HdfsDao ho) throws IOException {
		List<FileStatus> list = ho.listAllFiles("/zmk2", false, false)
				.stream()
				.filter(fs -> fs.getLen() <= 10 * 1024 * 1024)
				.collect(Collectors.toList());
		if (list.isEmpty()) {
			log.error("没有10M以下的文件可供测试");
			fail();
		}
		return list;
	}

	@Test
	public void downloadFileFromHdfs() {
		try {
			List<FileStatus> list = getTestFiles(ho);
			DownloadFileTask task = ho.downloadFileFromHdfs(list.get(0).getPath(), tmpDir);
			task.waitUntilDone();
			assertExistsThenDelete(new File(tmpDir.toPath().toString() + File.separator + list.get(0).getPath().getName()));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			fail();
		}
	}

	@Test
	public void download5MFileFromHdfs() {
		try {
			List<FileStatus> list = getTestFiles(ho);
			DownloadFileTask task = ho.download5MFileFromHdfs(list.get(0).getPath(), tmpDir);
			task.waitUntilDone();
			assertExistsThenDelete(new File(tmpDir.toPath().toString() + File.separator + list.get(0).getPath().getName()));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			fail();
		}
	}

	@Test
	public void downloadDirFromHdfs() {
		try {
			List<FileStatus> list = ho.listAllFiles("/zmk2", false, true)
					.stream()
					.filter(FileStatus::isDirectory)
					.filter(fs -> {
						long len = ho.getFileOrDirSize(fs.getPath());
						return len <= 10 * 1024 * 1024 && len > 0;
					})
					.collect(Collectors.toList());
			if (list.isEmpty()) {
				log.error("没有10M以下的文件夹可供测试");
				fail();
			}
			DownloadDirTask task = ho.downloadDirFromHdfs(list.get(0).getPath(), tmpDir);
			task.waitUntilDone();
			assertExistsThenDelete(new File(tmpDir.toPath().toString() + File.separator + list.get(0).getPath().getName()));
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			fail();
		}
	}
}