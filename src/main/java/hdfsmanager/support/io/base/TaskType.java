package hdfsmanager.support.io.base;

public enum TaskType {
	UNKNOWN_TYPE(-1, "未知类型"),
	DOWNLOAD_FILE(0, "下载文件"), UPLOAD_FILE(1, "上传文件"),
	DOWNLOAD_DIR(2, "下载文件夹"), UPLOAD_DIR(3, "上传文件夹"),
	DOWNLOAD_5M(4, "下载5M文件"), MERGE_DOWNLOAD(5, "归并压缩下载"),
	UNCOMPRESS_UPLOAD(6, "解压上传文件");

	private final int value;

	private final String description;

	TaskType(int value, String description) {
		this.value = value;
		this.description = description;
	}

	public String toString() {
		return getDescription();
	}

	public int getValue() {
		return this.value;
	}

	public String getDescription() {
		return this.description;
	}
}