package hdfsmanager.mvc.controller;

import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.hadoop.fs.FileStatus;

import hdfsmanager.mvc.model.HdfsModel;
import hdfsmanager.mvc.view.HdfsTreeView;

public class HdfsTreeController extends MainController<HdfsTreeView> {

	public HdfsTreeController(HdfsModel model) {
		super(model, HdfsTreeView.class);
	}

	public void treeExpanded(DefaultMutableTreeNode node) {
		model.updateFileStatusFrom(((FileStatus) node.getUserObject()).getPath());
	}
}
