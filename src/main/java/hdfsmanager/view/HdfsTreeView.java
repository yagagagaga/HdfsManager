package hdfsmanager.view;

import static hdfsmanager.support.command.ActionEnum.*;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;

import hdfsmanager.api.View;
import hdfsmanager.controller.HdfsTreeController;
import hdfsmanager.model.HdfsModel;
import hdfsmanager.support.command.ActionEnum;
import hdfsmanager.util.PathUtil;
import hdfsmanager.util.PopupMenuUtil;

public class HdfsTreeView extends View<HdfsTreeController, HdfsModel> {

	private JPanel viewPanel;
	private JTree tree;

	private DefaultMutableTreeNode currentNode;

	private static final FileStatus LOADING_FILE_STATUS = new FileStatus(
			0,
			false,
			0,
			0,
			0,
			new Path("loading..."));

	private static final FileStatus VOID_FILE_STATUS = new FileStatus(
			0,
			false,
			0,
			0,
			0,
			new Path("无"));

	/**
	 * 树形视图文件夹的右键菜单项目
	 */
	private static final ActionEnum[] TREE_MENU_ITEMS = {
			DOWN_FILE_AND_DIR, UP_FILE_AND_DIR,
			SEPARATOR,
			NEW_FOLDER, ADD_SUCCESS_FLAG, MERGE_FILES,
			SEPARATOR,
			RENAME_FILE, CUT_FILE, COPY_FILE, PASTE_FILE, DEL_FILE,
			SEPARATOR,
			COPY_PATH, EMPOWER_777
	};

	public HdfsTreeView(HdfsTreeController controller, HdfsModel model) {
		super(controller, model);
	}

	@Override
	protected void createView(JComponent... externalView) {
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(new FileStatus(
				0,
				true,
				0,
				0,
				0,
				new Path("/")));
		DefaultTreeModel tm = new DefaultTreeModel(rootNode);
		rootNode.add(placeHolder());
		currentNode = rootNode;
		tree.setModel(tm);
		tree.setCellRenderer(new MyTreeCellRenderer());
	}

	@Override
	protected void createControls() {
		tree.addTreeExpansionListener(this);
		tree.addMouseListener(this);
	}

	@Override
	protected JComponent getMainView() {
		return viewPanel;
	}

	@Override
	public void treeExpanded(TreeExpansionEvent e) {
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
		currentNode = node;
		controller.treeExpanded(node);
	}

	@Override
	protected void update(HdfsModel model, Object arg) {
		if (arg != HdfsModel.UpdateLevel.UI_AND_DATA)
			return;
		Path updatePath = model.getCurrentPath();
		Path currentPath = ((FileStatus) currentNode.getUserObject()).getPath();
		List<FileStatus> fileStatuses = model.getFileStatuses()
				.stream()
				.filter(FileStatus::isDirectory)
				.collect(Collectors.toList());
		if (PathUtil.isEquals(currentPath, updatePath)) {
			addChildrenToNode(fileStatuses, currentNode);
		}
	}

	private DefaultMutableTreeNode placeHolder() {
		return new DefaultMutableTreeNode(LOADING_FILE_STATUS);
	}

	private DefaultMutableTreeNode placeHolder2() {
		return new DefaultMutableTreeNode(VOID_FILE_STATUS);
	}

	private synchronized void addChildrenToNode(List<FileStatus> files, DefaultMutableTreeNode node) {
		DefaultTreeModel tm = (DefaultTreeModel) tree.getModel();
		int size = tm.getChildCount(node);
		// 先把新节点添加进来
		if (files.isEmpty()) {
			tm.insertNodeInto(placeHolder2(), node, size);
		} else {
			for (int i = files.size() - 1; i >= 0; i--) {
				FileStatus f = files.get(i);
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(f);
				tm.insertNodeInto(child, node, size);
				tm.insertNodeInto(placeHolder(), child, 0);
			}
		}
		// 再把旧节点删除
		for (int i = size; i > 0; i--) {
			tm.removeNodeFromParent((DefaultMutableTreeNode) node.getChildAt(0));
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		TreePath path = tree.getPathForLocation(e.getX(), e.getY());
		tree.setSelectionPath(path);
	}

	@Override
	public void mouseReleased(final MouseEvent e) {
		// 打开右键菜单
		Optional<FileStatus> fileStatusOpt = getSelectedFileStatus();
		if (!fileStatusOpt.isPresent()) {
			return;
		}
		if (e.getButton() == MouseEvent.BUTTON3) {
			PopupMenuUtil.show(getMainView(), controller, TREE_MENU_ITEMS, e, new FileStatus[] { fileStatusOpt.get() });
		}
	}

	/**
	 * 从JTree上获取文件状态信息
	 */
	private Optional<FileStatus> getSelectedFileStatus() {
		TreePath path = tree.getSelectionPath();
		if (path == null)
			return Optional.empty();

		DefaultMutableTreeNode selectnode = (DefaultMutableTreeNode) path.getLastPathComponent();

		// 这里加上类型判断
		if (!(selectnode.getUserObject() instanceof FileStatus)) {
			return Optional.empty();
		}
		return Optional.of((FileStatus) selectnode.getUserObject());
	}

	static class MyTreeCellRenderer extends DefaultTreeCellRenderer {
		@Override
		public Component getTreeCellRendererComponent(
				JTree tree,
				Object value,
				boolean sel,
				boolean expanded,
				boolean leaf,
				int row,
				boolean hasFocus) {
			Component defaultRet = super.getTreeCellRendererComponent(
					tree,
					value,
					sel,
					expanded,
					leaf,
					row,
					hasFocus);
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
			FileStatus f = (FileStatus) node.getUserObject();
			Path p = f.getPath();
			String name = "/".equals(p.toString()) ? "/" : p.getName();
			setText(name);
			return defaultRet;
		}
	}
}
