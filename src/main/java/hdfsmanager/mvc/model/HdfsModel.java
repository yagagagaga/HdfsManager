package hdfsmanager.mvc.model;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import hdfsmanager.exception.UnexpectedException;
import hdfsmanager.util.*;
import io.vavr.Tuple2;
import org.apache.commons.io.FileUtils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.log4j.Logger;

import hdfsmanager.api.Model;
import hdfsmanager.api.dao.AppStatusDao;
import hdfsmanager.api.dao.HdfsDao;
import hdfsmanager.support.command.CommandManager;
import hdfsmanager.support.command.GotoPathCmd;
import hdfsmanager.support.io.DownloadFileTask;
import hdfsmanager.support.io.base.BaseTask;

public class HdfsModel extends Model {

	private static final Logger log = Logger.getLogger(HdfsModel.class);

	private final HdfsDao hdfsDao;
	private final AppStatusDao appDao;
	private final CommandManager pathNavigationManager = new CommandManager();

	public HdfsModel(HdfsDao hdfsDao, AppStatusDao appDao) {
		this.hdfsDao = hdfsDao;
		this.appDao = appDao;
	}

	@Override
	public void initialize() {
		// do nothing
	}

	/**
	 * 更新当前路径下的文件
	 * 
	 * @param p               文件路径
	 * @param needComparePath 是否需要比较路径，如果路径一样，那么就不用更新
	 */
	public void updateFileStatusFrom(Path p, boolean needComparePath) {
		if (needComparePath && appDao.isCurrentPathEqualsTo(p)) {
			notifyObservers(UpdateLevel.UI_AND_DATA);
			return;
		}

		GotoPathCmd cmd = new GotoPathCmd(p, appDao.getCurrentPath(), path -> {
			notifyObservers(UpdateLevel.LOADING);
			EventLoop.submit(() -> {
				List<FileStatus> files;
				try {
					files = hdfsDao.listAllFiles(path, false, true);
				} catch (IOException e) {
					log.error(e.getMessage(), e);
					files = Collections.emptyList();
				}
				appDao.setCurrentPathAndFiles(path, files);
				notifyObservers(UpdateLevel.UI_AND_DATA);
				notifyObservers(UpdateLevel.LOADED);
			});
		});
		pathNavigationManager.executeCommand(cmd);
	}

	public void updateFileStatusFrom(Path p) {
		updateFileStatusFrom(p, true);
	}

	public byte[] read256KBOf(FileStatus f) {
		return hdfsDao.readFile(f.getPath(), FileUtils.ONE_MB >> 2);
	}

	public Path getCurrentPath() {
		return appDao.getCurrentPath();
	}

	public List<FileStatus> getFileStatuses() {
		return appDao.getFilesInCurrentPath();
	}

	public FileStatus[] getSelectedFileStatuses() {
		return appDao.getSelectedFileStatuses();
	}

	public HdfsDao getDao() {
		return hdfsDao;
	}

	public void gotoPreviousPath() {
		pathNavigationManager.undo();
	}

	public void gotoNextPath() {
		pathNavigationManager.redo();
	}

	public BaseTask downloadFromHdfs(FileStatus f, File targetPath) {
		Path p = f.getPath();
		BaseTask task;
		if (f.isDirectory()) {
			task = hdfsDao.downloadDirFromHdfs(p, targetPath);
			log.info("成功创建下载文件夹【" + p + "】到【" + targetPath + "】的任务");
		} else {
			task = hdfsDao.downloadFileFromHdfs(p, targetPath);
			log.info("成功创建下载文件【" + p + "】到【" + targetPath + "】的任务");
		}
		return task;
	}

	public DownloadFileTask download5MFileFromHdfs(Path path, File targetPath) {
		return hdfsDao.download5MFileFromHdfs(path, targetPath);
	}

	public BaseTask uploadToHdfs(File f, Path hdfsDstPath) {
		if (f.isDirectory())
			return hdfsDao.uploadDirToHdfs(f, hdfsDstPath);
		else
			return hdfsDao.uploadFileToHdfs(f, hdfsDstPath);
	}

	public void newFolder(Path target) {
		try {
			hdfsDao.mkdir(target);
			log.info("成功创建文件夹【" + target + "】");
		} catch (IOException e) {
			String errorMsg = "创建文件夹" + target + "失败，问题是" + e.getMessage();
			log.error(errorMsg, e);
			GuiUtil.displayTray("【错误】", errorMsg, TrayIcon.MessageType.ERROR);
		}
	}

	public void addSuccessFlag(Path target) {
		try {
			hdfsDao.addSuccessFlag(target);
			log.info("成功创建 _SUCCESS 标志到【" + target + "】");
		} catch (IOException e) {
			String errorMsg = "创建 _SUCCESS 标志时出现异常，问题是" + e.getMessage();
			log.error(errorMsg, e);
			GuiUtil.displayTray("【错误】", errorMsg, TrayIcon.MessageType.ERROR);
		}
	}

	public void delete(Path path) {
		try {
			hdfsDao.delete(path);
			log.info("【" + path + "】删除成功！！！");
		} catch (IOException e) {
			String errorMsg = path + "删除失败！！！问题是" + e.getMessage();
			log.error(errorMsg, e);
			GuiUtil.displayTray("【错误】", errorMsg, TrayIcon.MessageType.ERROR);
		}
	}

	public boolean rename(String src, String dst) {
		try {
			hdfsDao.rename(src, dst);
			return true;
		} catch (IOException | IllegalArgumentException e) {
			String errorMsg = "重命名失败，因为发生了" + e.getMessage();
			log.error(errorMsg, e);
			GuiUtil.displayTray("【错误】", errorMsg, TrayIcon.MessageType.ERROR);
			return false;
		}
	}

	public void concat(FileStatus[] fs) {
		try {
			List<FileStatus> allFs = hdfsDao.listAllFiles(fs[0].getPath(), false, false);
			List<FileStatus> fileStatuses = filterFileForConcat(allFs, hdfsDao);
			List<Tuple2<String, String[]>> tuple2s = groupFile(fileStatuses);
			for (Tuple2<String, String[]> t : tuple2s) {
				hdfsDao.concat(t._1(), t._2());
			}
			DialogUtil.show("合并完成", "", MsgType.INFORMATION);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

	/**
	 * 过滤掉不符合合并条件的文件，且已经按文件大小从小到大排序完毕
	 */
	private List<FileStatus> filterFileForConcat(List<FileStatus> files, HdfsDao dao) throws IOException {
		List<FileStatus> ret = new LinkedList<>();
		// 用来保存 存在某个后缀名的文件是否需要合并
		Map<String, Boolean> concatWithSuffixNameMatcher = new HashMap<>();
		long twoGb = 1L << 31; // 2GB，2*1024*1024*1024
		for (FileStatus f : files) {
			final String fileName = f.getPath().getName();
			if (f.isDirectory() ||
					f.getLen() >= twoGb ||
					fileName.contains("_SUCCESS"))
				continue;

			/*
			 * 如果有后缀名，如 .gitignore a.txt 等需要询问用户是否合并； 诸如 abc. . 等文件无需搭理；
			 * 如果发现后缀名超过16位的，则认为这个不是不后缀
			 */
			if (fileName.matches(".*\\..+")) {
				String[] split = fileName.split("\\.");
				String suffixName = "." + split[split.length - 1];
				if (suffixName.length() > 16)
					continue;
				Boolean needConcat = concatWithSuffixNameMatcher.get(suffixName);
				if (needConcat == null) {
					needConcat = DialogUtil.confirm("发现后缀名为【" + suffixName + "】的文件，是否合并", "【注意】", MsgType.INFORMATION);
					if (DialogUtil.confirm("是否为以后的文件执行相同操作", "【注意】", MsgType.INFORMATION)) {
						concatWithSuffixNameMatcher.put(suffixName, needConcat);
					}
				}
				if (!needConcat)
					continue;
			}
			if (f.getLen() == 0) {
				dao.delete(f.getPath().toString());
				continue;
			}
			ret.add(f);
		}

		ret.sort(Comparator.comparingLong(FileStatus::getLen));
		return ret;
	}

	/**
	 * 将符合条件的文件进行分组，以满足2GB这个限制条件
	 */
	private List<Tuple2<String, String[]>> groupFile(List<FileStatus> files) {
		List<Tuple2<String, String[]>> ret = new ArrayList<>();
		List<FileStatus> list = new ArrayList<>();
		long cntFileSize = 0L;
		int i = 0;
		long twoGb = 1L << 31; // 2GB
		while (i < files.size()) {
			FileStatus f = files.get(i);
			// 如果超过了 2 GB
			if ((cntFileSize + f.getLen()) > twoGb) {
				if (list.size() >= 2) {
					ret.add(transformList2Tuple(list));
				}
				list.clear();
				cntFileSize = 0L;
			}
			cntFileSize += f.getLen();
			list.add(f);
			i++;
		}
		if (list.size() >= 2) {
			ret.add(transformList2Tuple(list));
		}
		return ret;
	}

	/**
	 * 将列表的第一个元素放到元组一的位置，将其余元素放到元组二的位置
	 */
	private Tuple2<String, String[]> transformList2Tuple(List<FileStatus> list) {
		String[] otherFilePaths = new String[list.size() - 1];
		for (int j = 0, len = otherFilePaths.length; j < len; j++) {
			otherFilePaths[j] = list.get(j + 1).getPath().toString();
		}
		return new Tuple2<>(list.get(0).getPath().toString(), otherFilePaths);
	}

	public void setSelectedFileStatuses(FileStatus[] selectedRowsData) {
		appDao.setSelectedFileStatuses(selectedRowsData);
		notifyObservers(UpdateLevel.UI_ONLY);
	}

	public void diskUsage() {
		notifyObservers(UpdateLevel.LOADING);

		EventLoop.submit(() -> {
			List<FileStatus> filesInCurrentPath = appDao.getFilesInCurrentPath();
			List<FileStatus> collect = filesInCurrentPath.stream()
					.map(this::fetchDirSize)
					.collect(Collectors.toList());
			appDao.setCurrentPathAndFiles(appDao.getCurrentPath(), collect);
			notifyObservers(UpdateLevel.UI_AND_DATA);
			notifyObservers(UpdateLevel.LOADED);
		});
	}

	private FileStatus fetchDirSize(FileStatus f) {
		if (f.isFile()) {
			return f;
		}
		long len = hdfsDao.getFileOrDirSize(f.getPath());
		boolean dir = f.isDirectory();
		short rep = f.getReplication();
		long bSize = f.getBlockSize();
		long mtime = f.getModificationTime();
		long aTime = f.getAccessTime();
		FsPermission pm = f.getPermission();
		String o = f.getOwner();
		String g = f.getGroup();
		Path p = f.getPath();
		if (f.isSymlink()) {
			try {
				Path l = f.getSymlink();
				return new FileStatus(len, dir, rep, bSize, mtime, aTime, pm, o, g, l, p);
			} catch (IOException e) {
				throw new UnexpectedException(e);
			}
		} else {
			return new FileStatus(len, dir, rep, bSize, mtime, aTime, pm, o, g, p);
		}
	}

	public void chPermissionAndOwner(FileStatus f, FsPermission pm, String owner, String group) {
		Path path = f.getPath();
		FsPermission originPm = f.getPermission();
		String originOwner = f.getOwner();
		String originGroup = f.getGroup();
		try {
			if (!originPm.equals(pm))
				hdfsDao.changePermission(path, pm);
			if (!originOwner.equals(owner) || !originGroup.equals(group))
				hdfsDao.changeOwner(path, owner, group);
			log.info("更改" + path + "的权限为:" + pm + "\t" + owner + ":" + group);
		} catch (IOException e) {
			GuiUtil.displayTray("出现异常", e.getMessage(), TrayIcon.MessageType.ERROR);
		}
	}

	public void setPasteStatus(AppStatusDao.PasteStatus status) {
		appDao.setPasteStatus(status);
	}

	public void pasteFiles(String[] paths, Path target) {
		AppStatusDao.PasteStatus pasteStatus = appDao.getPasteStatus();
		String tgt = target.toString();
		switch (pasteStatus) {
		case CUT:
			for (String path : paths) {
				try {
					hdfsDao.movefile(path, tgt);
					log.info("成功将【" + path + "】挪到【" + tgt + "】");
				} catch (IOException e) {
					GuiUtil.displayTray("异常", "出现异常:" + e.getMessage(), TrayIcon.MessageType.ERROR);
					log.error(e.getMessage(), e);
				}
			}
			SysUtil.setClipboard("");
			updateFileStatusFrom(getCurrentPath(), false);
			break;
		case COPY:
			for (String path : paths) {
				try {
					hdfsDao.copyFile(path, tgt);
					log.info("成功将【" + path + "】复制到【" + tgt + "】");
				} catch (IOException e) {
					GuiUtil.displayTray("异常", "出现异常:" + e.getMessage(), TrayIcon.MessageType.ERROR);
					log.error(e.getMessage(), e);
				}
			}
			break;
		default:
		}
	}

	public enum UpdateLevel {
		UI_ONLY, UI_AND_DATA, LOADING, LOADED
	}
}
