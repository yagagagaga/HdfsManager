package hdfsmanager.mvc.view;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Writer;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

import hdfsmanager.support.component.RowHeaderTable;
import hdfsmanager.support.io.base.TaskType;
import hdfsmanager.util.DateUtil;
import hdfsmanager.util.GuiUtil;
import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;

import hdfsmanager.api.View;
import hdfsmanager.exception.UnexpectedException;
import hdfsmanager.mvc.controller.MainBottomController;
import hdfsmanager.mvc.model.DownUploadTaskModel;
import hdfsmanager.support.io.base.BaseTask;

public class MainBottomView extends View<MainBottomController, DownUploadTaskModel> {

	private static final Logger log = Logger.getLogger(MainBottomView.class);
	private static final String[] TABLE_HEADER = { "id", "任务类型", "文件名", "下载进度", "开始时间", "结束时间", "任务状态" };

	private JTabbedPane viewPanel;
	private JTable table;
	private JButton continuesBtn;
	private JButton pauseBtn;
	private JButton stopBtn;
	private JButton allSelect;
	private JButton inverseSelect;
	private JButton removeBtn;
	private JProgressBar progressBar;
	private JTextArea logArea;
	private JScrollPane tableScrollPane;

	private final JPopupMenu popupText = new JPopupMenu() {
		{
			JMenuItem menuItem = new JMenuItem("清空");
			menuItem.addActionListener(ee -> logArea.setText(""));
			add(menuItem);
		}
	};

	public MainBottomView(MainBottomController controller, DownUploadTaskModel model) {
		super(controller, model);
	}

	@Override
	protected void createView(JComponent... externalView) {
		table.setModel(new DefaultTableModel(new Object[0][0], TABLE_HEADER) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		});
		table.setRowSorter(new TableRowSorter<>(table.getModel()));
		table.setDefaultRenderer(Object.class, tableRenderer());
		GuiUtil.hideTableColumn(table, 0);
	}

	private DefaultTableCellRenderer tableRenderer() {
		return new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
				Component ret = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				// 第二列是任务类型
				if (isSecondColumnAndUnknownTask(column, value)) {
					this.setText("正在获取任务类型...");
					return this;
				}
				// 第三列文件名
				if (isThirdColumnAndBlankFileName(column, value)) {
					this.setText("正在获取文件名...");
					return this;
				}
				// 第四列是进度信息
				if (isFourthColumnAndProgressDetail(column, value)) {
					return transformProgressToProgressBar((Integer) value);
				}

				if (value instanceof Date) {
					String content = DateUtil.format((Date) value, "yyyy-MM-dd HH:mm:ss");
					this.setText(content);
					return this;
				}
				return ret;
			}

			private boolean isSecondColumnAndUnknownTask(int column, Object value) {
				return column == 1 &&
						value == TaskType.UNKNOWN_TYPE;
			}

			private boolean isThirdColumnAndBlankFileName(int column, Object value) {
				return column == 2 &&
						"".equals(value);
			}

			private boolean isFourthColumnAndProgressDetail(int column, Object value) {
				return column == 3 &&
						value instanceof Integer;
			}

			private Component transformProgressToProgressBar(Integer progress) {
				JProgressBar pBar = new JProgressBar();
				pBar.setValue(progress);
				pBar.setStringPainted(true);
				pBar.setForeground(progress == 100 ? Color.GREEN : Color.BLUE);
				return pBar;
			}
		};
	}

	@Override
	protected void createControls() {
		logArea.addMouseListener(this);
		continuesBtn.addActionListener(e -> controller.startTasks(fetchSelectionTasks()));
		pauseBtn.addActionListener(e -> controller.pauseTasks(fetchSelectionTasks()));
		stopBtn.addActionListener(e -> controller.stopTasks(fetchSelectionTasks()));
		removeBtn.addActionListener(e -> tableRemoveRows());
		allSelect.addActionListener(e -> table.selectAll());
		inverseSelect.addActionListener(e -> tableInverseSelectRows());
		redirectLog();
	}

	private void tableRemoveRows() {
		controller.removeTasks(fetchSelectionTasks());

		int[] selectedRows = table.getSelectedRows();
		Arrays.sort(selectedRows);
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		for (int i = selectedRows.length - 1; i >= 0; i--) {
			model.removeRow(selectedRows[i]);
		}
		progressBar.setMaximum(Math.max(progressBar.getMaximum() - selectedRows.length, 0));
		tableScrollPane.setRowHeaderView(new RowHeaderTable(table));
	}

	private void tableInverseSelectRows() {
		List<Integer> tmp = new ArrayList<>();
		for (int i = 0; i < table.getRowCount(); i++) {
			if (!table.isRowSelected(i)) {
				tmp.add(i);
			}
		}
		table.clearSelection();
		for (Integer i : tmp) {
			table.addRowSelectionInterval(i, i);
		}
	}

	private void redirectLog() {
		Logger root = Logger.getRootLogger(); // 获取子记录器的输出源
		Appender appender = root.getAppender("console"); // 定义一个未连接的输入流管道
		PipedReader reader = new PipedReader(); // 定义一个已连接的输出流管理，并连接到reader

		Writer writer;
		try {
			writer = new PipedWriter(reader);
			// 设置 appender 输出流
			((WriterAppender) appender).setWriter(writer);
			// 开始输出日志
			new Thread(() -> {
				// 不间断地扫描输入流
				@SuppressWarnings("resource")
				Scanner scanner = new Scanner(reader);
				// 将扫描到的字符流输出到指定的JTextArea组件
				while (scanner.hasNextLine()) {
					try {
						Thread.sleep(100);
						String line = scanner.nextLine();
						logArea.append(line);
						logArea.append("\n");
					} catch (Exception e) {
						log.error(e);
					}
				}
			}).start();
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
	}

	@Override
	protected JComponent getMainView() {
		return viewPanel;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			popupText.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	protected void update(DownUploadTaskModel model) {
		updateUI(model);
	}

	private void updateUI(DownUploadTaskModel model) {
		try {
			// 当有数据进来的时候
			if (model.getDataSize() > 0) {
				DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
				Object[][] datas = fu(model.getDataBuffer());
				for (Object[] rowData : datas) {
					tableModel.addRow(rowData);
				}

				progressBar.setMinimum(0);
				progressBar.setMaximum(tableModel.getRowCount());

				tableScrollPane.setRowHeaderView(new RowHeaderTable(table));

			} else {
				// 否则，更新table单元格里的数据即可
				updateTableCell();
			}
		} catch (Exception ignored) {
			// 确保该方法不会被异常打断
		}
	}

	private BaseTask[] fetchSelectionTasks() {
		int[] rows = table.getSelectedRows();
		BaseTask[] tasks = new BaseTask[rows.length];
		for (int i = 0; i < rows.length; i++) {
			BaseTask task = (BaseTask) table.getModel().getValueAt(rows[i], 0);
			tasks[i] = task;
		}
		return tasks;
	}

	private Object[][] fu(List<BaseTask> dataMap) {
		Object[][] ret = new Object[dataMap.size()][table.getColumnCount()];
		for (int i = 0; i < dataMap.size(); i++) {
			BaseTask task = dataMap.get(i);
			ret[i][0] = task; // key
			ret[i][1] = task.getTaskType(); // 任务类型
			ret[i][2] = task.getTaskName(); // 文件名
			ret[i][3] = task.getProgress(); // 下载进度
			ret[i][4] = task.getStartTime(); // 开始时间
			ret[i][5] = task.getFinishTime(); // 结束时间
			ret[i][6] = task.getStatus(); // 任务状态
		}
		return ret;
	}

	private void updateTableCell() {
		int count = 0; // 统计完成的任务数量
		DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
		for (int i = 0; i < tableModel.getRowCount(); i++) {
			if (!(tableModel.getValueAt(i, 0) instanceof BaseTask)) {
				throw new ClassCastException("表格的第0列应该存放BaseTask");
			}
			BaseTask task = (BaseTask) tableModel.getValueAt(i, 0);
			// 更新任务类型
			tableModel.setValueAt(task.getTaskType().getDescription(), i, 1);
			// 更新文件名
			tableModel.setValueAt(task.getTaskName(), i, 2);
			// 更新进度条
			int progress = (int) (task.getProgress() * 100);
			tableModel.setValueAt(progress, i, 3);
			if (progress == 100)
				count++;
			// 更新任务开始时间
			tableModel.setValueAt(task.getStartTime(), i, 4);
			// 更新任务结束时间
			tableModel.setValueAt(task.getFinishTime(), i, 5);
			// 更新状态信息
			tableModel.setValueAt(task.getStatus(), i, 6);
		}
		progressBar.setValue(count);
	}
}
