package hdfsmanager.frame.main_frame.bottom_view.bottom_view.tab_view2.top_toolbar_view;

import java.util.Observable;

import javax.swing.*;

import hdfsmanager.HdfsContext;
import hdfsmanager.api.controller.ViewController;
import hdfsmanager.support.io.base.BaseTask;

public class TaskInfoDetailToolbarController extends ViewController {

	private final HdfsContext ctx;

	private TaskInfoDetailToolbar mainView;

	public TaskInfoDetailToolbarController(HdfsContext ctx) {
		this.ctx = ctx;
		init();
	}

	@Override
	protected void initModel() {
		ctx.getTasksModel().addObserver(this);
	}

	@Override
	protected void initView() {
		mainView = new TaskInfoDetailToolbar();
	}

	@Override
	protected void initUI() {
		// 没有初始化的东西
	}

	@Override
	protected void initEventListener() {
		mainView.addStartButtonAction(e -> {
			int[] selectedTaskId = this.ctx.getTasksModel().getSelectedTaskIds();
			for (int taskId : selectedTaskId) {
				BaseTask task = this.ctx.getTasksModel().get(taskId);
				if (task != null) {
					task.proceed();
				}
			}
		});
		mainView.addPauseButtonAction(e -> {
			int[] selectedTaskId = this.ctx.getTasksModel().getSelectedTaskIds();
			for (int taskId : selectedTaskId) {
				BaseTask task = this.ctx.getTasksModel().get(taskId);
				if (task != null) {
					task.pause();
				}
			}
		});
		mainView.addSuspendButtonAction(e -> {
			int[] selectedTaskId = this.ctx.getTasksModel().getSelectedTaskIds();
			for (int taskId : selectedTaskId) {
				BaseTask task = this.ctx.getTasksModel().get(taskId);
				if (task != null) {
					task.suspend();
				}
			}
		});
		mainView.addRemoveButtonAction(e -> {
			int[] selectedTaskId = this.ctx.getTasksModel().getSelectedTaskIds();
			for (int taskId : selectedTaskId) {
				BaseTask task = this.ctx.getTasksModel().get(taskId);
				if (task != null) {
					task.suspend();
				}
				this.ctx.getTasksModel().remove(taskId);
			}
		});
		mainView.addSelectAllButtonAction(e -> {
			this.ctx.getTasksModel().setTriggerSelectAll(true);
			this.ctx.getTasksModel().notifyObservers();
		});
		mainView.addInverseSelButtonAction(e -> {
			this.ctx.getTasksModel().setTriggerInverseSel(true);
			this.ctx.getTasksModel().notifyObservers();
		});
	}

	@Override
	public void update(Observable o, Object arg) {
		JProgressBar pBar = mainView.getTotalProgressBar();
		pBar.setMinimum(ctx.getTasksModel().getMinimum());
		pBar.setMaximum(ctx.getTasksModel().getMaximum());
		pBar.setValue(ctx.getTasksModel().getValue());
	}

	public TaskInfoDetailToolbar getMainView() {
		return this.mainView;
	}
}
