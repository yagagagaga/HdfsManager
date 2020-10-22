package hdfsmanager.util;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.TableColumn;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

public final class GuiUtil {

	private static final Map<String, Icon> ICON_MAP = new HashMap<>();
	private static final Logger log = Logger.getLogger(GuiUtil.class);

	private GuiUtil() {
		throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
	}

	/**
	 * 隐藏表格的某一列
	 * 
	 * @param tb    表格
	 * @param index 列下标（从0开始）
	 */
	public static void hideTableColumn(JTable tb, int index) {
		TableColumn tc = tb.getColumnModel().getColumn(index);
		tc.setMinWidth(0);
		tc.setMaxWidth(0);
		tc.setPreferredWidth(0);
		tc.setWidth(0);
		tb.getTableHeader().getColumnModel().getColumn(index).setMaxWidth(0);
		tb.getTableHeader().getColumnModel().getColumn(index).setMinWidth(0);
	}

	public static void initLookAndFeel() {
		if (UIManager.getLookAndFeel().isSupportedLookAndFeel()) {
			final String platform = UIManager.getSystemLookAndFeelClassName();
			if (!UIManager.getLookAndFeel().getName().equals(platform)) {
				try {
					UIManager.setLookAndFeel(platform);

					// 是否使用粗体
					UIManager.put("swing.boldMetal", Boolean.FALSE);
					UIDefaults uId = UIManager.getDefaults();
					FontUIResource font = new FontUIResource("宋体", Font.PLAIN, 13);
					uId.put("Button.font", font);
					uId.put("TextField.font", font);
					uId.put("Label.font", font);
					uId.put("TextArea.font", font);
					uId.put("TableHeader.font", font);
					uId.put("Table.font", font);
					uId.put("Tree.font", font);
					uId.put("Menu.font", font);
					uId.put("ComboBox.font", font);
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * 计算出屏幕的中心点
	 */
	public static Point calcScreenCenterLocation(Component component) {
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		Dimension componentSize = component.getSize();
		int x = (screenSize.width - componentSize.width) >> 1;
		int y = (screenSize.height - componentSize.height) >> 1;
		return new Point(x, y);
	}

	/**
	 * 获取系统图标
	 */
	public static Icon getSystemIcon(String fileName, boolean isDir) {
		if (isDir) {
			Icon ret = ICON_MAP.get("dir");
			if (ret == null) {
				try {
					File file = new File(UUID.randomUUID().toString());
					FileUtils.forceMkdir(file);
					ICON_MAP.put("dir", FileSystemView.getFileSystemView().getSystemIcon(file));
					FileUtils.forceDelete(file);
				} catch (IOException ignored) {
					// 忽略
				}
			}
			return ICON_MAP.get("dir");
		}
		if (fileName == null || fileName.length() == 0) {
			return null;
		}

		String ext = (!fileName.contains(".") || fileName.lastIndexOf('.') == fileName.length() - 1)
				? "unknown"
				: fileName.substring(fileName.lastIndexOf('.') + 1);
		if (ext.contains(":") || ext.contains("/"))
			ext = "unknown";
		try {
			Icon ret = ICON_MAP.get(ext);
			if (ret == null) {
				File file = File.createTempFile("icon", "." + ext);
				ICON_MAP.put(ext, FileSystemView.getFileSystemView().getSystemIcon(file));
				FileUtils.forceDelete(file);
			}
			return ICON_MAP.get(ext);
		} catch (IOException e) {
			log.info("获取系统图标失败，失败原因:" + e.getMessage());
			return null;
		}
	}

	/**
	 * 将组件挪到屏幕中心
	 */
	public static void setToCenterLocation(Component component) {
		Point point = calcScreenCenterLocation(component);
		component.setLocation(point);
	}

	/**
	 * 绑定 esc 按键事件
	 */
	public static void bindEscapeAction(JPanel panel, ActionListener listener) {
		panel.registerKeyboardAction(
				listener,
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	/**
	 * 窗口尺寸全屏化（非最大化）
	 */
	public static void setFullSize(JFrame window) {
		window.setSize(Toolkit.getDefaultToolkit().getScreenSize());
	}

	/**
	 * 推送系统通知
	 * 
	 * @param caption 标题
	 * @param text    内容
	 * @param type    通知类型，有【警告】、【错误】、【消息】三种类型
	 */
	public static void displayTray(String caption, String text, TrayIcon.MessageType type) {
		SystemTray tray = SystemTray.getSystemTray();
		Image image = Toolkit.getDefaultToolkit().createImage("ico.png");
		TrayIcon trayIcon = new TrayIcon(image);
		trayIcon.setImageAutoSize(true);
		try {
			tray.add(trayIcon);
			trayIcon.displayMessage(caption, text, type);
		} catch (AWTException e) {
			throw new IllegalStateException(e);
		}
	}
}
