package com.heaven7.java.logic;

import com.heaven7.java.logic.LogicAction.LogicCallback;

/**
 * the logic task
 * @author heaven7
 *
 */
public class LogicTask {
	private final int tag;
	private final LogicAction action;
	private final LogicParam logicParam;
	
	LogicTask(int tag, LogicAction action, LogicParam logicParam) {
		this.tag = tag;
		this.action = action;
		this.logicParam = logicParam;
	}
	
	public static LogicTask from(int tag, LogicAction action, LogicParam logicParam){
		return new LogicTask(tag, action, logicParam);
	}
	
	public LogicTask schedulerOn(Scheduler scheduler){
		action.scheduleOn(tag, scheduler);
		return this;
	}
	public LogicTask observeOn(Scheduler scheduler){
		action.observeOn(tag, scheduler);
		return this;
	}
	public LogicTask schedulerDelay(long delay){
		action.scheduleDelay(tag, delay);
		return this;
	}
	
	void addStateCallback(LogicCallback callback) {
		action.addStateCallback(tag, callback);
	}
	
	void removeStateCallback(LogicCallback callback) {
		action.removeStateCallback(tag, callback);
	}
	
	void perform() {
		action.perform(tag, logicParam);
	}

	void cancel(boolean immediately) {
		action.cancel(tag, immediately);
	}
	
	void setContextData(Object data) {
		if( action.getContextData() == null){
		    action.setContextData(data);
		}
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		LogicTask logicTask = (LogicTask) o;

		if (tag != logicTask.tag)
			return false;
		if (action != null ? !action.equals(logicTask.action) : logicTask.action != null)
			return false;
		return logicParam != null ? logicParam.equals(logicTask.logicParam) : logicTask.logicParam == null;
	}

	@Override
	public int hashCode() {
		int result = tag;
		result = 31 * result + (action != null ? action.hashCode() : 0);
		result = 31 * result + (logicParam != null ? logicParam.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "LogicTask{" + "tag=" + tag + ", action=" + action + ", logicParam=" + logicParam + '}';
	}

}