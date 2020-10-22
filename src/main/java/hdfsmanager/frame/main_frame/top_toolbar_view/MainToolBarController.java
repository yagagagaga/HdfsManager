package hdfsmanager.frame.main_frame.top_toolbar_view;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Observable;

import javax.swing.*;

import hdfsmanager.HdfsContext;
import hdfsmanager.MainModel;
import hdfsmanager.api.controller.ViewController;
import hdfsmanager.support.command.GotoPathCmd;
import hdfsmanager.util.GuiUtil;
import org.apache.hadoop.fs.Path;

public class MainToolBarController extends ViewController {

	private HdfsContext ctx;

	private MainToolbar mainView;

	private final ItemListener selectedItemListener = e -> {
		if (e.getStateChange() == ItemEvent.SELECTED) {
			String keyWord = e.getItem().toString();
			ctx.getMainModel().executeCmd(new GotoPathCmd(
					new Path(keyWord),
					new Path(ctx.getFileStatusModel().getNavigationPath()),
					path -> ctx.getMainModel().notification(path.toString())));
		}
	};

	public MainToolBarController(HdfsContext ctx) {
		this.ctx = ctx;
		ctx.getMainModel().addObserver(this);
		init();
	}

	public JTextField getFilterField() {
		return mainView.getFilterField();
	}

	public JLabel getFilterDetail() {
		return mainView.getFilterDetail();
	}

	@Override
	protected void initView() {
		mainView = new MainToolbar();
	}

	@Override
	protected void initOtherViewControllers() {
		// 本视图控制器不依赖其他视图控制器
	}

	@Override
	protected void initUI() {
		// do nothing
	}

	@Override
	protected void initLookAndFeel() {
		GuiUtil.initLookAndFeel();
		mainView.setFloatable(false);
	}

	@Override
	protected void initEventListener() {

		mainView.addUndoAction(e -> this.ctx.getMainModel().undo());
		mainView.addRedoAction(e -> this.ctx.getMainModel().redo());
		mainView.addSearchbarItemListener(selectedItemListener);
		mainView.addEnterAction(e -> {
			String keyWord = mainView.getSearchbarText();
			this.ctx.getMainModel().executeCmd(new GotoPathCmd(
					new Path(keyWord),
					new Path(this.ctx.getFileStatusModel().getNavigationPath()),
					path -> this.ctx.getMainModel().notification(path.toString())));
		});
		mainView.addRefreshAction(e -> this.ctx.getMainModel().refresh());
	}

	@Override
	protected void initActionListener() {
		// 暂时没有动作需要实现
	}

	@Override
	public void update(Observable o, Object arg) {
		if (o instanceof MainModel
				&& arg instanceof String) {
			// 移除选中事件，因为每次移除（插入）item时都有可能触发选中事件
			mainView.removeSearchbarRemoveItemListener(selectedItemListener);

			String value = (String) arg;

			DefaultComboBoxModel<String> model = mainView.getSearchbarComboBoxModel();

			// 去掉重复的记录
			for (int i = model.getSize() - 1; i >= 0; i--) {
				if (model.getElementAt(i).equals(value)) {
					model.removeElementAt(i);
				}
			}

			// 只保留最近15条记录
			while (mainView.getSearchbarItemCount() > 15) {
				mainView.removeSearchbarItemAt(mainView.getSearchbarItemCount() - 1);
			}

			model.insertElementAt(value, 0);
			mainView.setSearchbarSelectedItem(value);
			mainView.addSearchbarItemListener(selectedItemListener);
		}
	}

	public MainToolbar getMainView() {
		return this.mainView;
	}
}