package hdfsmanager.api;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.*;

import hdfsmanager.exception.UnexpectedException;
import hdfsmanager.util.R;

public abstract class Controller<M extends Model, V extends View<?, ?>> {

	protected M model;
	protected V view;

	public Controller(M model, Class<V> viewClass, Controller<?, ?>... otherControllers) {
		this.model = model;

		try {
			Constructor<V> constructor = viewClass.getConstructor(this.getClass(), model.getClass());
			this.view = constructor.newInstance(this, model);
		} catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
			throw new UnexpectedException(e);
		}

		this.view.createView(fetchExternalView(otherControllers));
		this.view.createControls();
		this.model.initialize();
		this.view.setVisible(true);
	}

	public Controller(Class<M> modelClass, Class<V> viewClass, Controller<?, ?>... otherControllers) {
		this(R.of(modelClass), viewClass, otherControllers);
	}

	public JComponent getMainView() {
		return view.getMainView();
	}

	private static JComponent[] fetchExternalView(Controller<?, ?>... otherControllers) {
		JComponent[] views = new JComponent[otherControllers.length];
		for (int i = 0; i < otherControllers.length; i++) {
			views[i] = otherControllers[i].getMainView();
		}
		return views;
	}
}
