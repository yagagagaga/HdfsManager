package hdfsmanager.support.command;

public interface Command {
	void execute();

	void undoExecute();

	boolean compareCmd(Command cmd);
}