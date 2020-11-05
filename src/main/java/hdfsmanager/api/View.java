package hdfsmanager.api;

import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.event.*;
import java.util.Observable;
import java.util.Observer;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;

import hdfsmanager.util.GuiUtil;

public abstract class View<C extends Controller<?, ?>, M extends Model> extends DropTargetAdapter implements
		Observer,
		ActionListener,
		MouseListener,
		MouseWheelListener,
		MouseMotionListener,
		KeyListener,
		TreeExpansionListener,
		ListSelectionListener {

	protected final C controller;
	private final M model;

	public View(C controller, M model) {
		GuiUtil.initLookAndFeel();
		this.controller = controller;
		this.model = model;
		if (this.model != null)
			this.model.addObserver(this);
	}

	/**
	 * 创建视图
	 */
	protected abstract void createView(JComponent... externalView);

	/**
	 * 为视图上的各个组件绑定事件
	 */
	protected abstract void createControls();

	/**
	 * 设置视图是否可见
	 * 
	 * @param isVisible <tt>true</tt>为可见，<tt>false</tt>为不可见
	 */
	protected void setVisible(boolean isVisible) {
		getMainView().setVisible(true);
	}

	protected abstract JComponent getMainView();

	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable o, Object arg) {
		if (model.getClass().isAssignableFrom(o.getClass())) {
			if (arg == null)
				update((M) o);
			else
				update((M) o, arg);
		}
	}

	protected void update(M model) {
		/* 子类需要时实现 */ }

	protected void update(M model, Object arg) {
		/* 子类需要时实现 */ }

	@Override
	public void actionPerformed(ActionEvent e) {
		/* 子类需要时实现 */ }

	@Override
	public void mouseClicked(MouseEvent e) {
		/* 子类需要时实现 */ }

	@Override
	public void mousePressed(MouseEvent e) {
		/* 子类需要时实现 */ }

	@Override
	public void mouseReleased(MouseEvent e) {
		/* 子类需要时实现 */ }

	@Override
	public void mouseEntered(MouseEvent e) {
		/* 子类需要时实现 */ }

	@Override
	public void mouseExited(MouseEvent e) {
		/* 子类需要时实现 */ }

	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		/* 子类需要时实现 */ }

	@Override
	public void mouseDragged(MouseEvent e) {
		/* 子类需要时实现 */ }

	@Override
	public void mouseMoved(MouseEvent e) {
		/* 子类需要时实现 */ }

	@Override
	public void keyTyped(KeyEvent e) {
		/* 子类需要时实现 */ }

	@Override
	public void keyPressed(KeyEvent e) {
		/* 子类需要时实现 */ }

	@Override
	public void keyReleased(KeyEvent e) {
		/* 子类需要时实现 */ }

	@Override
	public void treeCollapsed(TreeExpansionEvent e) {
		/* 子类需要时实现 */ }

	@Override
	public void treeExpanded(TreeExpansionEvent e) {
		/* 子类需要时实现 */ }

	@Override
	public void valueChanged(ListSelectionEvent e) {
		/* 子类需要时实现 */ }

	@Override
	public void drop(DropTargetDropEvent dtde) {
		/* 子类需要时实现 */ }
}
