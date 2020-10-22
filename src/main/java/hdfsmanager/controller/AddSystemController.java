package hdfsmanager.controller;

import hdfsmanager.api.Controller;
import hdfsmanager.model.AddSystemModel;
import hdfsmanager.view.AddSystemView;

public class AddSystemController extends Controller<AddSystemModel, AddSystemView> {
	public AddSystemController(AddSystemModel model) {
		super(model, AddSystemView.class);
	}
}
