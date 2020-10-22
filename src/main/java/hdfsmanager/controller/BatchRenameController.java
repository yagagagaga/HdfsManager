package hdfsmanager.controller;

import hdfsmanager.api.Controller;
import hdfsmanager.model.BatchRenameModel;
import hdfsmanager.view.BatchRenameView;

public class BatchRenameController extends Controller<BatchRenameModel, BatchRenameView> {

	public BatchRenameController(BatchRenameModel model) {
		super(model, BatchRenameView.class);
	}

	public void rename(String searchStr, String replaceStr) {
		model.rename(searchStr, replaceStr);
		view.dispose();
	}
}
