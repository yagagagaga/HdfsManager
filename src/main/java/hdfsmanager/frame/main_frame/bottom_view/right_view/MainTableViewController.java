package hdfsmanager.frame.main_frame.bottom_view.right_view;

import static hdfsmanager.support.command.ActionEnum.*;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Observable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import hdfsmanager.HdfsContext;
import hdfsmanager.MainModel;
import hdfsmanager.api.controller.ViewController;
import hdfsmanager.support.command.ActionEnum;
import hdfsmanager.support.command.GotoPathCmd;
import hdfsmanager.util.PopupMenuUtil;
import hdfsmanager.support.component.RowHeaderTable;
import hdfsmanager.util.FileUtil;
import hdfsmanager.util.GuiUtil;
import io.vavr.Tuple2;

public class MainTableViewController extends ViewController {

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
			COPY_PATH, COPY_TABLE_INFO
	};

	/**
	 * 表格视图文件的右键菜单项目
	 */
	private static final ActionEnum[] TABLE_FILE_MENU_ITEMS = {
			DOWN_FILE_AND_DIR, DOWN_5M_FILE, PREVIEW_FILE,
			SEPARATOR,
			RENAME_FILE, CUT_FILE, COPY_FILE, PASTE_FILE, DEL_FILE,
			SEPARATOR,
			COPY_PATH, COPY_TABLE_INFO
	};

	/**
	 * 表格视图文件和文件夹的右键菜单项目
	 */
	private static final ActionEnum[] TABLE_FILE_AND_DIR_MENU_ITEMS = {
			DOWN_FILE_AND_DIR,
			SEPARATOR,
			CUT_FILE, COPY_FILE, PASTE_FILE, DEL_FILE,
			SEPARATOR,
			COPY_PATH, COPY_TABLE_INFO
	};
	private static final Logger log = Logger.getLogger(MainTableViewController.class);

	private final HdfsContext ctx;

	private JScrollPane mainView;

	private MainTableView table;
	private JTextField filterField;
	private JLabel filterDetail;

	public MainTableViewController(HdfsContext ctx) {
		this.ctx = ctx;
		ctx.getMainModel().addObserver(this);
		init();
	}

	/**
	 * 延迟绑定事件
	 */
	public void setFilterField(JTextField filterField) {
		this.filterField = filterField;
		this.filterField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					mainView.setRowHeaderView(null);
					filterRow(MainTableViewController.this.filterField.getText());
					if (table.getRowCount() != 0)
						mainView.setRowHeaderView(new RowHeaderTable(table));
				}
			}
		});
	}

	public void setFilterDetail(JLabel filterDetail) {
		this.filterDetail = filterDetail;
	}

	@Override
	protected void initView() {
		this.mainView = new JScrollPane();
		this.table = new MainTableView();
	}

	@Override
	protected void initOtherViewControllers() {
		// 本视图控制器不依赖其他视图控制器
	}

	/**
	 * 初始化表格视图的数据模型
	 */
	protected void initUI() {
		mainView.setViewportView(table);
	}

	/**
	 * 初始化表格视图的样式与风格
	 */
	@Override
	protected void initLookAndFeel() {
		GuiUtil.initLookAndFeel();
	}

	@Override
	protected void initEventListener() {
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {// 点击几次，这里是双击事件
					tableDbClick();
				}
			}
		});
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(final MouseEvent e) {
//                if (!e.isPopupTrigger())
//                    return;
				if (e.getButton() == MouseEvent.BUTTON1 || e.getButton() == MouseEvent.BUTTON2)
					return;
				FileStatus[] fileStatuses;
				if (table.getSelectedRows().length <= 1) {
					int rowAtPoint = table.rowAtPoint(table.getMousePosition());
					table.setRowSelectionInterval(rowAtPoint, rowAtPoint);
					fileStatuses = new FileStatus[] { transformTableInfoToFileStatus(rowAtPoint) };
				} else
					fileStatuses = getSelectedRowsData();
				Tuple2<Boolean, Boolean> haveDirOrFile = isIncludeDirOrFile(fileStatuses);
				if (haveDirOrFile._1 && haveDirOrFile._2) {
					PopupMenuUtil.show(mainView, null, TABLE_FILE_AND_DIR_MENU_ITEMS, e, fileStatuses);
				} else if (haveDirOrFile._1) {
					PopupMenuUtil.show(mainView, null, TABLE_DIR_MENU_ITEMS, e, fileStatuses);
				} else {
					PopupMenuUtil.show(mainView, null, TABLE_FILE_MENU_ITEMS, e, fileStatuses);
				}
			}
		});
	}

	@Override
	protected void initActionListener() {
		// 没有动作需要监听
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

	/**
	 * tablePanel的双击事件：打开文件夹
	 */
	private void tableDbClick() {
		int row = table.getSelectedRow();
		FileStatus fileStatus = transformTableInfoToFileStatus(row);

		if (fileStatus.isFile()) {
			ActionEnum.PREVIEW_FILE.getAction().doAction(mainView, null, new FileStatus[] { fileStatus });
			return;
		}
		ctx.getMainModel().executeCmd(new GotoPathCmd(
				fileStatus.getPath(),
				new Path(ctx.getFileStatusModel().getNavigationPath()),
				path -> ctx.getMainModel().notification(path.toString())));
	}

	/**
	 * 表头为： icon , "文件", "大小", "用户与权限", "路径", "父路径", "最后修改日期", "文件大小排序(KB)"
	 */
	private FileStatus transformTableInfoToFileStatus(int row) {
		long len = Long.parseLong(getValue(row, 2));
		boolean isdir = getValue(row, 3).contains("目录");
		Path path = new Path(getValue(row, 4));
		long modificationTime = 0;
		try {
			modificationTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(getValue(row, 6)).getTime();
		} catch (ParseException e) {
			log.error(e.getMessage(), e);
		}
		return new FileStatus(len, isdir, 0, 0, modificationTime, path);
	}

	private FileStatus[] getSelectedRowsData() {
		final int[] rows = table.getSelectedRows();
		FileStatus[] ret = new FileStatus[rows.length];
		for (int i = 0; i < rows.length; i++) {
			ret[i] = transformTableInfoToFileStatus(rows[i]);
		}
		return ret;
	}

	/**
	 * 从tableModel的单元格中信息
	 */
	private String getValue(int row, int column) {
		return table.getModel().getValueAt(table.convertRowIndexToModel(row), column).toString();// 方法1：获取排序后实际行号值
	}

	/**
	 * 添加数据到表格里
	 */
	private void addRowForTable(Object[][] fileList) {
		try {
			// 去掉行号
			mainView.setRowHeaderView(null);
			DefaultTableModel model = ((DefaultTableModel) table.getModel());
			model.setRowCount(0);
			for (Object[] aFileList : fileList) {
				model.addRow(aFileList);
			}
			// 添加行号
			if (table.getRowCount() != 0)
				mainView.setRowHeaderView(new RowHeaderTable(table));
		} catch (Exception e) {
			log.error(e);
		}
	}

	private void refreshTable(List<FileStatus> fileList) {
		Object[][] list = fu(fileList);
		addRowForTable(list);
	}

	/**
	 * 根据文件状态信息转换JTableModel的信息
	 */
	private Object[][] fu(List<FileStatus> files) {

		Object[][] m = new Object[files.size()][MainTableView.TABLE_HEADERS.length];// 增加文件路径，父路径

		for (int i = 0; i < files.size(); i++) {
			setTableCellContentAt(m, files.get(i), i);
		}
		return m;
	}

	/**
	 * 更改表格单元内容
	 */
	private void setTableCellContentAt(Object[][] m, FileStatus file, int row) {
		m[row][1] = file.getPath().getName();
		if (file.isDirectory()) {
			long fileLen = 0;// ctx.getHdfsDao().getFileOrDirSize(file.getPath());
			m[row][0] = GuiUtil.getSystemIcon(file.getPath().getName(), file.isDirectory());
			m[row][2] = fileLen;
			m[row][3] = "目录[" + file.getOwner() + " " + file.getPermission().toString() + "]";
		} else {
			m[row][0] = GuiUtil.getSystemIcon(file.getPath().getName(), file.isDirectory());
			m[row][2] = file.getLen();
			m[row][3] = "文件[" + file.getOwner() + " " + file.getPermission().toString() + "]";
		}
		m[row][4] = file.getPath().toString();
		m[row][5] = file.getPath().getParent().toString();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		m[row][6] = sdf.format(new Date(file.getModificationTime()));
	}

	/**
	 * 表格过滤（支持正则表达式）
	 */
	private void filterRow(String filter) {

		if (filter == null)
			return;

		@SuppressWarnings("unchecked")
		TableRowSorter<TableModel> sorter = (TableRowSorter<TableModel>) table.getRowSorter();

		sorter.setRowFilter(RowFilter.regexFilter(filter));

		// 统计过滤后的表格信息
		long totalSize = 0;
		for (int i = 0; i < table.getRowCount(); i++) {
			totalSize += Double.parseDouble(getValue(i, 2));
		}

		filterDetail
				.setText("共" + table.getRowCount() + "个文件，总大小为" + FileUtil.transformFileLengthToHumanString(totalSize));
	}

	@Override
	public void update(Observable o, Object arg) {
		if ((o instanceof MainModel) && (arg instanceof String)) {
			List<FileStatus> fileStatus = ctx.getFileStatusModel().getUnmodifableFiles();
			refreshTable(fileStatus);
			if (filterField.getText() != null)
				filterRow(filterField.getText());
		}
	}

	public JScrollPane getMainView() {
		return this.mainView;
	}
}