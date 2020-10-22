package hdfsmanager.model;

import hdfsmanager.api.Model;

public class AddSystemModel extends Model {

	private final HdfsModel model;

	public AddSystemModel(HdfsModel model) {
		this.model = model;
	}

	@Override
	public void initialize() {

	}
}
