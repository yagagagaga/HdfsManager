package hdfsmanager.support.command;

import org.apache.hadoop.fs.Path;

import java.util.function.Consumer;

public class GotoPathCmd implements Command {
	private final Consumer<Path> executor;
	private final Path nextPath;
	private final Path previousPath;

	public GotoPathCmd(Path nextPath, Path previousPath, Consumer<Path> executor) {
		this.nextPath = nextPath;
		this.previousPath = previousPath;
		this.executor = executor;
	}

	@Override
	public void execute() {
		if (nextPath != null)
			executor.accept(nextPath);
	}

	@Override
	public void undoExecute() {
		if (previousPath != null)
			executor.accept(previousPath);
	}

	@Override
	public boolean compareCmd(Command cmd) {
		if (!(cmd instanceof GotoPathCmd))
			return false;
		if (this.nextPath == null || this.previousPath == null)
			return false;
		GotoPathCmd gotoPathCmd = (GotoPathCmd) cmd;
		return this.nextPath.equals(gotoPathCmd.nextPath) &&
				this.previousPath.equals(gotoPathCmd.previousPath);
	}
}