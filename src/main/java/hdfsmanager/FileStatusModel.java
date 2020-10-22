package hdfsmanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Observable;

import org.apache.hadoop.fs.FileStatus;

public class FileStatusModel extends Observable {
	private String navigationPath = "";

	private List<FileStatus> files = new ArrayList<>();

	private void setNavigationPath(String navigationPath) {
		this.navigationPath = navigationPath;
	}

	private void addAll(List<FileStatus> fileStatuses) {
		files.addAll(fileStatuses);
	}

	private void setFiles(List<FileStatus> fileStatuses) {
		files.clear();
		addAll(fileStatuses);
	}

	public void update(String navigationPath, List<FileStatus> fileStatuses) {
		setNavigationPath(navigationPath);
		setFiles(fileStatuses);
	}

	public List<FileStatus> getUnmodifableFiles() {
		return Collections.unmodifiableList(files);
	}

	@Override
	public void notifyObservers() {
		setChanged();
		super.notifyObservers();
	}

	public String getNavigationPath() {
		return this.navigationPath;
	}
}
