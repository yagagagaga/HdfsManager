package hdfsmanager.util;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Optional;

public final class SysUtil {
	private SysUtil() {
		throw new IllegalStateException("工具类不能实例化");
	}

	public static Optional<String> getClipboard() {
		Transferable transferable = Toolkit.getDefaultToolkit()
				.getSystemClipboard()
				.getContents(null);
		if (!transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			return Optional.empty();
		}
		try {
			String res = (String) transferable.getTransferData(DataFlavor.stringFlavor);
			return Optional.of(res);
		} catch (UnsupportedFlavorException | IOException e) {
			return Optional.empty();
		}
	}

	public static void setClipboard(String content) {
		Toolkit.getDefaultToolkit()
				.getSystemClipboard()
				.setContents(new StringSelection(content), null);
	}
}
