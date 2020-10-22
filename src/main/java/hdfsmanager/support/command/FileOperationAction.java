package hdfsmanager.support.command;

import javax.swing.*;

import org.apache.hadoop.fs.FileStatus;

import hdfsmanager.mvc.controller.MainController;

@FunctionalInterface
public interface FileOperationAction {
	void doAction(
			final JComponent trigger,
			final MainController<?> ctr,
			final FileStatus[] fs);
}