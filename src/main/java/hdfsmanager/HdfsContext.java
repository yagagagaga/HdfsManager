package hdfsmanager;

import hdfsmanager.api.dao.HdfsDao;
import hdfsmanager.api.dao.impl.HdfsDaoImpl;

public class HdfsContext {
	private final HdfsDao hdfsDao;
	private final TasksModel tasksModel;
	private final FileStatusModel fileStatusModel;
	private final MainModel mainModel;

	public HdfsContext(HdfsDao hdfsDao, TasksModel tasksModel, FileStatusModel fileStatusModel, MainModel mainModel) {
		this.hdfsDao = hdfsDao;
		this.tasksModel = tasksModel;
		this.fileStatusModel = fileStatusModel;
		this.mainModel = mainModel;
	}

	public static HdfsContext of(String url, String user) {
		HdfsDao dao = new HdfsDaoImpl(url, user);
		final FileStatusModel fileStatusModel = new FileStatusModel();
		MainModel helper = new MainModel(dao, fileStatusModel);
		return new HdfsContext(dao, new TasksModel(), fileStatusModel, helper);
	}

	public HdfsDao getHdfsDao() {
		return this.hdfsDao;
	}

	public TasksModel getTasksModel() {
		return this.tasksModel;
	}

	public FileStatusModel getFileStatusModel() {
		return this.fileStatusModel;
	}

	public MainModel getMainModel() {
		return this.mainModel;
	}
}
