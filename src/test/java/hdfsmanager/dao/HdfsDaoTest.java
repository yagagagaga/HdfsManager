package hdfsmanager.dao;

import hdfsmanager.dao.impl.HdfsDaoImpl;
import org.junit.Test;

import static org.junit.Assert.*;

public class HdfsDaoTest {

	@Test
	public void copyFile() throws Exception {
		final HdfsDaoImpl dao = new HdfsDaoImpl("hdfs://192.168.2.120", "hdfs");
		dao.copyFile("hdfs://192.168.2.120:8020/mt_wlyh/data_governance",
				"hdfs://192.168.2.120:8020/mt_wlyh/data_governance2");
	}
}