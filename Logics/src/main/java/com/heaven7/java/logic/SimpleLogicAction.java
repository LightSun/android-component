package com.heaven7.java.logic;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

/**
 * a simple implements of logic action. you should just ignore the logic tag .
 * @author heaven7
 */
public abstract class SimpleLogicAction extends BaseLogicAction{
	
	private final AtomicReference<ScheduleHandler> mAR_Scheduler;
	private final AtomicReference<TagInfo> mAR_tagInfo;
	private final CopyOnWriteArrayList<LogicCallback> mCallbacks;
	
	public SimpleLogicAction(){
		mAR_Scheduler = new AtomicReference<AbstractLogicAction.ScheduleHandler>(new ScheduleHandler());
		mAR_tagInfo = new AtomicReference<TagInfo>();
		mCallbacks = new CopyOnWriteArrayList<LogicCallback>();
	}
	
	@Override
	public final void addStateCallback(int tag, LogicCallback callback) {
		mCallbacks.add(callback);
	}

	@Override
	public final void removeStateCallback(int tag, LogicCallback callback) {
		mCallbacks.remove(callback);
	}

	@Override
	public final boolean isRunning() {
		return isRunning(0);
	}

	@Override
	public void reset() {
		reset(0);
	}
	
	protected final void dispatchCallbackInternal(int op, int resultCode, int tag, LogicParam lm) {
		final CallbackRunner runner = new CallbackRunner(op, resultCode, tag, lm);
		runner.s = getScheduleHandler(tag, false);
		for (LogicCallback cl : mCallbacks) {
			runner.scheduleCallback(this, cl);
		}
	}
	
	@Override
	protected ScheduleHandler getScheduleHandler(int tag, boolean cacheIfNeed) {
		return mAR_Scheduler.get();
	}
	
	@Override
	protected final TagInfo getTagInfo(int tag, boolean remove) {
		return remove ? mAR_tagInfo.getAndSet(null) : mAR_tagInfo.get();
	}
	@Override
	protected void putTagInfo(int tag, TagInfo info) {
		mAR_tagInfo.getAndSet(info);
	}
	

}
