package hdfsmanager.util;

import static hdfsmanager.support.command.ActionEnum.SEPARATOR;

import java.awt.event.MouseEvent;

import javax.swing.*;

import org.apache.hadoop.fs.FileStatus;

import hdfsmanager.support.command.ActionEnum;
import hdfsmanager.controller.MainController;

public final class PopupMenuUtil extends JPopupMenu {

	private PopupMenuUtil() {
		throw new IllegalStateException("工具类不允许实例化");
	}

	public static void show(
			final JComponent trigger,
			final MainController ctr,
			final ActionEnum[] menuItems,
			final MouseEvent e,
			final FileStatus[] fs) {
		JPopupMenu menu = new JPopupMenu();
		for (ActionEnum action : menuItems) {
			final String name = action.getName();
			if (name.equals(SEPARATOR.getName()))
				menu.addSeparator();
			else {
				JMenuItem item = new JMenuItem(action.getName());
				item.addActionListener(ae -> action.getAction().doAction(trigger, ctr, fs));
				menu.add(item);
			}
		}
		menu.show(e.getComponent(), e.getX(), e.getY());
	}
}
