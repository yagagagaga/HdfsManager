package hdfsmanager.mvc.controller;

import hdfsmanager.api.Controller;
import hdfsmanager.mvc.model.DownUploadTaskModel;
import hdfsmanager.mvc.view.MainBottomView;
import hdfsmanager.support.io.base.BaseTask;

public class MainBottomController extends Controller<DownUploadTaskModel, MainBottomView> {
	public MainBottomController() {
		super(DownUploadTaskModel.class, MainBottomView.class);
	}

	public void startTasks(BaseTask[] taskIds) {
		for (BaseTask task : taskIds) {
			if (task != null) {
				task.proceed();
			}
		}
	}

	public void pauseTasks(BaseTask[] taskIds) {
		for (BaseTask task : taskIds) {
			if (task != null) {
				task.pause();
			}
		}
	}

	public void stopTasks(BaseTask[] taskIds) {
		for (BaseTask task : taskIds) {
			if (task != null) {
				task.suspend();
			}
		}
	}

	public void removeTasks(BaseTask[] taskIds) {
		for (BaseTask task : taskIds) {
			if (task != null) {
				task.suspend();
			}
		}
	}
}
