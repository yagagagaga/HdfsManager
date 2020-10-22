package hdfsmanager.frame.main_frame.bottom_view.bottom_view.tab_view2.bottom_table_view;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import hdfsmanager.HdfsContext;
import hdfsmanager.TasksModel;
import hdfsmanager.api.controller.ViewController;
import hdfsmanager.support.component.RowHeaderTable;
import hdfsmanager.support.io.base.BaseTask;

public class TaskInfoDetailTableViewController extends ViewController {

	private static final ScheduledExecutorService POOL = Executors.newScheduledThreadPool(1);

	private JScrollPane mainView;
	private final TasksModel tasksModel;
	private JTable taskInfoDetailTableView;

	public TaskInfoDetailTableViewController(HdfsContext ctx) {
		this.tasksModel = ctx.getTasksModel();
		this.tasksModel.addObserver(this);

		POOL.scheduleWithFixedDelay(new ScheduledTask(), 0, 200, TimeUnit.MILLISECONDS);
		init();
	}

	@Override
	protected void initView() {
		mainView = new JScrollPane();
		taskInfoDetailTableView = new TaskInfoDetailTableView();
	}

	@Override
	protected void initUI() {
		mainView.setViewportView(taskInfoDetailTableView);
	}

	@Override
	protected void initEventListener() {
		ListSelectionModel selectionModel = taskInfoDetailTableView.getSelectionModel();
		selectionModel.addListSelectionListener(x -> {
			int[] selectedRows = taskInfoDetailTableView.getSelectedRows();
			for (int rowIndex = 0; rowIndex < selectedRows.length; rowIndex++) {
				selectedRows[rowIndex] = getTaskKey(selectedRows[rowIndex]);
			}
			tasksModel.setSelectedTaskIds(selectedRows);
		});
	}

	private int getTaskKey(int row) {
		if (row < 0 || row > taskInfoDetailTableView.getRowCount()) {
			throw new IllegalArgumentException(this.getClass().getName() + ":无法找到第" + row + "行（从0开始）");
		}
		return Integer.parseInt(taskInfoDetailTableView.getValueAt(row, 0).toString());
	}

	@Override
	public void update(Observable o, Object arg) {
		if (tasksModel.isTriggerSelectAll()) {
			taskInfoDetailTableView.selectAll();
			tasksModel.setTriggerSelectAll(false);
		} else if (tasksModel.isTriggerInverseSel()) {
			List<Integer> tmp = new ArrayList<>();
			for (int i = 0; i < taskInfoDetailTableView.getRowCount(); i++) {
				if (!taskInfoDetailTableView.isRowSelected(i)) {
					tmp.add(i);
				}
			}
			taskInfoDetailTableView.clearSelection();
			for (Integer i : tmp) {
				taskInfoDetailTableView.addRowSelectionInterval(i, i);
			}
			tasksModel.setTriggerInverseSel(false);
		}
	}

	public JScrollPane getMainView() {
		return this.mainView;
	}

	private class ScheduledTask implements Runnable {

		@Override
		public void run() {
			try {
				DefaultTableModel tableModel = (DefaultTableModel) taskInfoDetailTableView.getModel();
				// 当dataModel数据量有变化时，重绘table
				if (tableModel.getRowCount() != tasksModel.size()) {
					mainView.setRowHeaderView(null);
					tableModel.setRowCount(0);
					Object[][] objects = fu();
					for (Object[] rowData : objects) {
						tableModel.addRow(rowData);
					}

					if (objects.length != 0)
						mainView.setRowHeaderView(new RowHeaderTable(taskInfoDetailTableView));

					tasksModel.setMinimum(0);
					tasksModel.setMaximum(objects.length);

					// 还有未完成的任务时才需要设置刷新
				}
				// 否则，更新table单元格里的数据即可
				else
					updateTableCell();
			} catch (Exception ignored) {
				// 确保该方法不会被异常打断
			} finally {
				tasksModel.notifyObservers();
			}
		}

		private Object[][] fu() {
			Object[][] ret = new Object[tasksModel.size()][taskInfoDetailTableView.getColumnCount()];
			Set<Integer> keys = tasksModel.keySet();
			int index = 0;
			for (Integer key : keys) {
				BaseTask task = tasksModel.get(key);
				ret[index][0] = task.hashCode(); // key
				ret[index][1] = task.getTaskType(); // 任务类型
				ret[index][2] = task.getTaskName(); // 文件名
				ret[index][3] = task.getProgress(); // 下载进度
				ret[index][4] = task.getStartTime(); // 开始时间
				ret[index][5] = task.getFinishTime(); // 结束时间
				ret[index][6] = task.getStatus(); // 任务状态
				index++;
			}
			return ret;
		}

		private void updateTableCell() {
			int count = 0; // 统计完成的任务数量
			DefaultTableModel tableModel = (DefaultTableModel) taskInfoDetailTableView.getModel();
			for (int i = 0; i < tableModel.getRowCount(); i++) {
				Integer id = null;
				if (tableModel.getValueAt(i, 0) instanceof Integer) {
					id = (Integer) tableModel.getValueAt(i, 0);
				}
				BaseTask task = tasksModel.get(id);
				// 更新任务类型
				tableModel.setValueAt(task.getTaskType().getDescription(), i, 1);
				// 更新文件名
				tableModel.setValueAt(task.getTaskName(), i, 2);
				// 更新进度条
				int progress = (int) (task.getProgress() * 100);
				tableModel.setValueAt(progress, i, 3);
				if (progress == 100)
					count++;
				// 更新任务结束时间
				tableModel.setValueAt(task.getFinishTime(), i, 5);
				// 更新状态信息
				tableModel.setValueAt(task.getStatus(), i, 6);
			}
			tasksModel.setValue(count);

			// todo 只有需要刷新 且 进度条达到100% 且 进度条最大进度不为零才能刷新界面
		}
	}
}
