package com.heaven7.java.logic;

import com.heaven7.java.base.util.SparseArray;
import com.heaven7.java.base.util.Throwables;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * the logic action. support async and count analyse. default support multi tag
 * in one {@linkplain AbstractLogicAction}. you should call
 * {@link #dispatchResult(int, LogicResult)} in
 * {@linkplain #performImpl(int, int, LogicParam)} or it's relative method.
 * <p>this class is thread safe.</p>
 * Created by heaven7 on 2017/6/17.
 */
public abstract class AbstractLogicAction extends BaseLogicAction {

	private static final ScheduleHandler DEFAULT_SH = new ScheduleHandler();
	
	private final SparseArray<CopyOnWriteArrayList<LogicCallback>> mCallbacks;

	/**
	 * tag info map.
	 */
	private final SparseArray<TagInfo> mTagMap;

	/**
	 * the map which used to count the tag of state perform count.
	 */
	private final SparseArray<Integer> mCountMap;
	/**
	 * the scheduler map.
	 */
	private final SparseArray<ScheduleHandler> mSchedulerMap;

	/**
	 * create an instance of AbstractLogicAction.
	 * 
	 * @param wantCount
	 *            true if you want to COUNT the count of perform assigned tag.
	 * @see #perform(int, LogicParam, int)
	 */
	public AbstractLogicAction(boolean wantCount) {
		this.mCallbacks = new SparseArray<CopyOnWriteArrayList<LogicCallback>>(4);
		this.mTagMap = new SparseArray<TagInfo>(4);
		this.mCountMap = wantCount ? new SparseArray<Integer>(4) : null;
		this.mSchedulerMap = new SparseArray<ScheduleHandler>();
	}

	@Override
	public final void addStateCallback(int tag, LogicCallback callback) {
		Throwables.checkNull(callback);
		synchronized (mCallbacks) {
			CopyOnWriteArrayList<LogicCallback> list = mCallbacks.get(tag);
			if (list == null) {
				list = new CopyOnWriteArrayList<LogicCallback>();
				mCallbacks.put(tag, list);
			}
			list.add(callback);
		}
	}

	@Override
	public final void removeStateCallback(int tag, LogicCallback callback) {
		Throwables.checkNull(callback);
		synchronized (mCallbacks) {
			CopyOnWriteArrayList<LogicCallback> list = mCallbacks.get(tag);
			if (list != null) {
				list.remove(callback);
			}
		}
	}
	
	@Override
	protected final ScheduleHandler getScheduleHandler(int tag, boolean cacheIfNeed) {
		ScheduleHandler s;
		synchronized (mSchedulerMap) {
			s = mSchedulerMap.get(tag);
			if (s == null && cacheIfNeed) {
				s = new ScheduleHandler();
			    mSchedulerMap.append(tag, s);
			}
		}
		return s != null ? s : DEFAULT_SH;
	}
	@Override
	public final boolean isRunning() {
		synchronized (mTagMap) {
			final int size = mTagMap.size();
			for (int i = 0; i < size; i++) {
				TagInfo info = mTagMap.valueAt(i);
				int state = info.mState.get();
				if(state == STATE_STARTED || state == STATE_PERFORMING ){
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void reset() {
		synchronized (mTagMap) {
			mTagMap.clear();
		}		
	}
	
	@Override
	protected final int computeTagCount(int tag) {
		final int targetCount;
		if (mCountMap != null) {
			synchronized (mCountMap) {
				Integer val = mCountMap.get(tag);
				targetCount = val != null ? val + 1 : 1;
				mCountMap.put(tag, targetCount);
			}
		} else {
			targetCount = 1;
		}
		return targetCount;
	}

	protected final TagInfo getTagInfo(int tag, boolean remove) {
		TagInfo info;
		synchronized (mTagMap) {
			if(remove){
				info = mTagMap.getAndRemove(tag);
			}else{
				info = mTagMap.get(tag);
			}
		}
		return info;
	}
	
	@Override
	protected final void putTagInfo(int tag, TagInfo info) {
		synchronized (mTagMap) {
		    mTagMap.put(tag, info);
		}
	}

	protected void dispatchCallbackInternal(int op, int tag, LogicParam lm, LogicResult result) {
		CopyOnWriteArrayList<LogicCallback> callbacks;
		synchronized (mCallbacks) {
			callbacks = mCallbacks.get(tag);
		}
		if (callbacks != null) {
			final CallbackRunner runner = new CallbackRunner(op, tag, lm, result);
			runner.s = getScheduleHandler(tag, false);
			for (LogicCallback cl : callbacks) {
				runner.scheduleCallback(this, cl);
			}
		}
	}
	
	//========================== self method =====================
	
	/**
	 * clear the count map or analyse .
	 */
	public final void clearCount() {
		if (mCountMap != null) {
			synchronized (mCountMap) {
				mCountMap.clear();
			}
		}
	}

	/**
	 * remove the count which is assigned by target tag.
	 * 
	 * @param tag
	 *            the tag.
	 */
	public final void removeCount(int tag) {
		if (mCountMap != null) {
			synchronized (mCountMap) {
				mCountMap.delete(tag);
			}
		}
	}
	
}
