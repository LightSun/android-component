package com.heaven7.java.logic;

import com.heaven7.java.base.anno.Nullable;
import com.heaven7.java.base.util.SparseArray;
import com.heaven7.java.base.util.Throwables;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * the logic action. support async and count analyse. default support multi tag
 * in one {@linkplain AbstractLogicAction}. you should call
 * {@link #dispatchResult(int, int)} in
 * {@linkplain #performImpl(int, int, LogicParam)} or it's relative method.
 * Created by heaven7 on 2017/6/17.
 */
public abstract class AbstractLogicAction extends ContextDataImpl implements LogicAction {

	private static final int OP_RESULT = 1;
	private static final int OP_START = 2;

	private final SparseArray<ArrayList<LogicCallback>> mCallbacks;

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
	private final SparseArray<Schedulers> mSchedulerMap;

	/**
	 * create an instance of AbstractLogicAction.
	 * 
	 * @param wantCount
	 *            true if you want to COUNT the count of perform assigned tag.
	 * @see #perform(int, LogicParam)
	 */
	public AbstractLogicAction(boolean wantCount) {
		this.mCallbacks = new SparseArray<ArrayList<LogicCallback>>(4);
		this.mTagMap = new SparseArray<TagInfo>(4);
		this.mCountMap = wantCount ? new SparseArray<Integer>(4) : null;
		this.mSchedulerMap = new SparseArray<Schedulers>();
	}

	@Override
	public final void addStateCallback(int tag, LogicCallback callback) {
		Throwables.checkNull(callback);
		synchronized (mCallbacks) {
			ArrayList<LogicCallback> list = mCallbacks.get(tag);
			if (list == null) {
				list = new ArrayList<>();
				mCallbacks.put(tag, list);
			}
			list.add(callback);
		}
	}

	@Override
	public final void removeStateCallback(int tag, LogicCallback callback) {
		Throwables.checkNull(callback);
		synchronized (mCallbacks) {
			ArrayList<LogicCallback> list = mCallbacks.get(tag);
			if (list != null) {
				list.remove(callback);
			}
		}
	}

	@Override
	public LogicParam getLogicParameter(int tag) {
		TagInfo info;
		synchronized (mTagMap) {
			info = mTagMap.get(tag);
		}
		return info != null ? info.mLogicParam : null;
	}

	@Override
	public void scheduleDelay(int tag, long delay) {
		if (delay < 0) {
			delay = 0;
		}
		synchronized (mSchedulerMap) {
			Schedulers s = new Schedulers();
			s.delay = delay;
			mSchedulerMap.append(tag, s);
		}
	}

	@Override
	public void scheduleOn(int tag, @Nullable Scheduler scheduler) {
		if (scheduler == null) {
			synchronized (mSchedulerMap) {
				Schedulers s = mSchedulerMap.get(tag);
				if (s != null) {
					s.schedulerOn = null;
				}
			}
		} else {
			synchronized (mSchedulerMap) {
				Schedulers s = new Schedulers();
				s.schedulerOn = scheduler;
				mSchedulerMap.append(tag, s);
			}
		}
	}

	@Override
	public void observeOn(int tag, Scheduler scheduler) {
		if (scheduler == null) {
			synchronized (mSchedulerMap) {
				Schedulers s = mSchedulerMap.get(tag);
				if (s != null) {
					s.observeOn = null;
				}
			}
		} else {
			synchronized (mSchedulerMap) {
				Schedulers s = new Schedulers();
				s.observeOn = scheduler;
				mSchedulerMap.append(tag, s);
			}
		}
	}

	@Override
	public boolean isRunning(int tag) {
		TagInfo info = mTagMap.get(tag);
		if (info != null && !info.mCancelled.get()) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isRunning() {
		final int size = mTagMap.size();
		for (int i = 0; i < size; i++) {
			TagInfo info = mTagMap.valueAt(i);
			if (!info.mCancelled.get()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void perform(final int tag, final LogicParam param) {
		// put tag info
		synchronized (mTagMap) {
			mTagMap.put(tag, new TagInfo(param));
		}
		Schedulers s;
		synchronized (mSchedulerMap) {
			s = mSchedulerMap.get(tag);
			if (s == null) {
				s = new Schedulers();
				mSchedulerMap.put(tag, s);
			}
		}
		// start
		dispatchCallbackInternal(OP_START, 0, tag, param);

		// COUNT tag count if need
		final int targetCount;
		if (mCountMap != null) {
			synchronized (mCountMap) {
				targetCount = mCountMap.get(tag) + 1;
				mCountMap.put(tag, targetCount);
			}
		} else {
			targetCount = 1;
		}

		// perform impl
		s.schedule(new Runnable() {
			@Override
			public void run() {
				performImpl(tag, targetCount, param);
			}
		});
	}

	@Override
	public final boolean dispatchResult(int resultCode, int tag) {
		// usually callback
		final LogicParam lm = getLogicParameter(tag);

		dispatchCallbackInternal(OP_RESULT, resultCode, tag, lm);
		
		boolean result;
		switch (resultCode) {
			case RESULT_SUCCESS:
				return onLogicSuccess(tag);

		    case RESULT_FAILED:
			    return onLogicFailed(tag);
			
			default:
				result = dispatchLogicResult(resultCode, tag, lm);
		}
		synchronized (mSchedulerMap) {
			mSchedulerMap.remove(tag);
		}
		return result;
	}

	@Override
	public final void cancel(int tag, boolean immediately) {
		TagInfo info;
		synchronized (mTagMap) {
			info = mTagMap.get(tag);
		}
		if (info == null) {
			return;
		}
		if (!info.mCancelled.compareAndSet(false, true)) {
			System.err.println("AbstractLogicAction >>>>>> called [ cancel() ]: " + "cancel failed. tag = " + tag
					+ " ,param = " + info.mLogicParam);
			return;
		}
		synchronized (mSchedulerMap) {
			Schedulers s = mSchedulerMap.get(tag);
			if(s != null){
				s.cancel();
			}
		}
		synchronized (mCallbacks) {
			ArrayList<LogicCallback> callbacks = mCallbacks.get(tag);
			if (callbacks != null) {
				callbacks.clear();
			}
		}
		cancelImpl(tag, immediately);
	}

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

	/**
	 * called on cancel last perform. often called by
	 * {@linkplain #onLogicSuccess(int)}}.
	 * 
	 * @param tag
	 *            the tag .
	 * @param param
	 *            the logic parameter.
	 */
	protected void onCancel(int tag, LogicParam param) {

	}

	/**
	 * called on logic result.
	 * 
	 * @param resultCode
	 *            the result code. but not {@linkplain #RESULT_SUCCESS} or
	 *            {@linkplain #RESULT_FAILED}.
	 * @param tag
	 *            the tag
	 * @param lm
	 *            the logic parameter
	 * @return true if dispatch success.
	 */

	protected boolean dispatchLogicResult(int resultCode, int tag, LogicParam lm) {
		return false;
	}

	/**
	 * do perform this logic state. also support async perform this logic state.
	 *
	 * @param tag
	 *            the tag of this logic state.
	 * @param count
	 *            the count of perform this tag by logic. but if it was cleaned
	 *            it always be one. start from 1
	 * @param param
	 *            the logic parameter of this logic state
	 */
	protected abstract void performImpl(int tag, int count, LogicParam param);

	/**
	 * do cancel this perform logic.
	 * 
	 * @param tag
	 *            the tag
	 * @param immediately
	 *            true if cancel immediately.
	 */
	protected abstract void cancelImpl(int tag, boolean immediately);

	// ====================== self method ============================

	protected boolean onLogicFailed(int tag) {
		getAndRemoveTagInfo(tag);
		return false;
	}

	/**
	 * called on logic success.
	 * 
	 * @param tag
	 *            the tag
	 * @return true .if it is started ,but not cancelled and normal success.
	 */
	protected boolean onLogicSuccess(int tag) {
		// get and remove tag info
		TagInfo info = getAndRemoveTagInfo(tag);
		if (info == null) {
			return false;
		}

		// true, means it is cancelled.
		if (info.mCancelled.get()) {
			onCancel(tag, info.mLogicParam);
			return false;
		}
		return true;
	}

	private void dispatchCallbackInternal(int op, int resultCode, int tag, LogicParam lm) {
		Schedulers s;
		synchronized (mSchedulerMap) {
			s = mSchedulerMap.get(tag);
		}
		if (s == null) {
			// must be cancelled.
			System.err.println("logic action is cancelled. tag = " + tag);
			return;
		}
		ArrayList<LogicCallback> callbacks;
		synchronized (mCallbacks) {
			callbacks = mCallbacks.get(tag);
		}
		if (callbacks != null) {
			final CallbackRunner runner = new CallbackRunner(s, op, resultCode, tag, lm);
			for (LogicCallback cl : callbacks) {
				runner.scheduleCallback(this, cl);
			}
		}
	}

	private TagInfo getAndRemoveTagInfo(int tag) {
		TagInfo info;
		synchronized (mTagMap) {
			info = mTagMap.get(tag);
			mTagMap.remove(tag);
		}
		return info;
	}

	private static class TagInfo {
		AtomicBoolean mCancelled;
		LogicParam mLogicParam;

		public TagInfo(LogicParam mLogicParam) {
			this.mCancelled = new AtomicBoolean(false);
			this.mLogicParam = mLogicParam;
		}
	}

	private static class Schedulers {
		long delay;
		Scheduler schedulerOn;
		Scheduler observeOn;
		WeakReference<Runnable> mWeakScheduleTask;

		void schedule(Runnable task) {
			if (schedulerOn != null) {
				mWeakScheduleTask = new WeakReference<Runnable>(task);
				if (delay <= 0) {
					schedulerOn.post(task);
				} else {
					schedulerOn.postDelay(delay, task);
				}
			} else {
				if (delay > 0) {
					throw new IllegalStateException("#schedulerOn() must be called before.");
				}
				task.run();
			}
		}

		void scheduleCallback(Runnable task) {
			if (observeOn == null) {
				task.run();
			} else {
				observeOn.post(task);
			}
		}
		
		void cancel(){
			if(mWeakScheduleTask != null){
				Runnable task = mWeakScheduleTask.get();
				if(task != null){
					schedulerOn.remove(task);
				}
			}
		}
	}

	private static class CallbackRunner {
		private final int op;
		private final int resultCode;
		private final int tag;
		private final LogicParam lp;
		private final Schedulers s;

		public CallbackRunner(Schedulers s, int op, int resultCode, int tag, LogicParam lp) {
			super();
			this.op = op;
			this.resultCode = resultCode;
			this.tag = tag;
			this.lp = lp;
			this.s = s;
		}

		public void scheduleCallback(final LogicAction action, LogicCallback callback) {
			s.scheduleCallback(new Runnable() {
				@Override
				public void run() {
					switch (op) {
					case OP_RESULT:
						callback.onLogicResult(action, resultCode, tag, lp);
						break;

					case OP_START:
						callback.onLogicStart(action, tag, lp);
						break;

					default:
						throw new RuntimeException();
					}
				}
			});
		}

	}
}
