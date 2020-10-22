package hdfsmanager;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;
import java.util.Observable;

import javax.swing.*;

import org.apache.hadoop.fs.FileStatus;
import org.apache.log4j.Logger;

import hdfsmanager.api.dao.HdfsDao;
import hdfsmanager.support.command.Command;
import hdfsmanager.support.command.CommandManager;

public class MainModel extends Observable {

	private static final Logger log = Logger.getLogger(MainModel.class);
	private final HdfsDao hdfsDao;
	private final FileStatusModel fileStatusModel;

	/**
	 * 命令管理器，用于处理 undo 和 redo 操作
	 */
	private final CommandManager cmdManager;

	/**
	 * loading 面板，在界面繁忙的时候显示
	 */
	private JPanel loadingPane;

	public MainModel(HdfsDao hdfsDao, FileStatusModel fileStatusModel) {
		this.cmdManager = new CommandManager();
		this.hdfsDao = hdfsDao;
		this.fileStatusModel = fileStatusModel;
	}

	/**
	 * 通知被观察者
	 */
	public void notification(final String path) {
		this.loadingPane.setVisible(true); // 设置界面为加载中
		this.setChanged(); // 设置允许通知被观察者
		// todo 这里要改程线程池
		new Thread(() -> {
			try {
				final List<FileStatus> fs = hdfsDao.listAllFiles(path, false, true);
				fileStatusModel.update(path, fs);
				notifyObservers(path); // 通知所有被观察者更新数据
			} catch (Exception e) {
				log.error(e);
			} finally {
				loadingPane.setVisible(false); // 无论有无异常，界面都需回复正常
			}
		}).start();
	}

	/**
	 * 粘贴文件/文件夹
	 */
	public void pasteFile(String curpath) throws IOException, UnsupportedFlavorException {
		// 获取系统剪切板的文本内容[如果系统剪切板复制的内容是文本]
		Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(null); // 跟上面三行代码一样

		if (null != t && t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
			String tpath = (String) t.getTransferData(DataFlavor.stringFlavor);
			if (tpath.startsWith("cp")) {
				String[] tpaths = tpath.substring(2).split("\n");
				for (String targetPath : tpaths) {
					try {
						hdfsDao.copyFile(targetPath, curpath);
						log.info("已成功将【" + targetPath + "】复制到【" + curpath + "】");
					} catch (IOException e) {
						log.error("【" + targetPath + "】复制失败！！！", e);
					}

				}
			} else if (tpath.startsWith("mv")) {
				String[] tpaths = tpath.substring(2).split("\n");
				for (String targetPath : tpaths) {
					try {
						hdfsDao.movefile(targetPath, curpath);
						log.info("成功将【" + targetPath + "】剪切到【" + curpath + "】");
					} catch (IOException e) {
						log.error("【" + targetPath + "】剪切到【" + curpath + "】过程中出现了" + e.getMessage(), e);
					}
				}
			}
		}

	}

	public String getNavigationPath() {
		return fileStatusModel.getNavigationPath();
	}

	/**
	 * 刷新数据
	 */
	public void refresh() {
		refresh(getNavigationPath());
	}

	/**
	 * 刷新数据
	 */
	public void refresh(String path) {
		try {
			notification(path);
		} catch (Exception e) {
			log.info("出现了异常：" + e.getMessage());
		}
	}

	public void executeCmd(Command cmd) {
		this.cmdManager.executeCommand(cmd);
	}

	/**
	 * undo
	 */
	public void undo() {
		cmdManager.undo();
	}

	/**
	 * redo
	 */
	public void redo() {
		cmdManager.redo();
	}

	public void setLoadingPane(JPanel loadingPane) {
		this.loadingPane = loadingPane;
	}
}