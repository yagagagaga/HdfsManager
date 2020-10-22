package hdfsmanager.view;

import java.awt.*;

import javax.swing.*;

import hdfsmanager.api.View;
import hdfsmanager.controller.BatchRenameController;
import hdfsmanager.model.BatchRenameModel;
import hdfsmanager.util.GuiUtil;

public class BatchRenameView extends View<BatchRenameController, BatchRenameModel> {

	private JDialog dialog = new JDialog((Window) null);

	private JPanel contentPane;
	private JButton ok;
	private JButton cancel;
	private JTextField searchField;
	private JTextField replaceField;

	public BatchRenameView(BatchRenameController controller, BatchRenameModel model) {
		super(controller, model);
	}

	private void onOK() {
		String searchStr = searchField.getText();
		String replaceStr = replaceField.getText();
		controller.rename(searchStr, replaceStr);
	}

	private void onCancel() {
		dialog.dispose();
	}

	public void dispose() {
		dialog.dispose();
	}

	@Override
	protected void createView(JComponent... externalView) {
		dialog.setTitle("批量重命名");
		dialog.setContentPane(contentPane);
		dialog.pack();
		dialog.setResizable(false);
		dialog.setModal(true);
		dialog.getRootPane().setDefaultButton(ok);
		dialog.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		GuiUtil.setToCenterLocation(dialog);
	}

	@Override
	protected void createControls() {
		ok.addActionListener(e -> onOK());
		cancel.addActionListener(e -> onCancel());
		GuiUtil.bindEscapeAction(contentPane, e -> onCancel());
	}

	@Override
	protected JComponent getMainView() {
		return contentPane;
	}

	@Override
	protected void setVisible(boolean isVisible) {
		dialog.setVisible(isVisible);
	}

}
