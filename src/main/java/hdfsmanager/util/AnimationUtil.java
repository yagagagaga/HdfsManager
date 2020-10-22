package hdfsmanager.util;

import javax.swing.*;

public final class AnimationUtil {
	private AnimationUtil() {
		throw new IllegalStateException("工具类不能实例化");
	}

	public static void shakeFrame(JFrame frame) {
		if (frame == null)
			return;
		int x = frame.getX();
		int y = frame.getY();
		for (int i = 0; i < 4; i++) {
			x += (i & 1) == 0 ? 10 : -10;
			frame.setLocation(x, y);
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
