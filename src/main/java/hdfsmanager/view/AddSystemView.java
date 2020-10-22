package hdfsmanager.view;

import javax.swing.*;

import hdfsmanager.api.View;
import hdfsmanager.controller.AddSystemController;
import hdfsmanager.model.AddSystemModel;

public class AddSystemView extends View<AddSystemController, AddSystemModel> {

	private JTextField textField1;
	private JTextField textField2;
	private JButton submitButton;
	private JButton cancelButton;
	private JButton submitButton1;
	private JButton cancelButton1;
	private JTextField textField3;
	private JTextField textField4;
	private JPasswordField passwordField1;
	private JTextField textField5;
	private JButton submitButton2;
	private JButton cancelButton2;
	private JTabbedPane viewPanel;

	public AddSystemView(AddSystemController controller, AddSystemModel model) {
		super(controller, model);
	}

	@Override
	protected void createView(JComponent... externalView) {

	}

	@Override
	protected void createControls() {

	}

	@Override
	protected JComponent getMainView() {
		return viewPanel;
	}
}
