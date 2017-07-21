package com.heaven7.java.logic;

import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicInteger;

import com.heaven7.java.base.anno.Nullable;
import com.heaven7.java.base.util.DefaultPrinter;

/**
 * the base logic action. define common method.
 * @author heaven7
 *
 */
public abstract class BaseLogicAction extends ContextDataImpl implements LogicAction{
	
	protected static final int OP_RESULT = 1;
	protected static final int OP_START  = 2;
	
	protected final String TAG = getClass().getSimpleName() + "@hash("+ hashCode()+")";
	
    /**
     * convert integer state to string.	
     * @param state the state.
     * @return the string of state.
     */
	public static String stateToString(int state){
		switch (state) {
		case STATE_CANCELLED:
			
			return "STATE_CANCELLED";
		case STATE_STARTED:
			
			return "STATE_STARTED";
		case STATE_PERFORMING:
			
			return "STATE_PERFORMING";
		}
		return null;
	}
	
	@Override
	public void reset(int tag) {
		getTagInfo(tag, true);
	}
	
	@Override
	public void scheduleOn(int tag, @Nullable Scheduler scheduler) {
		getScheduleHandler(tag, true).setPerformScheduler(scheduler);
	}

	@Override
	public void observeOn(int tag, Scheduler scheduler) {
		getScheduleHandler(tag, true).setCallbackScheduler(scheduler);
	}

	@Override
	public void setDelay(int tag, long delay) {
		getScheduleHandler(tag, true).setDelay(delay);
	}
	
	@Override
	public final void perform(final int tag, final LogicParam param) {
		TagInfo info = getTagInfo(tag, false);
		if(info != null){
			switch (info.mState.get()) {
			case STATE_CANCELLED:
				DefaultPrinter.getDefault().warn(TAG, "perform", "The logic action of target tag(" + tag +") is cancelled."
						+ "you can call #reset(int) to reset it, and then you can perform again.");
				return;
			
			case STATE_PERFORMING:	
			case STATE_STARTED:
				DefaultPrinter.getDefault().warn(TAG, "perform", "The logic action of target tag(" + 
			             tag +") is running!");
				return;
			}
		}
		//mark start.
		putTagInfo(tag, new TagInfo(param));
		
		//put schedule handler
		ScheduleHandler s = getScheduleHandler(tag, true);
		// dispatch start
		dispatchCallbackInternal(OP_START, 0, tag, param);
		
		final int targetCount = computeTagCount(tag);
		
		// perform impl
		s.schedule(new Runnable() {
			@Override
			public void run() {
				perform0(tag, targetCount, param);
			}
		});
	}
	
	private void perform0(int tag, int count, LogicParam param){
		TagInfo info = getTagInfo(tag, false);
		if(info.mState.compareAndSet(STATE_STARTED, STATE_PERFORMING)){
			//success
			performImpl(tag, count, param);
		}else{
			if(info.mState.get() == STATE_CANCELLED){
				//cancelled.
				onCancel(RESULT_FAILED, tag, param);
			}else{
				//can't reach here
				throw new IllegalStateException("wrong state.");
			}
		}
	}
	

	@Override
	public final boolean dispatchResult(int resultCode, int tag) {
		final LogicParam lm = getLogicParameter(tag);

		// get tag info
		TagInfo info = getTagInfo(tag, false);
		if (info == null) {
			return false;
		}
		// true, means it is cancelled. wait reset called.
		if (info.mState.get() == STATE_CANCELLED) {
			onCancel(resultCode, tag, info.mLogicParam);
			return false;
		}
		// result indicate whether invoke callback or not.
		boolean result;
		switch (resultCode) {
		case RESULT_SUCCESS:
			result = onLogicSuccess(tag);
			break;

		case RESULT_FAILED:
			result = onLogicFailed(tag);
			break;

		default:
			result = onLogicResult(resultCode, tag, lm);
		}
		if (result) {
			// handle callbacks
			dispatchCallbackInternal(OP_RESULT, resultCode, tag, lm);
		}
		//clear tag info
		getTagInfo(tag, true);
		return result;
	}
	
	@Override
	public final void cancel(int tag) {
		// tag
		TagInfo info = getTagInfo(tag, false);
		if (info == null) {
			return;
		}
		int state = info.mState.getAndSet(STATE_CANCELLED);
		if(state == STATE_CANCELLED){
			//already cancelled.
			return;
		}
		DefaultPrinter.getDefault().debug(TAG, "cancel", "the tag(" + tag + ") is cancelled. previous state is " 
		      + stateToString(state));
		// cancel scheduler
		ScheduleHandler s = getScheduleHandler(tag, false);
		if (s != null) {
			s.cancel();
		}
		cancelImpl(tag);
	}
	
	@Override
	public LogicParam getLogicParameter(int tag) {
		final TagInfo info = getTagInfo(tag, false);
		return info != null ? info.mLogicParam : null;
	}
	
	@Override
	public final boolean isRunning(int tag) {
		TagInfo info = getTagInfo(tag, false);
		if (info != null ) {
			int state = info.mState.get();
			return state == STATE_STARTED || state == STATE_PERFORMING ;
		}
		return false;
	}
	
	@Override
	public final boolean isCancelled(int tag) {
		TagInfo info = getTagInfo(tag, false);
		if (info != null && info.mState.get() == STATE_CANCELLED) {
			return true;
		}
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
	 * compute the running count of target tag.
	 * @param tag the tag
	 * @return the running count for target tag, default value is one.
	 */
	protected int computeTagCount(int tag) {
		return 1;
	}
	/**
	 * get tag info for target tag.
	 * @param tag the tag 
	 * @param remove true to remove it from pool
	 * @return the tag info. may be null
	 */
	protected abstract TagInfo getTagInfo(int tag, boolean remove);
	
	/**
	 * put tag info 
	 * @param tag the tag 
	 * @param info the tag info
	 */
	protected abstract void putTagInfo(int tag, TagInfo info);
	
	/**
	 * get ScheduleHandler , if not exist create and cache it if need (assigned by parameter cacheIfNeed).
	 * @param tag the tag
	 * @param putIfNotExist if true, when the target ScheduleHandler not exist, create and cache it .false otherwise.
	 * @return the ScheduleHandler.never return null.
	 */
	protected abstract ScheduleHandler getScheduleHandler(int tag, boolean putIfNotExist);
	
	/**
	 * dispatch callback internal.
	 * @param op the option.
	 * @param resultCode the result code
	 * @param tag the tag
	 * @param lm the logic parameter
	 */
	protected abstract void dispatchCallbackInternal(int op, int resultCode, int tag, LogicParam lm);

	// ================ optional override method =========
	
	/**
	 * do cancel this performed logic. because sometimes we need to cancel other
	 * operation.
	 * 
	 * @param tag
	 *            the tag
	 * @param immediately
	 *            true if cancel immediately.
	 */
	protected void cancelImpl(int tag) {

	}
	

	/**
	 * called on cancel. this is only called when , often called by
	 * {@linkplain #onLogicSuccess(int)}}.
	 * 
	 * @param resultCode
	 *         the code of perform result. see {@linkplain LogicAction#RESULT_SUCCESS} / {@linkplain LogicAction#RESULT_FAILED} or others.
	 * @param tag
	 *            the tag .
	 * @param param
	 *            the logic parameter.
	 */
	protected void onCancel(int resultCode, int tag, LogicParam param) {

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
	 * @return true if success. default is true. this will effect the callback.
	 *         only true the callbacks can be invoke, false not invoke.
	 * @see {@linkplain #dispatchResult(int, int)}
	 */

	protected boolean onLogicResult(int resultCode, int tag, LogicParam lm) {
		return true;
	}

	/**
	 * called on logic result failed.
	 * 
	 * @return true if success. default is true. this will effect the callback.
	 *         only true the callbacks can be invoke, false not invoke.
	 * @see {@linkplain #dispatchResult(int, int)}
	 */
	protected boolean onLogicFailed(int tag) {
		return true;
	}

	/**
	 * called on logic success.
	 * 
	 * @param tag
	 *            the tag
	 * @return the handle result. if it is cancelled return false. this will
	 *         effect the callback. only true the callbacks can be invoke, false
	 *         not invoke.
	 * @see #dispatchResult(int, int)
	 */
	protected boolean onLogicSuccess(int tag) {
		return true;
	}
	
	protected static class TagInfo {
		public final LogicParam mLogicParam;
		public final AtomicInteger mState;

		TagInfo(LogicParam mLogicParam) {
			this.mLogicParam = mLogicParam;
			this.mState = new AtomicInteger(STATE_STARTED);
		}
	}
	
	protected static class ScheduleHandler {
		private long delay;
		private Scheduler schedulerOn;
		private Scheduler observeOn;
		private WeakReference<Runnable> mWeakScheduleTask;
		
		public void setPerformScheduler(Scheduler s){
			this.schedulerOn = s;
		}
		public void setCallbackScheduler(Scheduler s){
			this.observeOn = s;
		}
		public void setDelay(long delay){
			this.delay = delay >=0 ? delay : 0;
		}

		public void schedule(Runnable task) {
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

		public void scheduleCallback(Runnable task) {
			if (observeOn == null) {
				task.run();
			} else {
				observeOn.post(task);
			}
		}

		public void cancel() {
			if (mWeakScheduleTask != null) {
				Runnable task = mWeakScheduleTask.get();
				if (task != null) {
					schedulerOn.remove(task);
				}
			}
		}
	}

	protected static class CallbackRunner {
		private final int op;
		private final int resultCode;
		private final int tag;
		private final LogicParam lp;
		ScheduleHandler s;

		public CallbackRunner(int op, int resultCode, int tag, LogicParam lp) {
			super();
			this.op = op;
			this.resultCode = resultCode;
			this.tag = tag;
			this.lp = lp;
		}

		public void scheduleCallback(final LogicAction action, final LogicCallback callback) {
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
