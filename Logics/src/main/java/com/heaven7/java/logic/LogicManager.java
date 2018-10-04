package com.heaven7.java.logic;

import com.heaven7.java.base.util.DefaultPrinter;
import com.heaven7.java.base.util.SparseArray;
import com.heaven7.java.base.util.Throwables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * the logic manager help we handle an order logic tasks. it can run a
 * sequence/parallel tasks, support synchronous and asynchronous .
 * <p>
 * this class is thread safe.
 * </p>
 * 
 * @author heaven7
 * @see LogicTask
 * @see LogicTask#schedulerOn(Scheduler)
 * @see LogicTask#observeOn(Scheduler)
 */
public final class LogicManager extends ContextDataImpl {

	/**
	 * the flag indicate logic task perform result will share to next logic
	 * task. this flags can only used to sequence tasks.
	 */
	public static final int FLAG_SHARE_TO_NEXT = 0x0001;
	/** the flag indicate logic task perform result will share to pool. */
	public static final int FLAG_SHARE_TO_POOL = 0x0002;
	/** this flag indicate the tasks perform allow failed. that means. all left tasks will not be auto cancel/remove. */
	public static final int FLAG_ALLOW_FAILED = 0x0004;

	private static final String TAG = "LogicManager";
	
	private final SparseArray<LogicTask> mLogicMap;
	@SuppressWarnings("rawtypes")
	private final SparseArray<ArrayList> mResultMap;
	private final AtomicInteger mIndex = new AtomicInteger();

	/*
	 * type secondary index 0x ff ff ffff
	 */
	private static final int MASK_MAIN = 0x0000ffff;
	private static final int MASK_SECONDARY = 0x00ff0000;
	private static final int MASK_TYPE = 0xff000000;

	private static final int SHIFT_TYPE = 24;
	private static final int SHIFT_SECONDARY = 16;
	private static final int TYPE_PARALLEL = 1;

	private static final int MAX_PARALLEL_COUNT = 0xff;
	
	@SuppressWarnings("rawtypes")
	public LogicManager() {
		this.mLogicMap = new SparseArray<LogicTask>(3);
		this.mResultMap = new SparseArray<ArrayList>();
	}

	/**
	 * cancel the all task which is running.
	 */
	public void cancelAll() {
		synchronized (mLogicMap) {
			final int size = mLogicMap.size();
			for (int i = 0; i < size; i++) {
				mLogicMap.valueAt(i).cancel();
			}
			mLogicMap.clear();
		}
		synchronized (mResultMap) {
			mResultMap.clear();
		}
	}

	/**
	 * cancel the task which is assigned by target key.
	 * 
	 * @param key
	 *            the key . see {@linkplain #performParallel(List, LogicResultListener)} or
	 *            {@linkplain #performParallel(List, int, LogicResultListener)} .
	 */
	public void cancel(int key) {
		cancelByKey(key, false);
		synchronized (mResultMap) {
			mResultMap.remove(key);
		}
	}

	/**
	 * indicate the target key of tasks is running or not.
	 * 
	 * @param key
	 *            the key of last operation
	 * @return true if is running.
	 */
	public boolean isRunning(int key) {
		final int type = (key & MASK_TYPE) >> 24;
		switch (type) {
		case TYPE_PARALLEL:
			int baseKey = key & MASK_MAIN;
			int count = (key & MASK_SECONDARY) >> SHIFT_SECONDARY;
			synchronized (mLogicMap) {
				for (int i = 0; i < count; i++) {
					if (mLogicMap.get(baseKey + i) != null) {
						return true;
					}
				}
			}
			break;

		case 0:
			synchronized (mLogicMap) {
				return mLogicMap.get(key) != null;
			}
		}
		return false;
	}

	/**
	 * indicate the target task is running or not.
	 * 
	 * @param task
	 *            the logic task.
	 * @return true if is running.
	 */
	public boolean isRunning(LogicTask task) {
		synchronized (mLogicMap) {
			return mLogicMap.indexOfValue(task, false) >= 0;
		}
	}

	/**
	 * executeSequence the tasks in Parallel.
	 * 
	 * @param parallels
	 *            the all tasks.
	 * @param successAction
	 *            the success action , called when all task done.
	 * @return the key of this operation.
	 * @see #performParallel(LogicTask[], int, LogicResultListener)
	 */
	public int performParallel(LogicTask[] parallels,  LogicResultListener successAction) {
		return performParallel(Arrays.asList(parallels), 0,  successAction);
	}
	/**
	 * executeSequence the tasks in Parallel.
	 * 
	 * @param parallels
	 *            the all tasks.
	 * @param flags 
	 *            the flags of all tasks. see {@linkplain #FLAG_SHARE_TO_NEXT} /{@linkplain #FLAG_SHARE_TO_POOL}. 
	 *            in sequence task, flag of {@linkplain #FLAG_ALLOW_FAILED} is ignored. 
	 * @param successAction
	 *            the success action , called when all task done.
	 * @return the key of this operation.
	 */
	public int performParallel(LogicTask[] parallels, int flags, LogicResultListener successAction) {
		return performParallel(Arrays.asList(parallels), flags,  successAction);
	}

	/**
	 * executeSequence the tasks in Parallel.
	 * 
	 * @param parallels
	 *            the all tasks.
	 * @param listener
	 *            the logic listener, called when all task done.
	 * @return the key of this operation.
	 */
	public int performParallel(List<LogicTask> parallels, LogicResultListener listener) {
		return performParallel(parallels, 0, listener);
	}
	
	/**
	 * executeSequence the tasks in Parallel.
	 * 
	 * @param parallels
	 *            the all tasks.
	 * @param flags 
	 *            the flags of all tasks. see {@linkplain #FLAG_SHARE_TO_NEXT} /{@linkplain #FLAG_SHARE_TO_POOL}. 
	 *            in sequence task, flag of {@linkplain #FLAG_ALLOW_FAILED} is ignored.         
	 * @param listener
	 *            the logic listener, called when all task done.
	 * @return the key of this operation.
	 */
	public int performParallel(List<LogicTask> parallels, int flags, LogicResultListener listener) {
		Throwables.checkEmpty(parallels);
		flags &= ~FLAG_SHARE_TO_NEXT;
		final int size = parallels.size();
		if (size > MAX_PARALLEL_COUNT) {
			throw new UnsupportedOperationException("max parallel count must below " + MAX_PARALLEL_COUNT);
		}
		final int baseKey = mIndex.incrementAndGet();
		mIndex.addAndGet(size);
		// key of this parallel tasks.
		final int key = baseKey + (size << SHIFT_SECONDARY) + (TYPE_PARALLEL << SHIFT_TYPE);

		// put to logic pool
		synchronized (mLogicMap) {
			for (int i = 0; i < size; i++) {
				mLogicMap.put(baseKey + i, parallels.get(i));
			}
		}
		// add callback and perform
		final ParallelCallback callback = new ParallelCallback(key, parallels, listener ,
				flags > 0 && (flags & FLAG_ALLOW_FAILED) == FLAG_ALLOW_FAILED);
		final Object data = getContextData();
		for (LogicTask task : parallels) {
			task.mergeFlags(flags, false);
			task.setContextData(data);
			task.addStateCallback(callback);
			task.perform();
		}
		return key;
	}

	/**
	 * perform the tasks in sequence with the end action.
	 *
	 * @param task
	 *            the logic task
	 * @param listener
	 *            the logic tasks perform result listener. called on all tasks perform done.(may success or failed.
	 *            can be null.
	 * @return the key of this operation.
	 * @see #performSequence(List, int, LogicResultListener)
	 * @see #performSequence(LogicTask[], int, LogicResultListener)
	 */
	public int performSequence(LogicTask task, LogicResultListener listener) {
		return performSequence(new LogicTask[] { task }, 0, listener);
	}
	/**
	 * perform the tasks in sequence with the end action.
	 *
	 * @param task
	 *            the logic task
	 * @param listener
	 *            the logic tasks perform result listener. called on all tasks perform done.(may success or failed.
	 *            can be null.
	 * @param flags 
	 *            the flags of all tasks. see {@linkplain #FLAG_SHARE_TO_NEXT} /{@linkplain #FLAG_SHARE_TO_POOL}. 
	 *            in sequence task, flag of {@linkplain #FLAG_ALLOW_FAILED} is ignored. 
	 * @return the key of this operation.
	 */
	public int performSequence(LogicTask task, int flags, LogicResultListener listener) {
		return performSequence(new LogicTask[] { task }, flags, listener);
	}

	/**
	 * perform the tasks in sequence with the end action.
	 *
	 * @param first
	 *            the first logic task
	 * @param second
	 *            the second logic task
	 * @param listener
	 *            the logic tasks perform result listener. called on all tasks perform done.(may success or failed.
	 *            can be null.
	 * @return the key of this operation.
	 * @see #performSequence(LogicTask[], int, LogicResultListener)
	 * @see #performSequence(List, int, LogicResultListener)
	 */
	public int performSequence(LogicTask first, LogicTask second, LogicResultListener listener) {
		return performSequence(new LogicTask[] { first, second }, 0, listener);
	}
	
	/**
	 * perform the tasks in sequence with the end action.
	 *
	 * @param first
	 *            the first logic task
	 * @param second
	 *            the second logic task
	 * @param listener
	 *            the logic tasks perform result listener. called on all tasks perform done.(may success or failed.
	 *            can be null.
	 * @param flags 
	 *            the flags of all tasks. see {@linkplain #FLAG_SHARE_TO_NEXT} /{@linkplain #FLAG_SHARE_TO_POOL}. 
	 *            in sequence task, flag of {@linkplain #FLAG_ALLOW_FAILED} is ignored.  
	 * @return the key of this operation.
	 */
	public int performSequence(LogicTask first, LogicTask second, int flags ,LogicResultListener listener) {
		return performSequence(new LogicTask[] { first, second }, flags, listener);
	}

	/**
	 * perform the tasks in sequence with the end action.
	 *
	 * @param first
	 *            the first logic task
	 * @param second
	 *            the second logic task
	 * @param third
	 *            the third logic task
	 * @param listener
	 *            the logic tasks perform result listener. called on all tasks perform done.(may success or failed.
	 *            can be null.
	 * @return the key of this operation.
	 * @see #performSequence(List, int, LogicResultListener)
	 * @see #performSequence(LogicTask[], int, LogicResultListener)
	 */
	public int performSequence(LogicTask first, LogicTask second, LogicTask third,LogicResultListener listener) {
		return performSequence(new LogicTask[] { first, second, third }, 0,  listener);
	}
	/**
	 * perform the tasks in sequence with the end action.
	 *
	 * @param first
	 *            the first logic task
	 * @param second
	 *            the second logic task
	 * @param third
	 *            the third logic task
	 * @param listener
	 *            the logic tasks perform result listener. called on all tasks perform done.(may success or failed.
	 *            can be null.
	 * @param flags 
	 *            the flags of all tasks. see {@linkplain #FLAG_SHARE_TO_NEXT} /{@linkplain #FLAG_SHARE_TO_POOL}. 
	 *            in sequence task, flag of {@linkplain #FLAG_ALLOW_FAILED} is ignored.  
	 * @return the key of this operation.
	 */
	public int performSequence(LogicTask first, LogicTask second, LogicTask third, int flags ,LogicResultListener listener) {
		return performSequence(new LogicTask[] { first, second, third }, flags,  listener);
	}

	/**
	 * perform the tasks in sequence with the end action.
	 *
	 * @param tasks
	 *            the logic tasks
	 * @param listener
	 *            the logic tasks perform result listener. called on all tasks perform done.(may success or failed.
	 *            can be null.
	 * @return the key of this operation.
	 * @see #performParallel(List, int, LogicResultListener)
	 * @see #performParallel(LogicTask[], int, LogicResultListener)
	 */
	public int performSequence(LogicTask[] tasks, LogicResultListener listener) {
		return performSequence(Arrays.asList(tasks), 0, listener);
	}
	/**
	 * perform the tasks in sequence with the end action.
	 *
	 * @param tasks
	 *            the logic tasks
	 * @param listener
	 *            the logic tasks perform result listener. called on all tasks perform done.(may success or failed.
	 *            can be null.
	 * @param flags 
	 *            the flags of all tasks. see {@linkplain #FLAG_SHARE_TO_NEXT} /{@linkplain #FLAG_SHARE_TO_POOL}. 
	 *            in sequence task, flag of {@linkplain #FLAG_ALLOW_FAILED} is ignored.            
	 * @return the key of this operation.
	 */
	public int performSequence(LogicTask[] tasks, int flags, LogicResultListener listener) {
		return performSequence(Arrays.asList(tasks), flags, listener);
	}

	/**
	 * perform the tasks in sequence with the end action.
	 *
	 * @param tasks
	 *            the logic tasks, you can't modify this list outside. or else
	 *            you may cause bug.
	 * @param listener
	 *            the logic tasks perform result listener. called on all tasks perform done.(may success or failed.)
	 *            can be null.
	 * @return the key of this operation.
	 * @see #performSequence(List, int, LogicResultListener)
	 */
	public int performSequence(List<LogicTask> tasks, LogicResultListener listener) {
		return performSequence(tasks, 0,listener);
	}
	/**
	 * perform the tasks in sequence with the end action.
	 *
	 * @param tasks
	 *            the logic tasks, you can't modify this list outside. or else
	 *            you may cause bug.
	 * @param listener
	 *            the logic tasks perform result listener. called on all tasks perform done.(may success or failed.)
	 *            can be null.
	 * @param flags 
	 *            the flags of all tasks. see {@linkplain #FLAG_SHARE_TO_NEXT} /{@linkplain #FLAG_SHARE_TO_POOL}. 
	 *            in sequence task, flag of {@linkplain #FLAG_ALLOW_FAILED} is ignored.           
	 * @return the key of this operation.
	 */
	public int performSequence(List<LogicTask> tasks, int flags, LogicResultListener listener) {
		Throwables.checkNull(tasks);
		if (tasks.size() == 0) {
			throw new IllegalArgumentException("must assign a logic action");
		}
		final int key = mIndex.incrementAndGet();

		performSequenceImpl(tasks, key, 0, listener, null, flags);
		return key;
	}

	//lastResult the perform result of last logic task.
	private void performSequenceImpl(List<LogicTask> tasks, int key, int currentIndex, 
			LogicResultListener listener, Object lastResult,int flags) {
		final LogicTask target = tasks.get(currentIndex);
		target.mergeFlags(flags, true);
		target.setLastResult(lastResult);
		target.setContextData(getContextData());
		target.addStateCallback(new SequenceCallback(tasks, key, currentIndex, listener, flags));
		target.perform();
	}

	// this key is blur
	private void cancelByKey(int key, boolean invokeByInternal) {
		final int type = (key & MASK_TYPE) >> 24;
		switch (type) {
		case TYPE_PARALLEL:
			int baseKey = key & MASK_MAIN;
			int count = (key & MASK_SECONDARY) >> SHIFT_SECONDARY;
			for (int i = 0; i < count; i++) {
				cancelByRealKey(baseKey + i, invokeByInternal);
			}
			break;

		case 0:
			cancelByRealKey(key, invokeByInternal);
		}
	}

	//invokeByInternal: called by internal or not
	private void cancelByRealKey(int realKey, boolean invokeByInternal) {
		LogicTask task;
		synchronized (mLogicMap) {
			task = mLogicMap.getAndRemove(realKey);
		}
		if (task != null) {
			task.cancel();
			//internal should reset.
			if(invokeByInternal){
			    task.resetLogicAction();
			}
		} else {
			//internal just ignore log
			if(!invokeByInternal){
			    DefaultPrinter.getDefault().warn(TAG, "cancelByRealKey", 
			    		"cancel task .but key not exists , key = " + realKey);
			}
		}
	}

	/**
	 * process the result . if is end remove mapping from the result map. this is only called on success.
	 * 
	 * @param key
	 *            the unique key of parallel/sequence tasks.
	 * @param result
	 *            the logic result. comes from
	 *            {@linkplain LogicAction#dispatchResult(int, LogicResult)}.
	 * @param theEnd
	 *            true if the parallel/sequence tasks have run done. false otherwise.
	 * @param next 
	 *            the next logic task.  null when is the end.      
	 * @param parallel
	 *            true call this from the parallel callback.             
	 * @return the result list of all tasks perform.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ArrayList processResult(int key, LogicResult result, boolean theEnd, LogicTask next, boolean parallel) {
		// put result to result pool if need
		final int flags = result.getFlags();
		final boolean shareNext = (flags & FLAG_SHARE_TO_NEXT) == FLAG_SHARE_TO_NEXT;
		if (parallel && shareNext) {
			// parallel tasks may be async. so can't make sure the order of run.
			throw new UnsupportedOperationException("flag SHARE_TO_NEXT only support sequence tasks.");
		}
		final boolean sharePool = (flags & FLAG_SHARE_TO_POOL) == FLAG_SHARE_TO_POOL;
		final Object resultData = result.getData();
		
		ArrayList results = null;
		if (resultData != null) {
			if (shareNext && next != null ) {
				next.setLastResult(resultData);
			}
			if(sharePool){
				if (theEnd) {
					synchronized (mResultMap) {
						results = mResultMap.getAndRemove(key);
					}
					if (results == null) {
						results = new ArrayList();
					}
					results.add(resultData);
				} else {
					synchronized (mResultMap) {
						results = mResultMap.get(key);
						if (results == null) {
							results = new ArrayList();
							mResultMap.put(key, results);
						}
						results.add(resultData);
					}
				}
			}
		}
		return results;
	}

	/**
	 * the internal parallel callback. if any task perform failed . mark failed. later {@linkplain LogicResultListener#onFailed(LogicManager, List, Object, List)}.
	 * will be called.
	 * 
	 * @author heaven7
	 *
	 */
	@SuppressWarnings("rawtypes")
	private class ParallelCallback extends SimpleLogicCallback {
		/** the key of this parallel tasks. */
		final int key;
		final List<LogicTask> parallelTasks;
		final LogicResultListener listener;
		final AtomicInteger finishCount;
		final boolean allowFailed;
		final Vector<LogicTask> mFailedTasks;
		final AtomicBoolean mDisableCallback; 

		public ParallelCallback(int key, List<LogicTask> parallelTasks, LogicResultListener endAction,boolean allowFailed) {
			this.key = key;
			this.parallelTasks = parallelTasks;
			this.listener = endAction;
			this.finishCount = new AtomicInteger(0);
			this.allowFailed = allowFailed;
			this.mFailedTasks = new Vector<LogicTask>();
			this.mDisableCallback = new AtomicBoolean(false);
		}

		private int getTaskCount() {
			return parallelTasks.size();
		}

		@Override
		public void onLogicStart(LogicAction action, int tag, LogicParam param) {
			// if put parallel task in here. can cause the last Logic task may
			// not be cancelled.
			/*
			 * int count = getTaskCount(); synchronized (mLogicMap) { for(int i
			 * = 0 ; i < count ; i++){ mLogicMap.put(key + i,
			 * parallelTasks.get(i)); } }
			 */
		}

		@Override
		protected void onSuccess(LogicAction action, int tag, LogicParam param, LogicResult result) {
			final boolean theEnd = finishCount.incrementAndGet() == getTaskCount();
			// remove from task pool 
			LogicTask task = LogicTask.of(tag, action, param).setFlags(result.getFlags());
			removeTask(task);
			//handle perform result
			ArrayList results = processResult(key, result, theEnd, null, true);
			DefaultPrinter.getDefault().debug(TAG, "onSuccess", "results = " + results);
			
			// callback if need
			if (theEnd && listener != null) {
				//no failed tasks.
				if(mFailedTasks.size() == 0){
				   listener.onSuccess(LogicManager.this, task, result.getData(), results);
				   // System.out.println("task ok. " + param);
				}else{
					listener.onFailed(LogicManager.this, mFailedTasks, result.getData(), results);
				}
			}
		}

		@Override
		protected void onFailed(LogicAction action, int tag, LogicParam param, LogicResult result) {
			if(!allowFailed){
				//may multiply thread comes into.
				if(!mDisableCallback.compareAndSet(false, true)){
					return;
				}
			}
			final boolean theEnd = finishCount.incrementAndGet() == getTaskCount();
			LogicTask task = LogicTask.of(tag, action, param)
					.setFlags(result.getFlags());
					
			// remove task and result
			removeTask(task);
			
			if(allowFailed){
				mFailedTasks.add(task);
				//allow failed. only callback when is the last task
				if (theEnd) {
					callbackOnFailed(result, mFailedTasks);
				}
			}else{
				//cancel left tasks.
				cancelByKey(key, true);
				//not allow failed. direct callback onFailed.
				callbackOnFailed(result, Arrays.asList(task));
			}
			
		}

		private void callbackOnFailed(LogicResult result, List<LogicTask> failedTasks) {
			ArrayList results = null;
			synchronized (mResultMap) {
				results = mResultMap.getAndRemove(key);
			}
			if(listener != null){
			    listener.onFailed(LogicManager.this, failedTasks, result.getData(), results);
			}
		}

		private void removeTask(LogicTask task) {
			synchronized (mLogicMap) {
				int index = mLogicMap.indexOfValue(task, false);
				if (index >= 0) {
					LogicTask actualTask = mLogicMap.getAndRemove(mLogicMap.keyAt(index));
					actualTask.removeStateCallback(this);
					// Logger.i(TAG,"removeTask","task is removed . task = " +
					// task);
				}
			}
		}
	}

	/**
	 * the sequence callback, if any task perform failed . mark failed. call
	 * {@linkplain LogicResultListener#onFailed(LogicManager, List, Object, List)}  immediately.
	 * 
	 * @author heaven7
	 */
	private class SequenceCallback extends SimpleLogicCallback {

		final int key;
		final int curIndex;
		final List<LogicTask> mTasks;
		final LogicResultListener mListener;
		final int flags;

		public SequenceCallback(List<LogicTask> tasks, int key, int currentIndex,
				LogicResultListener listener, int flags) {
			this.key = key;
			this.curIndex = currentIndex;
			this.mTasks = tasks;
			this.mListener = listener;
			this.flags = flags;
		}

		@Override
		public void onLogicStart(LogicAction action, int tag, LogicParam param) {
			synchronized (mLogicMap) {
				mLogicMap.put(key, mTasks.get(curIndex));
			}
		}
		@SuppressWarnings("rawtypes")
		@Override
		protected void onSuccess(LogicAction action, int tag, LogicParam param, LogicResult result) {
			//remove task
			final LogicTask task = removeTask();
			
			final boolean theEnd = curIndex == mTasks.size() - 1;
			//handle perform result.
			ArrayList list = processResult(key, result, theEnd, 
					theEnd ? null : mTasks.get(curIndex + 1), false);
			
			DefaultPrinter.getDefault().debug(TAG, "onSuccess", "results = " + list);
			if (theEnd) {
				// all end
				if (mListener != null) {
					mListener.onSuccess(LogicManager.this, task, result.getData(), list);
				}
			} else {
				// perform next
				performSequenceImpl(mTasks, key, curIndex + 1, mListener, result.getData(), flags);
			}
		}

		@SuppressWarnings("rawtypes")
		@Override
		protected void onFailed(LogicAction action, int tag, LogicParam param, LogicResult result) {
			final LogicTask failedTask = removeTask();
			ArrayList results = null;
			synchronized (mResultMap) {
				results = mResultMap.getAndRemove(key);
			}
			if (mListener != null) {
				mListener.onFailed(LogicManager.this, Arrays.asList(failedTask), 
						result.getData(), results);
			}
		}

		private LogicTask removeTask() {
			synchronized (mLogicMap) {
				mLogicMap.remove(key);
			}
			// unregister.
			LogicTask task = mTasks.get(curIndex);
			task.removeStateCallback(this);
			return task;
		}
	}

}
