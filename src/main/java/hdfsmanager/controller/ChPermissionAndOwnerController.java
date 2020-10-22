package hdfsmanager.controller;

import hdfsmanager.api.Controller;
import hdfsmanager.model.HdfsModel;
import hdfsmanager.view.ChPermissionAndOwnerView;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.permission.FsAction;
import org.apache.hadoop.fs.permission.FsPermission;

public class ChPermissionAndOwnerController extends Controller<HdfsModel, ChPermissionAndOwnerView> {

	public ChPermissionAndOwnerController(HdfsModel model, Controller<?, ?>... otherControllers) {
		super(model, ChPermissionAndOwnerView.class, otherControllers);
		FileStatus[] fs = model.getSelectedFileStatuses();
		if (fs.length == 0)
			return;
		FileStatus f = fs[0];
		FsPermission p = f.getPermission();
		String owner = f.getOwner();
		String group = f.getGroup();
		setInitValue(p, owner, group);
	}

	private void setInitValue(FsPermission p, String owner, String group) {
		view.setInitValue(p, owner, group);
	}

	public void aa(String permission, String owner, String group) {
		FileStatus[] fs = model.getSelectedFileStatuses();

		try {
			FsPermission pm = parseFsPermission(permission);
			for (FileStatus f : fs) {
				model.chPermissionAndOwner(f, pm, owner, group);
			}
			view.dispose();
			model.updateFileStatusFrom(model.getCurrentPath(), false);
		} catch (Exception e) {
			view.playFailedLoginAnimation(e);
		}
	}

	private FsPermission parseFsPermission(String str) {
		if (str.length() != 9) {
			throw new IllegalArgumentException("必须输入9个字符");
		}
		FsAction[] actions = new FsAction[3];
		for (int i = 0; i < 3; i++) {
			actions[i] = FsAction.getFsAction(str.substring(i * 3, i * 3 + 3));
		}
		return new FsPermission(actions[0], actions[1], actions[2]);
	}
}
