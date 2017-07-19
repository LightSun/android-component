package com.heaven7.java.logic;

import static com.heaven7.java.base.util.SafeUtil.getAndUpdate;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicReference;

import com.heaven7.java.base.util.SafeUtil;
import com.heaven7.java.logic.AbstractLogicAction.CallbackRunner;
import com.heaven7.java.logic.AbstractLogicAction.Schedulers;
import com.heaven7.java.logic.AbstractLogicAction.TagInfo;

/**
 * a simple implements of logic action. this class just ignore the logic tag.
 * @author heaven7
 */
public abstract class SimpleLogicAction extends ContextDataImpl implements LogicAction{
	
	private final AtomicReference<Schedulers> mAR_Scheduler;
	private final AtomicReference<TagInfo> mAR_tagInfo;
	private final CopyOnWriteArrayList<LogicCallback> mCallbacks;
	
	public SimpleLogicAction(){
		mAR_Scheduler = new AtomicReference<AbstractLogicAction.Schedulers>(new Schedulers());
		mAR_tagInfo = new AtomicReference<TagInfo>();
		mCallbacks = new CopyOnWriteArrayList<LogicCallback>();
	}
	
	@Override
	public LogicParam getLogicParameter(int tag) {
		TagInfo info = mAR_tagInfo.get();
		return info != null ? info.mLogicParam : null;
	}

	@Override
	public void addStateCallback(int tag, LogicCallback callback) {
		mCallbacks.add(callback);
	}

	@Override
	public void removeStateCallback(int tag, LogicCallback callback) {
		mCallbacks.remove(callback);
	}

	@Override
	public void scheduleOn(int tag,final Scheduler scheduler) {
		getAndUpdate(mAR_Scheduler, new SafeUtil.SafeOperator<Schedulers>() {
			@Override
			public Schedulers apply(Schedulers pre) {
				pre.schedulerOn = scheduler;
				return pre;
			}
		});
	}

	@Override
	public void setDelay(int tag,final long delay) {
		getAndUpdate(mAR_Scheduler, new SafeUtil.SafeOperator<Schedulers>() {
			@Override
			public Schedulers apply(Schedulers pre) {
				pre.delay = delay;
				return pre;
			}
		});
	}

	@Override
	public void observeOn(int tag, final Scheduler scheduler) {
		getAndUpdate(mAR_Scheduler, new SafeUtil.SafeOperator<Schedulers>() {
			@Override
			public Schedulers apply(Schedulers pre) {
				pre.observeOn = scheduler;
				return pre;
			}
		});
	}

	@Override
	public void perform(final int tag,final LogicParam param) {
		if(!mAR_tagInfo.compareAndSet(null, new TagInfo(param))){
			return;
		}
		//start.
		dispatchCallbackInternal(AbstractLogicAction.OP_START, 0, tag, param);
		//do perform
		getAndUpdate(mAR_Scheduler, new SchedulerOperator(param));
	}
	
	@Override
	public void cancel(int tag) {
		//handle tag
		TagInfo info = mAR_tagInfo.getAndSet(null);
		if(info == null){
			//already cancelled.
			return;
		}
		if(!info.mCancelled.compareAndSet(false, true)){
			return;
		}
		//cancel scheduler
		getAndUpdate(mAR_Scheduler, new SafeUtil.SafeOperator<Schedulers>() {
			@Override
			public Schedulers apply(Schedulers pre) {
				pre.cancel();
				return pre;
			}
		});
		cancelImpl();
	}

	@Override
	public boolean dispatchResult(int resultCode, int tag) {
		final LogicParam lm = getLogicParameter(tag);

		dispatchCallbackInternal(AbstractLogicAction.OP_RESULT, resultCode, tag, lm);
		
		boolean result;
		switch (resultCode) {
			case RESULT_SUCCESS:
				return onLogicSuccess();

		    case RESULT_FAILED:
			    return onLogicFailed();
			
			default:
				result = dispatchLogicResult(resultCode, lm);
		}
		return result;
	}

	@Override
	public boolean isRunning(int tag) {
        return isRunning();
	}

	@Override
	public boolean isRunning() {
		TagInfo info = mAR_tagInfo.get();
		return info != null && !info.mCancelled.get();
	}
	
	
	private void dispatchCallbackInternal(int op, int resultCode, int tag, LogicParam lm) {
		final CallbackRunner runner = new CallbackRunner(op, resultCode, tag, lm);
		runner.s = mAR_Scheduler.get();
		for (LogicCallback cl : mCallbacks) {
			runner.scheduleCallback(this, cl);
		}
	}
	
	//========================== protected method ===============================
	
	/**
	 * called on logic result success.
	 * @return the handle result. default is true.
	 */
	protected boolean onLogicSuccess() {
		TagInfo info = mAR_tagInfo.getAndSet(null);
		if(info == null){
			return false;
		}
		if(info.mCancelled.get()){
			onCancel(info.mLogicParam);
		}
		return true;
	}
	
	/**
	 * called on logic result failed.
	 * @return the handle result. default is true.
	 */
	protected boolean onLogicFailed() {
		mAR_tagInfo.getAndSet(null);
		return true;
	}
	
	/**
	 * do cancel this performed logic. because sometimes we need to cancel other operation. 
	 * called by {@linkplain #cancel(int)}.
	 */
	protected void cancelImpl() {
		
	}
	
	/**
	 * called on cancel this logic action
	 * @param lp the logic parameter
	 */
	protected void onCancel(LogicParam lp) {
		
	}
	
	/**
	 * called on logic result.
	 * 
	 * @param resultCode
	 *            the result code. but not {@linkplain #RESULT_SUCCESS} or
	 *            {@linkplain #RESULT_FAILED}.
	 * @param lm
	 *            the logic parameter
	 * @return true if dispatch success.
	 */

	protected boolean dispatchLogicResult(int resultCode, LogicParam lm) {
		return false;
	}
	
	/**
	 * do perform the logic action by target logic parameter.
	 * @param param the target logic parameter.
	 */
	protected abstract void performImpl(LogicParam param);
	
	
	private class SchedulerOperator implements Runnable, SafeUtil.SafeOperator<Schedulers>{
		
		private final LogicParam param;
		
		public SchedulerOperator(LogicParam param) {
			super();
			this.param = param;
		}

		@Override
		public Schedulers apply(Schedulers pre) {
			pre.schedule(this);
			return pre;
		}

		@Override
		public void run() {
			performImpl(param);
		}
		
	}

}
