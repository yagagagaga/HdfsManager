package hdfsmanager.view;

import static hdfsmanager.support.command.ActionEnum.*;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import hdfsmanager.support.component.RowHeaderTable;
import org.apache.hadoop.fs.FileStatus;
import org.apache.log4j.Logger;

import hdfsmanager.api.View;
import hdfsmanager.controller.HdfsTableController;
import hdfsmanager.model.HdfsModel;
import hdfsmanager.support.command.ActionEnum;
import hdfsmanager.util.*;
import io.vavr.Tuple2;

public class HdfsTableView extends View<HdfsTableController, HdfsModel> {

	private static final Logger log = Logger.getLogger(HdfsTableView.class);

	/**
	 * 表格视图文件夹的右键菜单项目
	 */
	private static final ActionEnum[] TABLE_DIR_MENU_ITEMS = {
			DOWN_FILE_AND_DIR, UP_FILE_AND_DIR,
			SEPARATOR,
			NEW_FOLDER, ADD_SUCCESS_FLAG, MERGE_FILES,
			SEPARATOR,
			RENAME_FILE, BATCH_RENAME, CUT_FILE, COPY_FILE, PASTE_FILE, DEL_FILE,
			SEPARATOR,
			COPY_PATH, COPY_TABLE_INFO, EMPOWER_777
	};

	/**
	 * 表格视图文件的右键菜单项目
	 */
	private static final ActionEnum[] TABLE_FILE_MENU_ITEMS = {
			DOWN_FILE_AND_DIR, DOWN_5M_FILE, PREVIEW_FILE,
			SEPARATOR,
			RENAME_FILE, CUT_FILE, COPY_FILE, PASTE_FILE, DEL_FILE,
			SEPARATOR,
			COPY_PATH, COPY_TABLE_INFO, EMPOWER_777
	};

	/**
	 * 表格视图文件和文件夹的右键菜单项目
	 */
	private static final ActionEnum[] TABLE_FILE_AND_DIR_MENU_ITEMS = {
			DOWN_FILE_AND_DIR,
			SEPARATOR,
			CUT_FILE, COPY_FILE, PASTE_FILE, DEL_FILE,
			SEPARATOR,
			COPY_PATH, COPY_TABLE_INFO, EMPOWER_777
	};

	private JPanel viewPanel;
	private JTable table;
	private JScrollPane tblScrollPane;

	public HdfsTableView(HdfsTableController controller, HdfsModel model) {
		super(controller, model);
	}

	@Override
	protected void createView(JComponent... externalView) {
		MyTableModel tm = new MyTableModel(
				new Object[] { "", "文件名", "大小", "用户", "权限", "修改时间" }, 0);
		table.setModel(tm);
		table.getTableHeader().setReorderingAllowed(false);
		table.getColumnModel().getColumn(0).setMaxWidth(20);
		table.getColumnModel().getColumn(0).setMinWidth(20);
		MyTableCellRenderer renderer = new MyTableCellRenderer();
		table.getColumnModel().getColumn(0).setCellRenderer(renderer);
		table.getColumnModel().getColumn(1).setCellRenderer(renderer);
		table.getColumnModel().getColumn(2).setCellRenderer(renderer);
		table.getColumnModel().getColumn(5).setCellRenderer(renderer);

		table.setRowSorter(new TableRowSorter<>(tm));
		tblScrollPane.setRowHeaderView(new RowHeaderTable(table));
	}

	@Override
	protected void createControls() {
		table.addMouseListener(this);
		table.addKeyListener(this);
		table.getSelectionModel().addListSelectionListener(this);
		new DropTarget(viewPanel, this);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() != 2) {
			return;
		}

		int row = table.getSelectedRow();
		FileStatus f = fetchFileStatusAt(row);
		if (f.isDirectory()) {
			controller.gotoPath(f.getPath());
		} else {
			controller.preview(f);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON2)
			return;
		FileStatus[] fileStatuses;
		if (table.getSelectedRows().length <= 1) {
			int rowAtPoint = table.rowAtPoint(table.getMousePosition());
			table.setRowSelectionInterval(rowAtPoint, rowAtPoint);
			fileStatuses = new FileStatus[] { transformTableInfoToFileStatus(rowAtPoint) };
		} else {
			fileStatuses = getSelectedRowsData();
		}

		Tuple2<Boolean, Boolean> haveDirOrFile = isIncludeDirOrFile(fileStatuses);
		if (haveDirOrFile._1 && haveDirOrFile._2) {
			PopupMenuUtil.show(table, controller, TABLE_FILE_AND_DIR_MENU_ITEMS, e, fileStatuses);
		} else if (haveDirOrFile._1) {
			PopupMenuUtil.show(table, controller, TABLE_DIR_MENU_ITEMS, e, fileStatuses);
		} else {
			PopupMenuUtil.show(table, controller, TABLE_FILE_MENU_ITEMS, e, fileStatuses);
		}
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		FileStatus[] selectedRowsData = getSelectedRowsData();
		controller.setSelectedFiles(selectedRowsData);
	}

	private Tuple2<Boolean, Boolean> isIncludeDirOrFile(FileStatus[] fileStatuses) {
		boolean includeDir = false;
		boolean includeFile = false;
		for (FileStatus f : fileStatuses) {
			if (f.isDirectory()) {
				includeDir = true;
			} else {
				includeFile = true;
			}
		}
		return new Tuple2<>(includeDir, includeFile);
	}

	private FileStatus transformTableInfoToFileStatus(int row) {
		// 图标，文件名，大小，用户，权限，修改时间
		return (FileStatus) table.getModel().getValueAt(table.convertRowIndexToModel(row), 1);
	}

	private FileStatus[] getSelectedRowsData() {
		final int[] rows = table.getSelectedRows();
		FileStatus[] ret = new FileStatus[rows.length];
		for (int i = 0; i < rows.length; i++) {
			ret[i] = transformTableInfoToFileStatus(rows[i]);
		}
		return ret;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			int row = table.getSelectedRow();
			FileStatus f = fetchFileStatusAt(row);
			controller.preview(f);
		}
	}

	@Override
	protected JComponent getMainView() {
		return viewPanel;
	}

	@Override
	protected void update(HdfsModel model, Object arg) {
		if (arg != HdfsModel.UpdateLevel.UI_AND_DATA)
			return;
		DefaultTableModel tm = (DefaultTableModel) table.getModel();
		addRowToTable(model.getFileStatuses(), tm);
		tblScrollPane.setRowHeaderView(new RowHeaderTable(table));
	}

	private FileStatus fetchFileStatusAt(int row) {
		int idx = table.convertRowIndexToModel(row);
		TableModel tm = table.getModel();
		return (FileStatus) tm.getValueAt(idx, 1);
	}

	private void addRowToTable(List<FileStatus> files, DefaultTableModel tm) {
		tm.setRowCount(0);
		for (FileStatus f : files) {
			Object[] data = new Object[6];
			data[0] = GuiUtil.getSystemIcon(f.getPath().getName(), f.isDirectory());
			data[1] = f;
			data[2] = f.getLen();
			data[3] = f.getOwner() + ":" + f.getGroup();
			data[4] = f.getPermission().toString();
			data[5] = f.getModificationTime();
			tm.addRow(data);
		}
	}

	@Override
	public void drop(DropTargetDropEvent dtde) {
		try {
			if (!dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
				return;
			}
			dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
			@SuppressWarnings("unchecked")
			List<File> files = (List<File>) dtde.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
			dropToUpload(files);
			dtde.dropComplete(true);
		} catch (UnsupportedFlavorException | IOException e) {
			e.printStackTrace();
		}
	}

	private void dropToUpload(List<File> files) {
		if (files.isEmpty())
			return;
		final String s = files.stream()
				.map(File::toString)
				.reduce((a, b) -> a + "\n" + b)
				.get();
		final boolean accept = DialogUtil.confirm(s, "是否上传以下文件", MsgType.INFORMATION);
		if (accept)
			controller.upload(files.toArray(new File[0]));
	}

	private static class MyTableModel extends DefaultTableModel {

		MyTableModel(Object[] columnNames, int rowCount) {
			super(columnNames, rowCount);
		}

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
	}

	private static class MyTableCellRenderer extends DefaultTableCellRenderer {
		@Override
		public Component getTableCellRendererComponent(
				JTable table,
				Object value,
				boolean isSelected,
				boolean hasFocus,
				int row,
				int column) {
			Component defaultRet = super.getTableCellRendererComponent(
					table,
					value,
					isSelected,
					hasFocus,
					row,
					column);
			if (value == null)
				return defaultRet;
			switch (column) {
			case 0:
				setIcon((Icon) value);
				setText("");
				break;
			case 1:
				FileStatus f = (FileStatus) value;
				setIcon(null);
				setText(f.getPath().getName());
				setHorizontalAlignment(JLabel.LEFT);
				break;
			case 2:
				setIcon(null);
				long len = (Long) value;
				String val = FileUtil.transformFileLengthToHumanString(len);
				setValue(val);
				setHorizontalAlignment(JLabel.RIGHT);
				break;
			case 5:
				String timestamp = DateUtil.format((Long) value, "yyyy-MM-dd HH:mm:ss");
				setIcon(null);
				setText(timestamp);
				break;
			default:
				break;
			}
			return defaultRet;
		}
	}
}
