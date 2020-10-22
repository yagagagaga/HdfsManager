package hdfsmanager.api;

import hdfsmanager.exception.UnexpectedException;

import java.util.Observable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 此类负责实现业务逻辑
 */
public abstract class Model extends Observable {

	/**
	 * 此方法会在 controller 初始化完视图之后调用
	 */
	public abstract void initialize();

	@Override
	public void notifyObservers() {
		try {
			setChanged();
			super.notifyObservers();
		} catch (Exception e) {
			throw new UnexpectedException(e);
		}
	}

	@Override
	public void notifyObservers(Object arg) {
		try {
			setChanged();
			super.notifyObservers(arg);
		} catch (Exception e) {
			throw new UnexpectedException(e);
		}
	}

	public static class VoidModel extends Model {

		@Override
		public void initialize() {
			// do nothing
		}
	}
}
