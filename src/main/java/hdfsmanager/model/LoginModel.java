package hdfsmanager.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.hadoop.fs.Path;

import hdfsmanager.api.Model;
import hdfsmanager.controller.MainController;
import hdfsmanager.util.FileUtil;

public class LoginModel extends Model {

	private static final String HISTORY_URLS_PATH = "/hdfsmanager.historyurls";
	private static final String HISTORY_NAME_PATH = "/hdfsmanager.name";

	private String name;
	private List<String> historyUrls;

	@Override
	public void initialize() {
		// 从临时目录中读取历史url
		File historyUrlsFile = new File(fetchHistoryUrlsPath(HISTORY_URLS_PATH));
		historyUrls = new ArrayList<>();
		if (!historyUrlsFile.exists()) {
			return;
		}
		// 通知视图，更新url
		historyUrls.addAll(FileUtil.readFileAsLine(historyUrlsFile));
		// 从临时目录中读取用户名
		File historyNameFile = new File(fetchHistoryUrlsPath(HISTORY_NAME_PATH));
		if (!historyUrlsFile.exists()) {
			return;
		}
		// 通知视图，更新name
		final List<String> names = FileUtil.readFileAsLine(historyNameFile);
		this.name = names.isEmpty() ? "hdfs" : names.get(0);
		notifyObservers();
	}

	private String fetchHistoryUrlsPath(String fileName) {
		String tmpdir = System.getProperty("java.io.tmpdir");
		Path path = Path.mergePaths(new Path(tmpdir), new Path(fileName));
		return path.toString();
	}

	public void login(String loginUrl, String loginUser) {

		// 尝试新建一个HdfsManager
		MainController.startWith(loginUrl, loginUser);

		// 将选项框里的数据存入本地
		historyUrls.remove(loginUrl);
		historyUrls.add(0, loginUrl);
		FileUtil.writeToFile(historyUrls, fetchHistoryUrlsPath(HISTORY_URLS_PATH));
		name = loginUser;
		FileUtil.writeToFile(Collections.singleton(name), fetchHistoryUrlsPath(HISTORY_NAME_PATH));
	}

	public String getName() {
		return name;
	}

	public List<String> getHistoryUrls() {
		return this.historyUrls;
	}
}
