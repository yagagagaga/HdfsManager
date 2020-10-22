package hdfsmanager;

import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import hdfsmanager.support.io.base.BaseTask;

public class TasksModel extends Observable {

	private final Map<Integer, BaseTask> dataMap = new HashMap<>();
	private int minimum;
	private int maximum;
	private int value;
	private boolean triggerSelectAll;
	private boolean triggerInverseSel;
	private int[] selectedTaskIds = new int[0];

	public void remove(int taskKey) {
		dataMap.remove(taskKey);
	}

	public BaseTask get(int taskKey) {
		return dataMap.get(taskKey);
	}

	public int size() {
		return dataMap.size();
	}

	public Set<Integer> keySet() {
		return dataMap.keySet();
	}

	public void put(BaseTask task) {
		int key = task.hashCode();
		dataMap.put(key, task);
	}

	@Override
	public void notifyObservers() {
		setChanged();
		super.notifyObservers();
	}

	public int getMinimum() {
		return this.minimum;
	}

	public int getMaximum() {
		return this.maximum;
	}

	public int getValue() {
		return this.value;
	}

	public boolean isTriggerSelectAll() {
		return this.triggerSelectAll;
	}

	public boolean isTriggerInverseSel() {
		return this.triggerInverseSel;
	}

	public int[] getSelectedTaskIds() {
		return this.selectedTaskIds;
	}

	public void setMinimum(int minimum) {
		this.minimum = minimum;
	}

	public void setMaximum(int maximum) {
		this.maximum = maximum;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public void setTriggerSelectAll(boolean triggerSelectAll) {
		this.triggerSelectAll = triggerSelectAll;
	}

	public void setTriggerInverseSel(boolean triggerInverseSel) {
		this.triggerInverseSel = triggerInverseSel;
	}

	public void setSelectedTaskIds(int[] selectedTaskIds) {
		this.selectedTaskIds = selectedTaskIds;
	}
}