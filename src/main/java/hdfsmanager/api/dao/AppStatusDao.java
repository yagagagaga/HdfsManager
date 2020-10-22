package hdfsmanager.api.dao;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;

import java.util.List;

public interface AppStatusDao {
	Path getCurrentPath();

	List<FileStatus> getFilesInCurrentPath();

	FileStatus[] getSelectedFileStatuses();

	PasteStatus getPasteStatus();

	void setCurrentPathAndFiles(Path currentPath, List<FileStatus> filesInCurrentPath);

	boolean isCurrentPathEqualsTo(Path aPath);

	void setSelectedFileStatuses(FileStatus[] fileStatuses);

	void setPasteStatus(PasteStatus status);

	enum PasteStatus {
		COPY, CUT, NONE
	}

}
