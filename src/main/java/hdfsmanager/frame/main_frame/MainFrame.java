package hdfsmanager.frame.main_frame;

import java.awt.*;

import javax.swing.*;

import hdfsmanager.HdfsContext;
import hdfsmanager.frame.main_frame.bottom_view.MainViewController;
import hdfsmanager.frame.main_frame.glass_view.MainGlassViewController;
import hdfsmanager.frame.main_frame.top_toolbar_view.MainToolBarController;
import hdfsmanager.util.GuiUtil;
import hdfsmanager.util.ResourcesDepository;

public class MainFrame extends JFrame {

	private final transient HdfsContext ctx;

	private transient MainViewController mainViewController;
	private transient MainToolBarController mainToolBarController;
	private transient MainGlassViewController mainGlassViewController;

	private MainFrame(HdfsContext ctx) {
		this.ctx = ctx;

		initUI();
		initLayout();
		initLookAndFeel();

		mainViewController.expandRootPath();
	}

	/**
	 * 初始应用程序主界面
	 */
	private void initUI() {

		mainViewController = new MainViewController(ctx);
		mainToolBarController = new MainToolBarController(ctx);
		mainGlassViewController = new MainGlassViewController();

		mainViewController.setFilterField(mainToolBarController.getFilterField());
		mainViewController.setFilterDetail(mainToolBarController.getFilterDetail());

		setTitle("HDFS 管理器");
		setSize(1000, 600);
		setExtendedState(Frame.MAXIMIZED_BOTH);
		setVisible(true);
		setIconImage(ResourcesDepository.getImage("images/logo2.gif"));
		setGlassPane(mainGlassViewController.getMainView());
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		ctx.getMainModel().setLoadingPane(mainGlassViewController.getMainView());
	}

	/**
	 * 初始化主界面布局
	 */
	private void initLayout() {

		// 工具栏
		getContentPane().add(BorderLayout.NORTH, mainToolBarController.getMainView());
		// 主视图
		getContentPane().add(BorderLayout.CENTER, mainViewController.getMainView());
	}

	/**
	 * 初始化主界面的字体等ui风格
	 */
	private void initLookAndFeel() {
		GuiUtil.initLookAndFeel();
	}

	public static void startWith(HdfsContext ctx) {
		new MainFrame(ctx);
	}

	public HdfsContext getCtx() {
		return this.ctx;
	}
}