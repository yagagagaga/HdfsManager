package hdfsmanager.api.controller;

import java.awt.*;
import java.util.Observable;
import java.util.Observer;

import hdfsmanager.util.GuiUtil;

public abstract class ViewController implements Observer {

	protected void init() {
		GuiUtil.initLookAndFeel();
		initModel();
		initOtherViewControllers();
		initView();
		initUI();
		initLookAndFeel();
		initEventListener();
		initActionListener();
	}

	protected void initModel() {
		/* 子类有需要时，重载此方法 */}

	/**
	 * 用来初始化子类的界面控件
	 */
	protected void initView() {
		/* 子类有需要时，重载此方法 */}

	/**
	 * 用来初始化需要依赖到的其他视图控制器
	 */
	protected void initOtherViewControllers() {
		/* 子类有需要时，重载此方法 */}

	/**
	 * 用来构建程序的界面布局
	 */
	protected abstract void initUI();

	/**
	 * 用来美化程序的界面样式
	 */
	protected void initLookAndFeel() {
		/* 子类有需要时，重载此方法 */}

	/**
	 * 用来初始化界面控件的事件监听
	 */
	protected void initEventListener() {
		/* 子类有需要时，重载此方法 */}

	/**
	 * 用来初始化界面控件的动作监听
	 */
	protected void initActionListener() {
		/* 子类有需要时，重载此方法 */}

	@Override
	public void update(Observable o, Object arg) {
		/* 子类有需要时，重载此方法 */}

	public abstract Component getMainView();
}
