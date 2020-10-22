package hdfsmanager.util;

import java.io.File;
import java.util.Optional;

import javax.swing.*;

import org.apache.log4j.Logger;

import hdfsmanager.mvc.controller.PreviewController;

public final class DialogUtil {

	private static final Logger log = Logger.getLogger(DialogUtil.class);

	private DialogUtil() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	@SuppressWarnings("all")
	public static boolean confirm(String msg, String title, MsgType type) {
		final int resultCode = JOptionPane.showConfirmDialog(null, msg, title, JOptionPane.YES_NO_OPTION, type.getLevel());
		return resultCode == JOptionPane.YES_OPTION;
	}

	public static Optional<String> input(String msg, String title, MsgType type) {
		return input(msg, title, "", type);
	}

	@SuppressWarnings("all")
	public static Optional<String> input(String msg, String title, String placeholder, MsgType type) {
		final String userInputString = JOptionPane.showInputDialog(null, msg, title, type.getLevel());

		if (userInputString != null) {
			return Optional.of(userInputString);
		}
		return Optional.empty();
	}

	@SuppressWarnings("all")
	public static void show(String msg, String title, MsgType type) {
		JOptionPane.showMessageDialog(null, msg, title, type.getLevel());
	}

	@SuppressWarnings("all")
	public static <T> Optional<T> select(String msg, String title, MsgType type, T... items) {
		Object userSelectedItem = JOptionPane.showInputDialog(null, msg, title, type.getLevel(), null, items, null);

		if (userSelectedItem != null) {
			return Optional.of((T) userSelectedItem);
		}
		return Optional.empty();
	}

	@SuppressWarnings("all")
	public static <T> Optional<T> option(String msg, String title, MsgType type, T... items) {
		final int selectedIndex = JOptionPane.showOptionDialog(null, msg, title, JOptionPane.YES_NO_CANCEL_OPTION, type.getLevel(), null, items,
				null);

		if (selectedIndex != -1) {
			return Optional.of(items[selectedIndex]);
		}
		return Optional.empty();
	}

	/**
	 * 文件选择器
	 *
	 * @param selectionMode 值为{@link JFileChooser#FILES_ONLY}、{@link JFileChooser#DIRECTORIES_ONLY}和{@link JFileChooser#FILES_AND_DIRECTORIES}这三者中的一个
	 * @param title         标题
	 * @return 返回File数组，如果未启用多选模式，则返回的数组里只有一个File，如果用户取消了操作，则返回File[0]
	 */
	public static File[] chooseFiles(int selectionMode, String title) {

		GuiUtil.initLookAndFeel();

		JFileChooser fc = new JFileChooser();
		fc.setApproveButtonText("确定");
		fc.setFileSelectionMode(selectionMode);
		fc.setMultiSelectionEnabled(true);
		fc.setDialogTitle(title);
		int result = fc.showOpenDialog(null);

		if (JFileChooser.APPROVE_OPTION == result) {
			return fc.getSelectedFiles();
		} else {
			return new File[0];
		}
	}

	public static Optional<File> chooseFile(int selectionMode, String title) {
		GuiUtil.initLookAndFeel();

		JFileChooser fc = new JFileChooser();
		fc.setApproveButtonText("确定");
		fc.setFileSelectionMode(selectionMode);
		fc.setMultiSelectionEnabled(false);
		fc.setDialogTitle(title);
		int result = fc.showOpenDialog(null);

		if (JFileChooser.APPROVE_OPTION == result) {
			return Optional.of(fc.getSelectedFile());
		}
		return Optional.empty();
	}

	public static void preview(byte[] content) {
		PreviewController c = new PreviewController();
		c.show(content);
	}
}