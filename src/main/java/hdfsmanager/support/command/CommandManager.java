package hdfsmanager.support.command;

import java.util.ArrayDeque;
import java.util.Deque;

public class CommandManager {
	private final Deque<Command> cmdUndoStack = new ArrayDeque<>();
	private final Deque<Command> cmdRedoStack = new ArrayDeque<>();

	public void executeCommand(Command cmd) {
		cmd.execute();
		// 如果 redo 栈为空，则入栈；
		// 否则判断要压入的命令是否与之前压入的命令相同，不相同才压栈
		if (cmdUndoStack.isEmpty() || !cmd.compareCmd(cmdUndoStack.peek()))
			cmdUndoStack.push(cmd);
	}

	public void undo() {
		if (!cmdUndoStack.isEmpty()) {
			Command cmd = cmdUndoStack.pop();
			cmd.undoExecute();
			cmdRedoStack.push(cmd);
		}
	}

	public void redo() {
		if (!cmdRedoStack.isEmpty()) {
			Command cmd = cmdRedoStack.pop();
			cmd.execute();
			cmdUndoStack.push(cmd);
		}
	}
}
