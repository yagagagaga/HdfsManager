package hdfsmanager.support.command;

public enum ActionEnum {

	SEPARATOR("|", Action.unCompleteAction),
	DOWN_FILE_AND_DIR("下载", Action.downAction),
	DOWN_5M_FILE("下载5M", Action.down5MAction),
	UP_FILE_AND_DIR("上传", Action.uploadAction),
	NEW_FOLDER("新建文件夹", Action.newFolderAction),
	ADD_SUCCESS_FLAG("添加SUCCESS标志文件", Action.addSuccessAction),
	MERGE_FILES("合并为一个文件", Action.concatFile),
	DEL_FILE("删除", Action.delAction),
	RENAME_FILE("重命名", Action.renameAction),
	BATCH_RENAME("批量重命名", Action.batchRenameAction),
	CUT_FILE("剪切", Action.cutAction),
	COPY_FILE("复制", Action.copyAction),
	PASTE_FILE("粘贴", Action.pasteAction),
	PREVIEW_FILE("预览文件", Action.previewAction),
	COPY_PATH("复制完整名称", Action.copyPathAction),
	COPY_TABLE_INFO("复制表信息", Action.copyTableInfoAction),
	EMPOWER_777("赋权777", Action.empower777);

	private final String name;

	private final FileOperationAction action;

	ActionEnum(String name, FileOperationAction action) {
		this.name = name;
		this.action = action;
	}

	public String getName() {
		return this.name;
	}

	public FileOperationAction getAction() {
		return this.action;
	}
}
