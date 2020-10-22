package hdfsmanager.mvc.controller;

import hdfsmanager.mvc.model.HdfsModel;
import hdfsmanager.mvc.view.HdfsTableView;

public class HdfsTableController extends MainController<HdfsTableView> {

	public HdfsTableController(HdfsModel model) {
		super(model, HdfsTableView.class);
	}
}
