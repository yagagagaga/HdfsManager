package hdfsmanager.support.command;

import hdfsmanager.util.DialogUtil;
import hdfsmanager.util.MsgType;

public final class Action {

	private Action() {
		throw new IllegalStateException("工具类不能被初始化");
	}

	static FileOperationAction unCompleteAction = (trigger, ctr, fileStatuses) -> DialogUtil.show("暂未实现", "", MsgType.INFORMATION);

	static FileOperationAction downAction = (trigger, ctr, fileStatuses) -> ctr.download(fileStatuses);

	static FileOperationAction down5MAction = (trigger, ctr, fileStatuses) -> ctr.download5M(fileStatuses);

	static FileOperationAction uploadAction = (trigger, ctr, fileStatuses) -> ctr.uploadTo(fileStatuses);

	static FileOperationAction newFolderAction = (trigger, ctr, fileStatuses) -> ctr.newFolder(fileStatuses);

	static FileOperationAction addSuccessAction = (trigger, ctr, fileStatuses) -> ctr.addSuccessFlag(fileStatuses);

	static FileOperationAction delAction = (trigger, ctr, fileStatuses) -> ctr.del(fileStatuses);

	static FileOperationAction renameAction = (trigger, ctr, fileStatuses) -> ctr.rename(fileStatuses);

	static FileOperationAction previewAction = (trigger, ctr, fileStatuses) -> ctr.preview(fileStatuses[0]);

	static FileOperationAction copyPathAction = (trigger, ctr, fileStatuses) -> ctr.copyPath(fileStatuses);

	static FileOperationAction copyTableInfoAction = (trigger, ctr, fileStatuses) -> ctr.copyTableInfo(fileStatuses);

	static FileOperationAction batchRenameAction = (trigger, ctr, fileStatuses) -> ctr.batchRename(fileStatuses);

	static FileOperationAction concatFile = (trigger, ctr, fileStatuses) -> ctr.concat(fileStatuses);

	static FileOperationAction empower777 = (trigger, ctr, fileStatuses) -> ctr.empower777(fileStatuses);

	static FileOperationAction copyAction = (trigger, ctr, fileStatuses) -> ctr.copyFiles(fileStatuses);

	static FileOperationAction cutAction = (trigger, ctr, fileStatuses) -> ctr.cutFiles(fileStatuses);

	static FileOperationAction pasteAction = (trigger, ctr, fileStatuses) -> ctr.pasteFilesTo(fileStatuses);
}
