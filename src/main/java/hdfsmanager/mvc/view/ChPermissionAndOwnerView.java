package hdfsmanager.mvc.view;

import hdfsmanager.api.View;
import hdfsmanager.mvc.controller.ChPermissionAndOwnerController;
import hdfsmanager.mvc.model.HdfsModel;
import hdfsmanager.util.AnimationUtil;
import hdfsmanager.util.GuiUtil;
import org.apache.hadoop.fs.permission.FsPermission;

import javax.swing.*;

public class ChPermissionAndOwnerView extends View<ChPermissionAndOwnerController, HdfsModel> {

	private JFrame viewFrame;
	private JPanel viewPanel;

	private JButton cancelBtn;
	private JButton saveBtn;
	private JTextField ownerField;
	private JTextField groupField;
	private JTextField permissionField;
	private JLabel tip;

	public ChPermissionAndOwnerView(ChPermissionAndOwnerController controller, HdfsModel model) {
		super(controller, model);
	}

	@Override
	protected void createView(JComponent... externalView) {
		viewFrame = new JFrame("更改用户与权限");
		viewFrame.setResizable(true);
		viewFrame.setContentPane(viewPanel);
		viewFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	@Override
	protected void createControls() {
		cancelBtn.addActionListener(e -> dispose());
		saveBtn.addActionListener(e -> submit());
	}

	private void submit() {
		String owner = ownerField.getText();
		String group = groupField.getText();
		String permission = permissionField.getText();
		controller.aa(permission, owner, group);
	}

	@Override
	protected void setVisible(boolean isVisible) {
		viewFrame.pack();
		GuiUtil.setToCenterLocation(viewFrame);
		viewFrame.setVisible(isVisible);
	}

	@Override
	protected JComponent getMainView() {
		return viewPanel;
	}

	public void setInitValue(FsPermission p, String owner, String group) {
		permissionField.setText(p.toString());
		ownerField.setText(owner);
		groupField.setText(group);
	}

	public void dispose() {
		viewFrame.dispose();
	}

	public void playFailedLoginAnimation(Exception e) {
		tip.setText(e.getClass().getName() + ":" + e.getMessage());
		viewFrame.pack();
		AnimationUtil.shakeFrame(viewFrame);
	}
}
