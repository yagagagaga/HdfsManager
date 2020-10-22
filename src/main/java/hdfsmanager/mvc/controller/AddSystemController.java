package hdfsmanager.mvc.controller;

import hdfsmanager.api.Controller;
import hdfsmanager.mvc.model.AddSystemModel;
import hdfsmanager.mvc.view.AddSystemView;

public class AddSystemController extends Controller<AddSystemModel, AddSystemView> {
    public AddSystemController(AddSystemModel model) {
        super(model, AddSystemView.class);
    }
}
