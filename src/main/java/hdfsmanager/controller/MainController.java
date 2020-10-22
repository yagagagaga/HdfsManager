package hdfsmanager.controller;

import static hdfsmanager.util.Topics.TASKS_TOPIC;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.swing.*;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.fs.permission.FsPermission;
import org.apache.log4j.Logger;

import hdfsmanager.api.Controller;
import hdfsmanager.api.View;
import hdfsmanager.dao.AppStatusDao;
import hdfsmanager.dao.impl.AppStatusDaoImpl;
import hdfsmanager.dao.impl.HdfsDaoImpl;
import hdfsmanager.model.BatchRenameModel;
import hdfsmanager.model.HdfsModel;
import hdfsmanager.view.MainView;
import hdfsmanager.support.io.DownloadFileTask;
import hdfsmanager.support.io.base.BaseTask;
import hdfsmanager.util.*;

public class MainController<V extends View<?, ?>> extends Controller<HdfsModel, V> {

	private static final MessageBus.Producer<BaseTask> tasksProducer = MessageBus.producer(TASKS_TOPIC);
	private static final Logger log = Logger.getLogger(MainController.class);

	protected MainController(HdfsModel model, Class<V> v, Controller<?, ?>... otherControllers) {
		super(model, v, otherControllers);
		refreshPath(new Path("/"));
	}

	public void gotoPath(Path path) {
		model.updateFileStatusFrom(path);
	}

	public void refreshPath(Path p) {
		model.updateFileStatusFrom(p, false);
	}

	public void gotoPreviousPath() {
		model.gotoPreviousPath();
	}

	public void gotoNextPath() {
		model.gotoNextPath();
	}

	public static void startWith(String loginUrl, String loginUser) {
		HdfsModel hdfsModel = new HdfsModel(
				new HdfsDaoImpl(loginUrl, loginUser),
				AppStatusDaoImpl.getOrCreate());

		MainBottomController mainBottomController = new MainBottomController();

		new MainController<>(
				hdfsModel,
				MainView.class,
				mainBottomController,
				new HdfsTreeController(hdfsModel),
				new HdfsTableController(hdfsModel));
	}

	public void download() {
		FileStatus[] selectedFileStatuses = model.getSelectedFileStatuses();
		if (selectedFileStatuses.length == 0)
			return;
		download(selectedFileStatuses);
	}

	public void download(FileStatus[] fileStatuses) {
		File[] targetPaths = DialogUtil.chooseFiles(JFileChooser.DIRECTORIES_ONLY, "请选择保存路径");
		if (targetPaths.length != 1)
			return;
		for (FileStatus f : fileStatuses) {
			BaseTask task = model.downloadFromHdfs(f, targetPaths[0]);
			tasksProducer.offer(task);
		}
	}

	public void download5M(FileStatus[] fileStatuses) {
		File[] targetPaths = DialogUtil.chooseFiles(JFileChooser.DIRECTORIES_ONLY, "请选择保存路径");
		if (targetPaths.length != 1)
			return;
		for (FileStatus f : fileStatuses) {
			DownloadFileTask task = model.download5MFileFromHdfs(f.getPath(), targetPaths[0]);
			tasksProducer.offer(task);
			log.info("成功创建下载5M文件【" + f.getPath() + "】到【" + targetPaths[0] + "】的任务");
		}
	}

	public void upload() {
		File[] files = DialogUtil.chooseFiles(JFileChooser.FILES_AND_DIRECTORIES, "请选择要上传的文件(夹)");
		Path hdfsDstPath = model.getCurrentPath();
		for (File f : files) {
			BaseTask task = model.uploadToHdfs(f, hdfsDstPath);
			tasksProducer.offer(task);
			log.info("成功创建上传文件(夹)【" + f + "】到【" + hdfsDstPath + "】的任务");
		}
	}

	public void upload(FileStatus[] fileStatuses) {
		if (fileStatuses.length != 1) {
			DialogUtil.show("你不能同时把文件(夹)上传到多个目录上", "非法操作", MsgType.ERROR);
			return;
		}
		File[] files = DialogUtil.chooseFiles(JFileChooser.FILES_AND_DIRECTORIES, "请选择要上传的文件(夹)");
		Path hdfsDstPath = fileStatuses[0].getPath();
		// 如果是在表格界面上，则上传到当前的文件夹
		if (fileStatuses[0].isFile()) {
			hdfsDstPath = fileStatuses[0].getPath().getParent();
		}
		for (File f : files) {
			BaseTask task = model.uploadToHdfs(f, hdfsDstPath);
			tasksProducer.offer(task);
			log.info("成功创建上传文件(夹)【" + f + "】到【" + hdfsDstPath + "】的任务");
		}
	}

	public void newFolder() {
		Optional<String> folderNameOpt = DialogUtil.input("请输入新文件夹名称", "新建文件夹", MsgType.QUESTION);
		if (!folderNameOpt.isPresent()) {
			return;
		}
		String folderName = folderNameOpt.get();
		Path path = PathUtil.addPath(model.getCurrentPath(), folderName);
		model.newFolder(path);
		refreshPath(path.getParent());
	}

	public void newFolder(FileStatus[] fileStatuses) {
		Optional<String> folderNameOpt = DialogUtil.input("请输入新文件夹名称", "新建文件夹", MsgType.QUESTION);
		if (!folderNameOpt.isPresent()) {
			return;
		}
		String folderName = folderNameOpt.get();
		Path target = null;
		for (FileStatus f : fileStatuses) {
			target = PathUtil.addPath(f.getPath(), folderName);
			// 如果是在表格界面上，则在当前的文件夹创建新文件夹
			if (f.isFile()) {
				target = PathUtil.addPath(f.getPath().getParent(), folderName);
			}
			model.newFolder(target);
		}
		// 刷新
		if (target != null)
			refreshPath(target.getParent());
	}

	public void addSuccessFlag() {
		Path currentPath = model.getCurrentPath();
		model.addSuccessFlag(currentPath);
		refreshPath(currentPath);
	}

	public void addSuccessFlag(FileStatus[] fileStatuses) {
		Path target = null;
		for (FileStatus f : fileStatuses) {
			target = f.getPath();
			// 如果是在表格界面上，则在当前的文件夹创建新文件夹
			if (fileStatuses[0].isFile()) {
				target = f.getPath().getParent();
			}
			model.addSuccessFlag(target);
		}
		// 刷新
		if (target != null)
			refreshPath(target);
	}

	public void del() {
		FileStatus[] selectedFileStatuses = model.getSelectedFileStatuses();
		del(selectedFileStatuses);
	}

	public void del(FileStatus[] fileStatuses) {
		if (fileStatuses.length == 0)
			return;
		StringBuilder msg = new StringBuilder("你确定要删除以下文件吗？");
		for (FileStatus f : fileStatuses) {
			msg.append("\n-- ").append(f.getPath().getName());
		}
		boolean isYes = DialogUtil.confirm(msg.toString(), "删除文件", MsgType.ERROR);
		if (!isYes)
			return;

		Path targetRefreshPath = new Path("/");
		for (FileStatus f : fileStatuses) {
			targetRefreshPath = f.getPath().getParent();
			model.delete(f.getPath());
		}
		refreshPath(targetRefreshPath);
	}

	public void rename() {
		FileStatus[] fileStatuses = model.getSelectedFileStatuses();
		rename(fileStatuses);
	}

	public void rename(FileStatus[] fileStatuses) {
		List<String> errors = new ArrayList<>();
		int cntRename = 0;
		Path targetRefreshPath = new Path("/");
		for (FileStatus f : fileStatuses) {
			String fileName = f.getPath().getName();
			Optional<String> newNameOpt = DialogUtil.input("请输入【" + fileName + "】的新名称", "【重命名】", fileName, MsgType.QUESTION);
			if (newNameOpt.isPresent()) {
				boolean isSuccess = model.rename(f.getPath().toString(), f.getPath().getParent().toString() + "/" + newNameOpt.get());
				if (isSuccess) {
					cntRename++;
					targetRefreshPath = f.getPath().getParent();
				} else {
					errors.add(f.getPath().toString());
				}
			}
		}
		if (!errors.isEmpty()) {
			StringBuilder msg = new StringBuilder("以下文件重命名失败：");
			errors.forEach(s -> msg.append("\n\t").append(s));
			DialogUtil.show(msg.toString(), "有文件重命名失败！", MsgType.ERROR);
		}
		if (cntRename > 0) {
			refreshPath(targetRefreshPath);
		}
	}

	public void preview(FileStatus f) {
		if (f.isDirectory())
			return;
		byte[] data = model.read256KBOf(f);
		DialogUtil.preview(data);
	}

	public void copyPath() {
		FileStatus[] selectedFileStatuses = model.getSelectedFileStatuses();
		copyPath(selectedFileStatuses);
	}

	public void copyPath(FileStatus[] fileStatuses) {
		StringBuilder content = new StringBuilder();
		for (int i = 0; i < fileStatuses.length; i++) {
			content.append(fileStatuses[i].getPath().toString());
			if (i != fileStatuses.length - 1)
				content.append("\n");
		}
		SysUtil.setClipboard(content.toString());
	}

	public void copyTableInfo(FileStatus[] fileStatuses) {
		StringBuilder content = new StringBuilder("文件名称\t大小（Byte）\t用户权限\t最后修改日期\n");
		for (FileStatus f : fileStatuses) {
			content.append(f.getPath().getName())
					.append("\t")
					.append(f.getLen())
					.append("\t")
					.append(f.isDirectory() ? "目录" : "文件").append(" ").append(f.getPermission())
					.append("\t")
					.append(DateUtil.format(f.getModificationTime(), "yyyy-MM-dd HH:mm:ss"))
					.append("\n");
		}
		SysUtil.setClipboard(content.toString());
	}

	public void batchRename() {
		FileStatus[] selectedFileStatuses = model.getSelectedFileStatuses();
		batchRename(selectedFileStatuses);
	}

	public void batchRename(FileStatus[] statuses) {
		if (statuses.length == 0)
			return;
		new BatchRenameController(new BatchRenameModel(model.getDao(), statuses));
	}

	public void concat() {
		FileStatus[] selectedFileStatuses = model.getSelectedFileStatuses();
		concat(selectedFileStatuses);
	}

	public void concat(FileStatus[] fs) {
		if (fs.length == 0)
			return;
		model.concat(fs);
	}

	public void empower777(FileStatus[] fileStatuses) {
		FsPermission p = new FsPermission("777");
		for (FileStatus f : fileStatuses) {
			model.chPermissionAndOwner(f, p, f.getOwner(), f.getGroup());
		}
		refreshPath(fileStatuses[fileStatuses.length - 1].getPath().getParent());
	}

	public void setSelectedFiles(FileStatus[] selectedRowsData) {
		model.setSelectedFileStatuses(selectedRowsData);
	}

	public void diskUsage() {
		model.diskUsage();
	}

	public void chPermissionAndOwner() {
		FileStatus[] selectedFileStatuses = model.getSelectedFileStatuses();
		if (selectedFileStatuses.length == 0)
			return;

		new ChPermissionAndOwnerController(model);
	}

	public void copyFiles(FileStatus[] fileStatuses) {
		copyPath(fileStatuses);
		model.setPasteStatus(AppStatusDao.PasteStatus.COPY);
	}

	public void cutFiles() {
		FileStatus[] selectedFileStatuses = model.getSelectedFileStatuses();
		cutFiles(selectedFileStatuses);
	}

	public void cutFiles(FileStatus[] fileStatuses) {
		copyPath(fileStatuses);
		model.setPasteStatus(AppStatusDao.PasteStatus.CUT);
	}

	public void pasteFiles() {
		FileStatus[] fileStatuses = model.getSelectedFileStatuses();
		pasteFiles(fileStatuses);
	}

	public void pasteFiles(FileStatus[] fileStatuses) {
		if (fileStatuses.length != 1) {
			GuiUtil.displayTray("警告", "不能将文件粘贴到多个路径下", TrayIcon.MessageType.WARNING);
			return;
		}
		Optional<String> clipboardOpt = SysUtil.getClipboard();
		if (!clipboardOpt.isPresent()) {
			return;
		}
		String pathsStr = clipboardOpt.get();
		String[] paths = StringUtils.split(pathsStr, "\n");
		model.pasteFiles(paths, fileStatuses[0].getPath());
	}
}
