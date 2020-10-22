package hdfsmanager.view;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.*;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.log4j.Logger;

import hdfsmanager.api.View;
import hdfsmanager.exception.UnexpectedException;
import hdfsmanager.controller.MainController;
import hdfsmanager.model.HdfsModel;
import hdfsmanager.support.component.LoadingPanel;
import hdfsmanager.util.GuiUtil;
import hdfsmanager.util.ResourcesDepository;

public class MainView extends View<MainController<MainView>, HdfsModel> {

	private static final Logger log = Logger.getLogger(MainView.class);

	private JFrame viewFrame;
	private LoadingPanel loadingPanel;

	private JPanel viewPanel;
	private JSplitPane leftRightPanel;
	private JButton previousBtn;
	private JButton nextBtn;
	private JComboBox<String> searchField;
	private JButton jumpToPathBtn;
	private JButton refreshBtn;
	private JSplitPane topBottomPanel;
	private JButton upperBtn;
	private JTextField filterField;
	private JButton renameBtn;
	private JButton copyBtn;
	private JButton pasteBtn;
	private JButton cutBtn;
	private JButton uploadBtn;
	private JButton downloadBtn;
	private JButton createFolderBtn;
	private JButton addSuccessFlagBtn;
	private JButton concatBtn;
	private JButton batchRenameBtn;
	private JButton delFileBtn;
	private JButton chOwnAndModBtn;
	private JButton diskUsageBtn;

	public MainView(MainController<MainView> controller, HdfsModel model) {
		super(controller, model);
	}

	@Override
	protected void createView(JComponent... externalView) {
		viewFrame = new JFrame("My Manager");
		viewFrame.setContentPane(viewPanel);
		viewFrame.setIconImage(ResourcesDepository.getImage("images/logo2.gif"));
		viewFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		loadingPanel = new LoadingPanel();
		viewFrame.setGlassPane(loadingPanel);

		if (externalView.length != 3)
			throw new UnexpectedException("请传入三个视图");
		topBottomPanel.setBottomComponent(externalView[0]);
		leftRightPanel.setLeftComponent(externalView[1]);
		leftRightPanel.setRightComponent(externalView[2]);
		topBottomPanel.setDividerLocation(500);
		leftRightPanel.setDividerLocation(200);
	}

	@Override
	protected void createControls() {
		// 第一行
		jumpToPathBtn.addActionListener(e -> {
			String p = (String) searchField.getSelectedItem();
			if (p == null) {
				log.error("你输入的路径为 NULL");
			} else {
				controller.gotoPath(new Path(p));
			}
		});
		searchField.addItemListener(e -> {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				jumpToPathBtn.doClick();
			}
		});
		previousBtn.addActionListener(e -> controller.gotoPreviousPath());
		nextBtn.addActionListener(e -> controller.gotoNextPath());
		upperBtn.addActionListener(e -> {
			String p = String.valueOf(searchField.getSelectedItem());
			Path path = new Path(p).getParent();
			if (path == null) {
				upperBtn.setEnabled(false);
			} else {
				controller.gotoPath(path);
			}
		});
		refreshBtn.addActionListener(e -> {
			String p = String.valueOf(searchField.getSelectedItem());
			controller.refreshPath(new Path(p));
		});
		// 第二行
		renameBtn.addActionListener(e -> controller.rename());
		uploadBtn.addActionListener(e -> controller.upload());
		downloadBtn.addActionListener(e -> controller.download());
		createFolderBtn.addActionListener(e -> controller.newFolder());
		addSuccessFlagBtn.addActionListener(e -> controller.addSuccessFlag());
		concatBtn.addActionListener(e -> controller.concat());
		batchRenameBtn.addActionListener(e -> controller.batchRename());
		delFileBtn.addActionListener(e -> controller.del());
		diskUsageBtn.addActionListener(e -> controller.diskUsage());
		chOwnAndModBtn.addActionListener(e -> controller.chPermissionAndOwner());
		copyBtn.addActionListener(e -> controller.copyPath());
		cutBtn.addActionListener(e -> controller.cutFiles());
		pasteBtn.addActionListener(e -> controller.pasteFiles());
	}

	@Override
	protected void setVisible(boolean isVisible) {
		GuiUtil.setFullSize(viewFrame);
		GuiUtil.setToCenterLocation(viewFrame);
		viewFrame.setExtendedState(viewFrame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		viewFrame.setVisible(isVisible);
	}

	@Override
	protected void update(HdfsModel model) {
		// 更新搜索框
		updateSearchField(model);
		// 更新按钮
		updateBtn(model);
	}

	@Override
	protected void update(HdfsModel model, Object arg) {
		if (!(arg instanceof HdfsModel.UpdateLevel))
			return;
		HdfsModel.UpdateLevel updateLevel = (HdfsModel.UpdateLevel) arg;
		switch (updateLevel) {
		case LOADING:
			loadingPanel.loading();
			break;
		case LOADED:
			loadingPanel.loaded();
			break;
		default:
			update(model);
			break;
		}
	}

	private void updateSearchField(HdfsModel model) {
		// 除选中事件，因为每次移除（插入）item时都有可能触发选中事件
		ItemListener[] listeners = searchField.getItemListeners();
		for (ItemListener l : listeners) {
			searchField.removeItemListener(l);
		}

		String item = model.getCurrentPath().toString();
		DefaultComboBoxModel<String> comboBoxModel = (DefaultComboBoxModel<String>) searchField.getModel();
		// 去掉重复的记录
		for (int i = comboBoxModel.getSize() - 1; i >= 0; i--) {
			if (comboBoxModel.getElementAt(i).equals(item)) {
				comboBoxModel.removeElementAt(i);
			}
		}

		// 只保留最近15条记录
		while (searchField.getItemCount() > 15) {
			searchField.removeItemAt(searchField.getItemCount() - 1);
		}
		searchField.insertItemAt(item, 0);
		searchField.setSelectedIndex(0);

		// 重新添加事件
		for (ItemListener l : listeners) {
			searchField.addItemListener(l);
		}
	}

	private void updateBtn(HdfsModel model) {
		upperBtn.setEnabled(true);
		FileStatus[] selectedFileStatuses = model.getSelectedFileStatuses();
		boolean enabled = selectedFileStatuses.length != 0;
		renameBtn.setEnabled(enabled);
		downloadBtn.setEnabled(enabled);
		concatBtn.setEnabled(enabled);
		batchRenameBtn.setEnabled(enabled);
		delFileBtn.setEnabled(enabled);
		chOwnAndModBtn.setEnabled(enabled);
		copyBtn.setEnabled(enabled);
		cutBtn.setEnabled(enabled);
		pasteBtn.setEnabled(enabled);
	}

	@Override
	protected JComponent getMainView() {
		return viewPanel;
	}
}
