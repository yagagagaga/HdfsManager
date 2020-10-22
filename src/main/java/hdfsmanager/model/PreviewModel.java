package hdfsmanager.model;

import hdfsmanager.api.Model;

public class PreviewModel extends Model {

	private byte[] content;

	@Override
	public void initialize() {
		content = new byte[0];
	}

	public byte[] getContent() {
		return this.content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}
}
