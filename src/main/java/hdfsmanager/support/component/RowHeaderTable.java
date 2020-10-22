package hdfsmanager.support.component;

import java.awt.*;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;

/**
 * 用于显示RowHeader的JTable，只需要将其加入JScrollPane的RowHeaderView即可为JTable生成行标题
 */
public class RowHeaderTable extends JTable {
	private static final long serialVersionUID = 1L;

	/**
	 * 为JTable添加RowHeader，
	 * 
	 * @param refTable 需要添加rowHeader的JTable
	 */
	public RowHeaderTable(JTable refTable) {
		super();

		if (refTable == null) {
			throw new IllegalStateException("refTable 为 null");
		}

		this.setModel(new RowHeaderTableModel(refTable));

		this.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);// 不可以调整列宽
		int width = this.adaptiveWidth(refTable.getRowCount());// 自适应宽度
		this.getColumnModel().getColumn(0).setPreferredWidth(width);
		this.setPreferredScrollableViewportSize(new Dimension(width, 0));
		this.setRowHeight(refTable.getRowHeight());

		this.setDefaultRenderer(Object.class, new RowHeaderRenderer(refTable, this));// 设置渲染器
	}

	private int adaptiveWidth(int rowCount) {
		if (rowCount == 0)
			return 0;
		int width = 14;
		while ((rowCount /= 10) > 0)
			width += 7;
		return width;
	}

	/**
	 * 用于显示RowHeader的JTable的渲染器，可以实现动态增加，删除行，在Table中增加、删除行时RowHeader
	 * 一起变化。当选择某行时，该行颜色会发生变化
	 */
	static class RowHeaderRenderer extends JLabel implements TableCellRenderer, ListSelectionListener {
		private static final long serialVersionUID = 1L;
		JTable reftable;// 需要添加rowHeader的JTable
		JTable tableShow;// 用于显示rowHeader的JTable

		public RowHeaderRenderer(JTable reftable, JTable tableShow) {
			this.reftable = reftable;
			this.tableShow = tableShow;
			// 增加监听器，实现当在reftable中选择行时，RowHeader会发生颜色变化
			ListSelectionModel listModel = reftable.getSelectionModel();
			listModel.addListSelectionListener(this);
		}

		@Override
		public Component getTableCellRendererComponent(JTable table, Object obj, boolean isSelected, boolean hasFocus,
				int row, int col) {
			JTableHeader header = reftable.getTableHeader();
			this.setOpaque(true);
			setBorder(UIManager.getBorder("TableHeader.cellBorder"));// 设置为TableHeader的边框类型
			setHorizontalAlignment(RIGHT);// 让text居中显示
			setBackground(header.getBackground());// 设置背景色为TableHeader的背景色
			if (isSelect(row)) // 当选取单元格时,在row header上设置成选取颜色
			{
				setForeground(Color.white);
				setBackground(Color.lightGray);
			} else {
				setForeground(header.getForeground());
			}
			setFont(header.getFont());
			setText(String.valueOf(row + 1));
			return this;
		}

		@Override
		public void valueChanged(ListSelectionEvent e) {
			this.tableShow.repaint();
			this.repaint();
		}

		private boolean isSelect(int row) {
			int[] sel = reftable.getSelectedRows();
			for (int value : sel)
				if (value == row)
					return true;
			return false;
		}
	}

	/**
	 * 用于显示表头RowHeader的JTable的TableModel，不实际存储数据
	 */
	static class RowHeaderTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		private final JTable refTable;// 当前JTable的行数，与需要加RowHeader的TableModel同步

		public RowHeaderTableModel(JTable refTable) {
			this.refTable = refTable;
		}

		public int getRowCount() {
			return refTable.getRowCount();
		}

		public int getColumnCount() {
			return 1;
		}

		public Object getValueAt(int row, int column) {
			return row;
		}
	}
}