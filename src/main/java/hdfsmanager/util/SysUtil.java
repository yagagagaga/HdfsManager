package hdfsmanager.util;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.hadoop.fs.FileStatus;

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

	public static <T> Optional<T> getClipboard(Class<T> clazz) {
		final Clipboard clipboard = Toolkit.getDefaultToolkit()
				.getSystemClipboard();
		try {
			String mimeType;
			if (clazz.isArray()) {
				mimeType = "application/x-java-jvm-local-objectref;class = " + ArrayList.class.getName();
			} else {
				mimeType = "application/x-java-jvm-local-objectref;class = " + clazz.getName();
			}
			DataFlavor flavor = new DataFlavor(mimeType);
			if (!clipboard.isDataFlavorAvailable(flavor)) {
				return Optional.empty();
			}
			final Object data = clipboard.getData(flavor);
			if (clazz.isArray()) {
				final List list = ((List) data);
				if (list.isEmpty()) {
					return Optional.empty();
				}
				final Object o = Array.newInstance(list.get(0).getClass(), list.size());
				for (int i = 0; i < list.size(); i++) {
					Array.set(o, i, list.get(i));
				}
				// noinspection unchecked
				return Optional.of((T) o);
			} else {
				// noinspection unchecked
				return Optional.of((T) data);
			}
		} catch (UnsupportedFlavorException | IOException | ClassNotFoundException e) {
			return Optional.empty();
		}
	}

	public static <T> void setClipboard(T content) {
		LocalObjectSelection<?> los;
		if (content.getClass().isArray()) {
			final int len = Array.getLength(content);
			List<Object> list = new ArrayList<>(len);
			for (int i = 0; i < len; i++) {
				list.add(Array.get(content, i));
			}
			los = new LocalObjectSelection<>(list);
		} else {
			los = new LocalObjectSelection<>(content);
		}

		Toolkit.getDefaultToolkit()
				.getSystemClipboard()
				.setContents(los, null);
	}

	private static class LocalObjectSelection<T> implements Transferable {

		private final T data;

		public LocalObjectSelection(T data) {
			this.data = data;
		}

		@Override
		public DataFlavor[] getTransferDataFlavors() {
			DataFlavor[] flavor = new DataFlavor[2];
			Class<?> clazz = data.getClass();
			String mimeType = "application/x-java-jvm-local-objectref;" +
					"class = " + clazz.getName();
			try {
				flavor[0] = new DataFlavor(mimeType);
				flavor[1] = DataFlavor.stringFlavor;
				return flavor;
			} catch (ClassNotFoundException e) {
				throw new IllegalStateException(e);
			}
		}

		@Override
		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return flavor.equals(DataFlavor.stringFlavor) ||
					flavor.getPrimaryType().equals("application") &&
							flavor.getSubType().equals("x-java-jvm-local-objectref") &&
							flavor.getRepresentationClass().isAssignableFrom(data.getClass());
		}

		@SuppressWarnings("NullableProblems")
		@Override
		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException, IOException {
			if (flavor.equals(DataFlavor.stringFlavor)) {
				return data.toString();
			}
			if (!isDataFlavorSupported(flavor)) {
				throw new UnsupportedFlavorException(flavor);
			}
			return data;
		}
	}
}
