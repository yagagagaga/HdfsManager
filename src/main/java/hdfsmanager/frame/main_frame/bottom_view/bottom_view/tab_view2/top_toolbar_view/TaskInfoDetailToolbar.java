package hdfsmanager.frame.main_frame.bottom_view.bottom_view.tab_view2.top_toolbar_view;

import java.awt.event.ActionListener;

import javax.swing.*;

import hdfsmanager.util.ResourcesDepository;

class TaskInfoDetailToolbar extends JToolBar {

	private final JButton start = new JButton("继续");
	private final JButton pause = new JButton("暂停");
	private final JButton suspend = new JButton("停止");
	private final JButton selectAll = new JButton("全选");
	private final JButton inverseSel = new JButton("反选");
	private final JButton remove = new JButton("移除任务", ResourcesDepository.getIcon("images/delete.gif"));
	private final JProgressBar totalProgressBar = new JProgressBar();

	TaskInfoDetailToolbar() {
		setFloatable(false);
		add(start);
		add(pause);
		add(suspend);
		add(selectAll);
		add(inverseSel);
		add(remove);
		totalProgressBar.setStringPainted(true);
		add(totalProgressBar);
	}

	void addStartButtonAction(ActionListener l) {
		start.addActionListener(l);
	}

	void addPauseButtonAction(ActionListener l) {
		pause.addActionListener(l);
	}

	void addSuspendButtonAction(ActionListener l) {
		suspend.addActionListener(l);
	}

	void addSelectAllButtonAction(ActionListener l) {
		selectAll.addActionListener(l);
	}

	void addInverseSelButtonAction(ActionListener l) {
		inverseSel.addActionListener(l);
	}

	void addRemoveButtonAction(ActionListener l) {
		remove.addActionListener(l);
	}

	public JProgressBar getTotalProgressBar() {
		return this.totalProgressBar;
	}
}
