package hdfsmanager.mvc.controller;

import hdfsmanager.api.Controller;
import hdfsmanager.mvc.model.PreviewModel;
import hdfsmanager.mvc.view.PreviewView;

public class PreviewController extends Controller<PreviewModel, PreviewView> {

    public PreviewController() {
        super(PreviewModel.class, PreviewView.class);
    }

    public void show(byte[] content) {
        model.setContent(content);
        view.show(content, "UTF-8");
    }

    public void changeCharset(String charset) {
        byte[] content = model.getContent();
        view.show(content, charset);
    }
}
