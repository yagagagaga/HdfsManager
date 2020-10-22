package hdfsmanager.mvc.model;

import java.io.IOException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import hdfsmanager.api.Model;
import hdfsmanager.api.dao.HdfsDao;
import hdfsmanager.util.PathUtil;

public class BatchRenameModel extends Model {

	private static final Logger log = Logger.getLogger(BatchRenameModel.class);
	private final HdfsDao dao;
	private final FileStatus[] statuses;

	public BatchRenameModel(HdfsDao dao, FileStatus[] statuses) {
		this.dao = dao;
		this.statuses = statuses;
	}

	@Override
	public void initialize() {
		// do nothing
	}

	public void rename(String searchStr, String replaceStr) {
		try {
			List<FileStatus> allFiles = dao.listAllFiles(statuses[0].getPath(), false, false);
			for (FileStatus f : allFiles) {
				Path target = f.getPath();
				String targetFileName = target.getName();
				if (targetFileName.contains(searchStr)) {
					String newFileName = StringUtils.replace(targetFileName, searchStr, replaceStr);
					String newFilePath = PathUtil.addPath(target.getParent(), newFileName).toString();
					dao.rename(target.toString(), newFilePath);
				}
			}
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}
}
