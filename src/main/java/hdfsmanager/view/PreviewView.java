package hdfsmanager.view;

import java.io.UnsupportedEncodingException;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import hdfsmanager.api.View;
import hdfsmanager.controller.PreviewController;
import hdfsmanager.model.PreviewModel;
import hdfsmanager.util.GuiUtil;

public class PreviewView extends View<PreviewController, PreviewModel> {

	private JFrame viewFrame;

	private JPanel viewPanel;
	private JTextPane contentArea;
	private JTable table;
	private JComboBox<String> delimiterField;
	private JButton delimiterSubmit;
	private JComboBox<String> encodeField;
	private JButton encodeSubmit;

	public PreviewView(PreviewController controller, PreviewModel model) {
		super(controller, model);
	}

	@Override
	protected void createView(JComponent... externalView) {
		viewFrame = new JFrame("预览");
		viewFrame.setSize(900, 546);
		viewFrame.setContentPane(viewPanel);
		viewFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
	}

	@Override
	protected void createControls() {
		delimiterSubmit.addActionListener(e -> {
			String delimiter = (String) delimiterField.getSelectedItem();
			fillTableBy(contentArea.getText(), delimiter);
		});
		encodeSubmit.addActionListener(e -> {
			String charset = (String) encodeField.getSelectedItem();
			controller.changeCharset(charset);
		});
		GuiUtil.bindEscapeAction(viewPanel, e -> viewFrame.dispose());
	}

	@Override
	protected JComponent getMainView() {
		return viewPanel;
	}

	@Override
	protected void setVisible(boolean isVisible) {
		GuiUtil.setToCenterLocation(viewFrame);
		viewFrame.setVisible(isVisible);
	}

	public void show(byte[] content, String charsetName) {
		String txt;
		try {
			txt = new String(content, charsetName);
		} catch (UnsupportedEncodingException e) {
			txt = e.toString();
		}
		contentArea.setText(txt); // textArea 填充内容
		fillTableBy(txt, (String) delimiterField.getSelectedItem()); // 表格填充内容
	}

	private void fillTableBy(String content, String delimiter) {
		String[] lines = content.split("\n");
		DefaultTableModel tm = (DefaultTableModel) table.getModel();
		tm.setRowCount(0);
		tm.setColumnCount(0);
		for (String line : lines) {
			String[] split = line.split(delimiter);
			while (table.getColumnCount() < split.length) {
				tm.addColumn("c" + (table.getColumnCount() + 1));
			}
			tm.addRow(split);
		}
		table.setModel(tm);
	}

}
