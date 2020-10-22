package hdfsmanager.frame.main_frame.bottom_view.bottom_view.tab_view1;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.io.Writer;
import java.util.Scanner;

import javax.swing.*;

import org.apache.log4j.Appender;
import org.apache.log4j.Logger;
import org.apache.log4j.WriterAppender;

class LogRecordView extends JTextArea implements Runnable {

	private static final Logger log = Logger.getLogger(LogRecordView.class);
	private final transient PipedReader reader;

	LogRecordView() {
		super();
		Logger root = Logger.getRootLogger();
		// 获取子记录器的输出源
		Appender appender = root.getAppender("console");
		// 定义一个未连接的输入流管道
		reader = new PipedReader();
		try {
			// 定义一个已连接的输出流管理，并连接到reader
			Writer writer = new PipedWriter(reader);
			// 设置 appender 输出流
			((WriterAppender) appender).setWriter(writer);

			// 开始输出日志
			new Thread(this).start();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public void run() {
		// 不间断地扫描输入流
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(reader);
		// 将扫描到的字符流输出到指定的JTextArea组件
		while (scanner.hasNextLine()) {
			try {
				// 睡眠
				// noinspection BusyWait
				Thread.sleep(100);
				String line = scanner.nextLine();
				this.append(line);
				this.append("\n");
			} catch (Exception e) {
				log.error(e);
			}
		}
	}
}