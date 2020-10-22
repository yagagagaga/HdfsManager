package hdfsmanager.frame.main_frame.bottom_view.bottom_view;

import javax.swing.*;

import org.apache.log4j.Logger;

import hdfsmanager.HdfsContext;
import hdfsmanager.api.controller.ViewController;
import hdfsmanager.frame.main_frame.bottom_view.bottom_view.tab_view1.LogRecordViewController;
import hdfsmanager.frame.main_frame.bottom_view.bottom_view.tab_view2.MainTaskInfoDetailViewController;

public class MainBottomViewController extends ViewController {

	private static final Logger log = Logger.getLogger(MainBottomViewController.class);
	private final HdfsContext ctx;

	private JTabbedPane mainView;

	private ViewController logRecordViewController;
	private ViewController mainTaskInfoDetailViewController;

	public MainBottomViewController(HdfsContext ctx) {
		this.ctx = ctx;
		ctx.getMainModel().addObserver(this);
		init();
	}

	@Override
	protected void initView() {
		this.mainView = new JTabbedPane();
	}

	@Override
	protected void initOtherViewControllers() {
		logRecordViewController = new LogRecordViewController();
		mainTaskInfoDetailViewController = new MainTaskInfoDetailViewController(this.ctx);
	}

	@Override
	protected void initUI() {
		mainView.addTab("日志", null, logRecordViewController.getMainView(), null);
		mainView.addTab("任务详情", null, mainTaskInfoDetailViewController.getMainView(), null);
	}

	public JTabbedPane getMainView() {
		return this.mainView;
	}
}
