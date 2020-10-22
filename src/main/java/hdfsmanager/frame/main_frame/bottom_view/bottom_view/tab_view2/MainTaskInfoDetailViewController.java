package hdfsmanager.frame.main_frame.bottom_view.bottom_view.tab_view2;

import java.awt.*;

import javax.swing.*;

import hdfsmanager.HdfsContext;
import hdfsmanager.api.controller.ViewController;
import hdfsmanager.frame.main_frame.bottom_view.bottom_view.tab_view2.bottom_table_view.TaskInfoDetailTableViewController;
import hdfsmanager.frame.main_frame.bottom_view.bottom_view.tab_view2.top_toolbar_view.TaskInfoDetailToolbarController;

public class MainTaskInfoDetailViewController extends ViewController {

	private final HdfsContext ctx;

	private JPanel mainView;

	private ViewController tableViewController;
	private ViewController toolbarController;

	public MainTaskInfoDetailViewController(HdfsContext ctx) {
		this.ctx = ctx;
		init();
	}

	@Override
	protected void initView() {
		this.mainView = new JPanel();
	}

	@Override
	protected void initOtherViewControllers() {
		toolbarController = new TaskInfoDetailToolbarController(ctx);
		tableViewController = new TaskInfoDetailTableViewController(ctx);
	}

	@Override
	protected void initUI() {
		mainView.setLayout(new BorderLayout(0, 0));
		mainView.add(toolbarController.getMainView(), BorderLayout.NORTH);
		mainView.add(tableViewController.getMainView(), BorderLayout.CENTER);
	}

	public JPanel getMainView() {
		return this.mainView;
	}
}
