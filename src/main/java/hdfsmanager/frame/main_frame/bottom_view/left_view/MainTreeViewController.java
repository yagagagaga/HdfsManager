package hdfsmanager.frame.main_frame.bottom_view.left_view;

import static hdfsmanager.support.command.ActionEnum.*;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Observable;

import javax.swing.*;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;

import hdfsmanager.HdfsContext;
import hdfsmanager.MainModel;
import hdfsmanager.api.controller.ViewController;
import hdfsmanager.support.command.ActionEnum;
import hdfsmanager.support.command.GotoPathCmd;
import hdfsmanager.util.PopupMenuUtil;

public class MainTreeViewController extends ViewController {

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

	private final HdfsContext ctx;

	private JScrollPane mainView;

	private JTree tree;
	private DefaultMutableTreeNode treeRoot;
	private DefaultMutableTreeNode currentSelectedNode;

	public MainTreeViewController(HdfsContext ctx) {
		this.ctx = ctx;
		ctx.getMainModel().addObserver(this);
		init();
	}

	@Override
	protected void initView() {
		this.mainView = new JScrollPane();
		this.tree = new MainTreeView();
		this.treeRoot = (DefaultMutableTreeNode) tree.getModel().getRoot();
		this.currentSelectedNode = treeRoot;
	}

	@Override
	protected void initUI() {
		treeRoot.add(new DefaultMutableTreeNode("."));
		mainView.setViewportView(tree);
	}

	@Override
	protected void initEventListener() {
		tree.addTreeExpansionListener(new MyTreeExpansionListener());
		tree.addMouseListener(new MyMouseAdapter());
	}

	@Override
	public void update(Observable o, Object arg) {
		if (!((o instanceof MainModel) && (arg instanceof String))) {
			return;
		}
		// 当前选中的节点与传进来的节点不一致的话，就不能添加 treeNode 到当前节点
		if (currentSelectedNode == treeRoot && !currentSelectedNode.toString().equals(arg)) {
			return;
		}
		if (currentSelectedNode.getUserObject() instanceof FileStatus) {
			FileStatus fStatus = (FileStatus) currentSelectedNode.getUserObject();
			if (!fStatus.getPath().toString().equals(arg.toString())) {
				return;
			}
		}
		List<FileStatus> fileStatus = ctx.getFileStatusModel().getUnmodifableFiles();
		refreshTree(fileStatus, currentSelectedNode);
		SwingUtilities.invokeLater(() -> tree.updateUI());
	}

	/**
	 * 渲染某个JTreeNode下的子节点信息
	 */
	private void refreshTree(List<FileStatus> list, DefaultMutableTreeNode node) {
		if (list == null || node == null)
			return;

		node.removeAllChildren();
		node.add(new DefaultMutableTreeNode("."));
		for (int i = list.size() - 1; i >= 0; i--) {

			FileStatus fileStatus = list.get(i);

			if (fileStatus.isDirectory()) {
				DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(fileStatus);
				// 添加空白文件夹节点 使子节点显示为文件夹
				subNode.add(new DefaultMutableTreeNode("."));
				// 计算所有文件夹大小
				subNode.setUserObject(list.get(i));
				node.add(subNode);
			}
		}

		// 删除原有的空节点
		if (currentSelectedNode.getChildCount() > 1)
			removeFirstChildNode(currentSelectedNode);
	}

	/**
	 * 删除第一个节点
	 */
	private void removeFirstChildNode(DefaultMutableTreeNode selectnode) {
		DefaultMutableTreeNode firstchild = (DefaultMutableTreeNode) selectnode.getFirstChild();
		int nNum = selectnode.getChildCount();
		if (nNum > 1) {
			selectnode.remove(firstchild);
		}
	}

	public void expandRootPath() {
		tree.expandPath(new TreePath(treeRoot));
	}

	public JScrollPane getMainView() {
		return this.mainView;
	}

	class MyTreeExpansionListener implements TreeExpansionListener {
		@Override
		public void treeCollapsed(TreeExpansionEvent e) {
			TreePath path = e.getPath();
			if (path == null)
				return;
			DefaultMutableTreeNode selectnode = (DefaultMutableTreeNode) path.getLastPathComponent();
			if (selectnode == treeRoot)
				return;
			if (selectnode.getChildCount() > 1) {
				for (int i = selectnode.getChildCount() - 1; i >= 0; i--) {
					TreePath pathChild = new TreePath(selectnode.getChildAt(i));
					DefaultMutableTreeNode childnode = (DefaultMutableTreeNode) pathChild.getLastPathComponent();
					if (childnode.getUserObject() instanceof FileStatus) {
						FileStatus file = (FileStatus) childnode.getUserObject();
						if (file != null)
							selectnode.remove(i);
					}
				}
			}
			addNullNode(selectnode);// 关闭树后增加一个空节点
		}

		/**
		 * tableModel没数据时，添加一行空行
		 */
		private void addNullNode(DefaultMutableTreeNode selectnode) {
			int nNum = selectnode.getChildCount();
			if (nNum < 1) {
				DefaultMutableTreeNode stub = new DefaultMutableTreeNode(".");
				selectnode.add(stub);
			}
		}

		@Override
		public void treeExpanded(TreeExpansionEvent e) {
			tree.setSelectionPath(e.getPath());
			TreePath path = tree.getSelectionPath();
			if (path == null)
				return;

			DefaultMutableTreeNode selectnode = (DefaultMutableTreeNode) path.getLastPathComponent();

			String targetPath;
			if (selectnode != treeRoot)
				targetPath = ((FileStatus) selectnode.getUserObject()).getPath().toString();
			else
				targetPath = treeRoot.getUserObject().toString();

			currentSelectedNode = selectnode;
			ctx.getMainModel().executeCmd(
					new GotoPathCmd(
							new Path(targetPath),
							new Path(ctx.getFileStatusModel().getNavigationPath()),
							aPath -> ctx.getMainModel().notification(aPath.toString())));
		}
	}

	class MyMouseAdapter extends MouseAdapter {

		@Override
		public void mousePressed(MouseEvent e) {
			TreePath path = tree.getPathForLocation(e.getX(), e.getY());
			tree.setSelectionPath(path);
		}

		@Override
		public void mouseReleased(final MouseEvent e) {
			FileStatus fileStatus = getSelectedFileStatus();
			if (fileStatus == null) {
				return;
			}
			if (e.getButton() == MouseEvent.BUTTON3) {
				PopupMenuUtil.show(mainView, null, TREE_MENU_ITEMS, e, new FileStatus[] { fileStatus });
			}
		}

		/**
		 * 从JTree上获取文件状态信息
		 */
		private FileStatus getSelectedFileStatus() {
			TreePath path = tree.getSelectionPath();
			if (path == null)
				return null;

			DefaultMutableTreeNode selectnode = (DefaultMutableTreeNode) path.getLastPathComponent();
			if (selectnode == treeRoot) {
				return new FileStatus(0, true, 0, 0, 0, 0, null, null, null, new Path("/"));
			}
			// 这里加上类型判断
			if (!(selectnode.getUserObject() instanceof FileStatus)) {
				return null;
			}
			return (FileStatus) selectnode.getUserObject();
		}
	}
}