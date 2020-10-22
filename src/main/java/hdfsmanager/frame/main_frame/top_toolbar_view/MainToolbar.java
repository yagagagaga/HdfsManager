package hdfsmanager.frame.main_frame.top_toolbar_view;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

import javax.swing.*;

import hdfsmanager.frame.main_frame.bottom_view.right_view.MainTableViewController;
import hdfsmanager.util.ResourcesDepository;

class MainToolbar extends JToolBar {

	private JButton undo;
	private JButton redo;
	private JComboBox<String> searchBar;
	private JButton enter;
	private JButton refresh;
	private JLabel tip;

	/**
	 * 这个对象交给{@link MainTableViewController}处理，该类只是负责该对象的布局和样式，不更改该对象的值、状态或事件
	 */
	private JTextField filterField;

	/**
	 * 这个对象交给{@link MainTableViewController}处理，该类只是负责该对象的布局和样式，不更改该对象的值、状态或事件
	 */
	private JLabel filterDetail;

	MainToolbar() {
		initView();
		initUI();
	}

	private void initView() {
		undo = new JButton(ResourcesDepository.getIcon("images/back.png"));
		redo = new JButton(ResourcesDepository.getIcon("images/forward.png"));
		searchBar = new JComboBox<>();
		enter = new JButton(ResourcesDepository.getIcon("images/jump_to.png"));
		refresh = new JButton(ResourcesDepository.getIcon("images/refresh.png"));
		tip = new JLabel("过滤");
		filterField = new JTextField();
		filterDetail = new JLabel();
	}

	private void initUI() {
		searchBar.setEditable(true);
		searchBar.setPreferredSize(new Dimension(600, searchBar.getHeight()));

		add(undo);
		add(redo);
		add(searchBar);
		add(enter);
		add(refresh);
		addSeparator();
		add(tip);
		add(filterField);
		add(filterDetail);

		setFloatable(false);
	}

	void addUndoAction(ActionListener l) {
		undo.addActionListener(l);
	}

	void addRedoAction(ActionListener l) {
		redo.addActionListener(l);
	}

	void addSearchbarItemListener(ItemListener l) {
		searchBar.addItemListener(l);
	}

	void addEnterAction(ActionListener l) {
		enter.addActionListener(l);
	}

	void addRefreshAction(ActionListener l) {
		refresh.addActionListener(l);
	}

	String getSearchbarText() {
		return searchBar.getEditor().getItem().toString();
	}

	void removeSearchbarRemoveItemListener(ItemListener l) {
		searchBar.removeItemListener(l);
	}

	DefaultComboBoxModel<String> getSearchbarComboBoxModel() {
		return (DefaultComboBoxModel<String>) searchBar.getModel();
	}

	int getSearchbarItemCount() {
		return searchBar.getItemCount();
	}

	void removeSearchbarItemAt(int index) {
		searchBar.removeItemAt(index);
	}

	void setSearchbarSelectedItem(String value) {
		searchBar.setSelectedItem(value);
	}

	public JTextField getFilterField() {
		return this.filterField;
	}

	public JLabel getFilterDetail() {
		return this.filterDetail;
	}
}
