package cn.mastercom.bigdata.util.hadoop.hdfs;

import hdfsmanager.api.dao.HdfsDao;
import hdfsmanager.support.io.UploadFileTask;
import hdfsmanager.support.io.base.BaseTask;
import hdfsmanager.support.io.base.TaskStatus;
import hdfsmanager.support.io.DownloadDirTask;
import hdfsmanager.support.io.UploadDirTask;
import hdfsmanager.api.dao.impl.HdfsDaoImpl;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Ignore
public class TaskTest {

	private HdfsDao hdfs = new HdfsDaoImpl("hdfs://192.168.1.16:9000", System.getenv("USERNAME"));

	private static void printTaskInfo(BaseTask task) {
		while (true) {
			System.out.print(task.getTaskName() + "\t");
			System.out.print(task.getProgress() + "\t");
			System.out.print(task.getStatus());
			System.out.println();
			if (task.getStatus() == TaskStatus.ERROR || task.getStatus() == TaskStatus.FINISH) {
				try {
					TimeUnit.SECONDS.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				break;
			}
		}
	}

	@Test
	public void testDownloadFile() {
		BaseTask task = hdfs.downloadFileFromHdfs(
				"hdfs://192.168.1.16:9000/demodata/qinghai/mme/iop_semme4g_in_20181219/iop_semme4g_in_20181219000000.dat",
				"C:\\Users\\DELL\\Desktop\\");
		printTaskInfo(task);
	}

	@Test
	public void testUploadFile() {
		UploadFileTask task = hdfs.uploadFileToHdfs("C:\\Users\\DELL\\Desktop\\新建 Microsoft Excel 工作表.xlsx", "hdfs://192.168.1.31:9000/zmk2");
		printTaskInfo(task);
	}

	@Test
	public void testDownloadDir() {
		DownloadDirTask task = hdfs.downloadDirFromHdfs("hdfs://192.168.1.31:9000/zmk2/20170728", "C:\\Users\\DELL\\Desktop");
		printTaskInfo(task);
	}

	@Test
	public void testUploadDir() {
		UploadDirTask task = hdfs.uploadDirToHdfs("C:\\Users\\DELL\\Desktop\\防城港", "hdfs://192.168.1.31:9000/zmk2");
		printTaskInfo(task);
	}

	@Test
	public void testAddSuccessFlag() throws IOException {
		hdfs.addSuccessFlag("hdfs://192.168.1.31:9000/zmk2/北海");
	}
}
