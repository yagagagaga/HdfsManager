package hdfsmanager.frame.main_frame.bottom_view.bottom_view.tab_view2.bottom_table_view;

import java.awt.*;
import java.util.Date;

import javax.swing.*;
import javax.swing.table.*;

import hdfsmanager.support.io.base.TaskType;
import hdfsmanager.util.DateUtil;
import hdfsmanager.util.GuiUtil;

 class TaskInfoDetailTableView extends JTable {

    private static final String[] TABLE_HEADER = {"id", "任务类型", "文件名", "下载进度", "开始时间", "结束时间", "任务状态"};
    private static final int SECOND = 1;
    private static final int THIRD = 2;
    private static final int FOURTH = 3;

     TaskInfoDetailTableView() {
        initTableModel();
        initTableSorter();
        initTableRender();
        initLookAndFeel();
    }

    private void initTableModel() {
        TableModel model = createTableModel();
        setModel(model);
    }

    private TableModel createTableModel() {
        return new DefaultTableModel(new Object[0][0], TABLE_HEADER) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void initTableSorter() {
        TableRowSorter<TableModel> sorter = new TableRowSorter<>(getModel());
        setRowSorter(sorter);
    }

    private void initTableRender() {
        TableCellRenderer renderer = createTableCellRenderer();
        setDefaultRenderer(Object.class, renderer);
    }

    private TableCellRenderer createTableCellRenderer() {
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
                    String content = DateUtil.format((Date)value, "yyyy-MM-dd HH:mm:ss");
                    this.setText(content);
                    return this;
                }
                return ret;
            }
        };
    }

    private boolean isSecondColumnAndUnknownTask(int column, Object value) {
        return column == SECOND &&
                value instanceof TaskType &&
                value == TaskType.UNKNOWN_TYPE;
    }

    private boolean isThirdColumnAndBlankFileName(int column, Object value) {
        return column == THIRD &&
                "".equals(value);
    }

    private boolean isFourthColumnAndProgressDetail(int column, Object value) {
        return column == FOURTH &&
                value instanceof Integer;
    }

    private Component transformProgressToProgressBar(Integer progress) {
        JProgressBar pBar = new JProgressBar();
        pBar.setValue(progress);
        pBar.setStringPainted(true);
        pBar.setForeground(progress == 100 ? Color.GREEN : Color.BLUE);
        return pBar;
    }

    private void initLookAndFeel(){
        GuiUtil.initLookAndFeel();
        GuiUtil.hideTableColumn(this, 0);
    }
}
