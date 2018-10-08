package com.heaven7.java.logic;

import java.lang.ref.WeakReference;

import com.heaven7.java.base.anno.CalledInternal;
import com.heaven7.java.base.anno.NonNull;
import com.heaven7.java.base.anno.Nullable;
import com.heaven7.java.base.util.Throwables;
import com.heaven7.java.logic.LogicAction.LogicCallback;

/**
 * the logic task
 * @author heaven7
 * @see #of(int, LogicAction, LogicParam)
 * @see #ofSimple(SimpleLogicAction, LogicParam)
 * @see #delay(long)
 * @see #schedulerOn(Scheduler)
 * @see #observeOn(Scheduler)
 */
public class LogicTask {
	private final int tag;
	private final LogicAction action;
	private final LogicParam logicParam;
	private WeakReference<LogicAction.LogicCallback> mInternalCallback;
	/** some perform flags */
	private int mFlags;

	LogicTask(LogicAction action, LogicParam logicParam){
		this(0, action, logicParam);
	}
	private LogicTask(int tag, @NonNull LogicAction action, @Nullable LogicParam logicParam) {
		Throwables.checkNull(action);
		this.tag = tag;
		this.action = action;
		this.logicParam = logicParam != null ? logicParam : new LogicParam();
	}
	/**
	 * create logic task by target parameters.
	 * @param tag the logic tag
	 * @param action the logic action. can't be null.
	 * @param logicParam the logic parameter. can be null.
	 * @return an instance of logic task
	 */
	public static LogicTask of(int tag,  @NonNull  LogicAction action, @Nullable LogicParam logicParam){
		return new LogicTask(tag, action, logicParam);
	}
	/**
	 * create simple logic task by target parameters.
	 * @param action the simple logic action. can't be null.
	 * @param logicParam the logic parameter. can be null.
	 * @return an instance of logic task
	 */
	public static LogicTask ofSimple( @NonNull SimpleLogicAction action, @Nullable LogicParam logicParam){
		return new LogicTask(0, action, logicParam);
	}
	/**
	 * set the flags.
	 * @param flags the flags. flags <= 0 have nothing effect.
	 * @return this.
	 * @see LogicManager#FLAG_SHARE_TO_NEXT
	 * @see LogicManager#FLAG_SHARE_TO_POOL
	 */
	public LogicTask setFlags(int flags){
		if(flags > 0){
		    mFlags = flags;
		}
		return this;
	}
	
	/**
	 * make the logic task schedule/perform on the target scheduler.
	 * @param scheduler the target scheduler.
	 * @return this.
	 */
	public LogicTask schedulerOn(Scheduler scheduler){
		action.scheduleOn(tag, scheduler);
		return this;
	}
	/**
	 * make the logic task observe/callback on the target scheduler.
	 * @param scheduler the target scheduler.
	 * @return this.
	 */
	public LogicTask observeOn(Scheduler scheduler){
		action.observeOn(tag, scheduler);
		return this;
	}
	/**
	 * make the  logic task perform delay on the assigned scheduler.
	 * @param delay the delay in milliseconds
	 * @return this
	 * @see #schedulerOn(Scheduler)
	 */
	public LogicTask delay(long delay){
		action.setDelay(tag, delay);
		return this;
	}

	/**
	 * reset the logic task. this often called after cancel for next run.
	 * @since 1.0.1
	 */
	public void reset(){
		action.reset(tag);
	}
	
	//======================== private method ==================================
	
	@CalledInternal
	void addStateCallback(LogicCallback callback) {
		mInternalCallback = new WeakReference<LogicAction.LogicCallback>(callback);
		action.addStateCallback(tag, callback);
	}
	@CalledInternal
	void removeStateCallback(LogicCallback callback) {
		action.removeStateCallback(tag, callback);
	}
	@CalledInternal
	void perform(LogicManager lm) {
		action.perform(lm, tag, logicParam, mFlags);
	}
	@CalledInternal
	void cancel() {
		if(mInternalCallback != null){
            LogicAction.LogicCallback callback = mInternalCallback.get();
            if(callback != null) {
                action.removeStateCallback(tag, callback);
            }
        }
		action.cancel(tag);
	}
	@CalledInternal
	void setContextData(Object data) {
		//if self has context data. has nothing effect.
		if( action.getContextData() == null){
		    action.setContextData(data);
		}
	}
	@CalledInternal
	void resetLogicAction() {
		action.reset(tag);
	}
	@CalledInternal
	void setLastResult(Object resultData) {
		logicParam.setLastResult(resultData);
	}
	//flags the context flags.
	@CalledInternal
	void mergeFlags(int flags, boolean sequence){
		if(flags > 0){
			mFlags |= flags;
		}
		//remove share to next flag for parallel.
		if(!sequence){
			mFlags &= ~LogicManager.FLAG_SHARE_TO_NEXT;
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
		return "LogicTask [tag=" + tag + ", action=" + action + ", logicParam=" + logicParam + ", mFlags=" + mFlags
				+ "]";
	}
    
}