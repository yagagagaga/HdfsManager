package hdfsmanager.frame.main_frame.bottom_view.right_view;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.*;

import org.apache.log4j.Logger;

import hdfsmanager.util.FileUtil;
import hdfsmanager.util.GuiUtil;

class MainTableView extends JTable {

	static final String[] TABLE_HEADERS = { ""/* icon */, "文件名称", "大小", "用户与权限", "路径", "父路径", "最后修改日期", /* "文件大小排序(KB)" */ };
	private static final Logger log = Logger.getLogger(MainTableView.class);

	MainTableView() {
		initTableModel();
		initTableSorter();
		initTableRender();
		initLookAndFeel();
	}

	private void initTableRender() {
		// 第一列渲染为图标
		getColumnModel().getColumn(0).setCellRenderer(createFristTableCellRenderer());
		// 第一列（文件大小）渲染为带单位的值，同时右对齐
		getColumnModel().getColumn(2).setCellRenderer(createThirdTableCellRenderer());
	}

	private void initTableSorter() {
		TableRowSorter<TableModel> sorter = new TableRowSorter<>(getModel());
		setRowSorter(sorter);
	}

	private void initTableModel() {
		TableModel tableModel = createTableModel();
		setModel(tableModel);
	}

	private void initLookAndFeel() {
		GuiUtil.initLookAndFeel();

		getColumnModel().getColumn(0).setMaxWidth(20);
		getColumnModel().getColumn(0).setMinWidth(20);
		setRowHeight(20);
		GuiUtil.hideTableColumn(this, 4);// 隐藏---路径
		GuiUtil.hideTableColumn(this, 5);// 隐藏---父路径
	}

	private TableModel createTableModel() {
		return new DefaultTableModel(new Object[][] {}, TABLE_HEADERS) {

			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}

			@Override
			public Class<?> getColumnClass(int column) {
				try {
					if (getRowCount() <= 1) {
						return int.class;
					} else {
						return getValueAt(0, column).getClass();
					}
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
				return int.class;
			}
		};
	}

	private TableCellRenderer createFristTableCellRenderer() {
		return new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component defaultRet = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				if (value instanceof Icon) {
					this.setIcon((Icon) value);
					this.setText("");
				}
				return defaultRet;
			}
		};
	}

	private TableCellRenderer createThirdTableCellRenderer() {
		return new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				final Component defaultRet = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

				if (value instanceof Long) {
					setValue(FileUtil.transformFileLengthToHumanString((Long) value));
					setHorizontalAlignment(JLabel.RIGHT);
				}
				return defaultRet;
			}
		};
	}
}
