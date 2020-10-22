package hdfsmanager.frame.main_frame.bottom_view.bottom_view.tab_view1;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Observable;

import javax.swing.*;

import hdfsmanager.api.controller.ViewController;
import hdfsmanager.util.GuiUtil;

public class LogRecordViewController extends ViewController {

	private JScrollPane mainView;
	private JTextArea logRecordView;

	public LogRecordViewController() {
		init();
	}

	@Override
	protected void initView() {
		this.mainView = new JScrollPane();
		logRecordView = new LogRecordView();
	}

	@Override
	protected void initOtherViewControllers() {
		// 本视图控制器不依赖其他视图控制器
	}

	@Override
	protected void initUI() {
		mainView.setViewportView(logRecordView);
	}

	@Override
	protected void initLookAndFeel() {
		GuiUtil.initLookAndFeel();
		logRecordView.setEditable(false);
		logRecordView.setLineWrap(true);
		logRecordView.setFont(java.awt.Font.decode("宋体"));
		logRecordView.setText("");
	}

	@Override
	protected void initEventListener() {
		// 没有事件监听
	}

	@Override
	protected void initActionListener() {
		final JPopupMenu popupText = new JPopupMenu();
		JMenuItem menuItem = new JMenuItem("清空");
		menuItem.addActionListener(e -> logRecordView.setText(""));
		popupText.add(menuItem);

		logRecordView.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
				if (e.isPopupTrigger()) {
					popupText.show(e.getComponent(), e.getX(), e.getY());
				}
			}
		});
	}

	@Override
	public void update(Observable o, Object arg) {
		// 暂不实现
	}

	public JScrollPane getMainView() {
		return this.mainView;
	}
}
