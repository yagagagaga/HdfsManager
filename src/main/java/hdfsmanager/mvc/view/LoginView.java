package hdfsmanager.mvc.view;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.List;

import javax.swing.*;

import hdfsmanager.api.View;
import hdfsmanager.mvc.controller.LoginController;
import hdfsmanager.mvc.model.LoginModel;
import hdfsmanager.util.AnimationUtil;
import hdfsmanager.util.GuiUtil;

public class LoginView extends View<LoginController, LoginModel> {

	private JFrame viewFrame;
	private JPanel viewPanel;

	private JButton enter;
	private JButton exit;
	private JLabel tip;
	private JComboBox<String> url;
	private JTextField user;

	public LoginView(LoginController controller, LoginModel model) {
		super(controller, model);
	}

	@Override
	protected void createView(JComponent... externalView) {
		viewFrame = new JFrame("登陆");
		viewFrame.setResizable(false);
		viewFrame.setContentPane(viewPanel);
		viewFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	@Override
	protected void createControls() {
		enter.addActionListener(e -> {
			String loginUrl = (String) url.getSelectedItem();
			String loginUser = user.getText();
			controller.login(loginUrl, loginUser);
		});
		exit.addActionListener(e -> {
			dispose();
			System.exit(0);
		});
		user.addKeyListener(this);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		int keyCode = e.getKeyChar();
		switch (keyCode) {
		case KeyEvent.VK_ENTER:
			enter.doClick();
			break;
		case KeyEvent.VK_ESCAPE:
			dispose();
			break;
		default:

		}
	}

	@Override
	protected JComponent getMainView() {
		return viewPanel;
	}

	@Override
	protected void setVisible(boolean isVisible) {
		viewFrame.pack();
		GuiUtil.setToCenterLocation(viewFrame);
		viewFrame.setVisible(isVisible);
	}

	@Override
	protected void update(LoginModel model) {
		List<String> historyUrls = model.getHistoryUrls();
		if (historyUrls.isEmpty())
			return;
		url.removeAllItems();
		for (String s : historyUrls) {
			url.addItem(s);
		}

		url.setSelectedIndex(0);
		setVisible(true);
	}

	/**
	 * 播放登录失败时的动画
	 */
	public void playFailedLoginAnimation() {
		tip.setText("无效的URL");
		AnimationUtil.shakeFrame(viewFrame);
		viewFrame.pack();
	}

	/**
	 * 让登录窗口消失
	 */
	public void dispose() {
		viewFrame.dispose();
	}

}
