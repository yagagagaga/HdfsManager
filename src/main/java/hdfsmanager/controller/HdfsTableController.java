package hdfsmanager.controller;

import hdfsmanager.model.HdfsModel;
import hdfsmanager.view.HdfsTableView;

public class HdfsTableController extends MainController<HdfsTableView> {

	public HdfsTableController(HdfsModel model) {
		super(model, HdfsTableView.class);
	}
}
