package hdfsmanager.frame.main_frame.glass_view;

import hdfsmanager.api.controller.ViewController;

public class MainGlassViewController extends ViewController {

	private MainGlassView mainView;

	public MainGlassViewController() {
		init();
	}

	@Override
	protected void initView() {
		mainView = new MainGlassView();
	}

	@Override
	protected void initUI() {
		// 没ui可初始化
	}

	public MainGlassView getMainView() {
		return this.mainView;
	}
}
