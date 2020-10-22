package hdfsmanager.frame.main_frame.bottom_view;

import javax.swing.*;

import hdfsmanager.HdfsContext;
import hdfsmanager.api.controller.ViewController;
import hdfsmanager.frame.main_frame.bottom_view.bottom_view.MainBottomViewController;
import hdfsmanager.frame.main_frame.bottom_view.left_view.MainTreeViewController;
import hdfsmanager.frame.main_frame.bottom_view.right_view.MainTableViewController;

public class MainViewController extends ViewController {

	private final HdfsContext ctx;

	private JSplitPane mainView;

	private MainTreeViewController mainTreeViewController;
	private MainTableViewController mainTableViewController;
	private MainBottomViewController mainBottomViewController;

	public MainViewController(HdfsContext ctx) {
		this.ctx = ctx;
		init();
	}

	@Override
	protected void initView() {
		mainView = new JSplitPane();
	}

	@Override
	protected void initOtherViewControllers() {
		mainTreeViewController = new MainTreeViewController(this.ctx);
		mainTableViewController = new MainTableViewController(this.ctx);
		mainBottomViewController = new MainBottomViewController(this.ctx);
	}

	@Override
	protected void initUI() {
		// 设置可移动分割线
		JSplitPane jSplitPane = new JSplitPane();
		jSplitPane.setOneTouchExpandable(true);// 让分割线显示出箭头
		jSplitPane.setContinuousLayout(true);// 操作箭头，重绘图形
		jSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);// 设置分割线方向
		jSplitPane.setLeftComponent(mainTreeViewController.getMainView());
		jSplitPane.setRightComponent(mainTableViewController.getMainView());
		jSplitPane.setDividerSize(3);// 设置分割线的宽度
		jSplitPane.setDividerLocation(300);// 设定分割线的距离左边的位置

		mainView.setOneTouchExpandable(true);
		mainView.setContinuousLayout(true);
		mainView.setOrientation(JSplitPane.VERTICAL_SPLIT);
		mainView.setTopComponent(jSplitPane);
		mainView.setBottomComponent(mainBottomViewController.getMainView());
		mainView.setDividerSize(3);
		mainView.setDividerLocation(500);
	}

	public void setFilterField(JTextField filterField) {
		mainTableViewController.setFilterField(filterField);
	}

	public void setFilterDetail(JLabel filterDetail) {
		mainTableViewController.setFilterDetail(filterDetail);
	}

	public void expandRootPath() {
		mainTreeViewController.expandRootPath();
	}

	public JSplitPane getMainView() {
		return this.mainView;
	}
}
