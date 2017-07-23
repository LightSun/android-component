package com.heaven7.java.logic;

import com.heaven7.java.base.util.SparseArray;
import com.heaven7.java.base.util.Throwables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
	public static final int FLAG_SHARE_TO_NEXT = 0x1;
	/** the flag indicate logic task perform result will share to pool. */
	public static final int FLAG_SHARE_TO_POOL = 0x2;

	private static final String TAG = "LogicManager";
	private final SparseArray<LogicTask> mLogicMap;
	private final SparseArray<ArrayList> mResultMap;

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
	private final AtomicInteger mIndex = new AtomicInteger();

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
	 *            the key . see {@linkplain #performParallel(List, Runnable)} or
	 *            {@linkplain #performSequence(List, Runnable)}.
	 */
	public void cancel(int key) {
		cancelByKey(key);
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
	 */
	public int performParallel(LogicTask[] parallels, LogicRunner successAction) {
		return performParallel(Arrays.asList(parallels), successAction);
	}

	/**
	 * executeSequence the tasks in Parallel.
	 * 
	 * @param parallels
	 *            the all tasks.
	 * @param successAction
	 *            the success action , called when all task done.
	 * @return the key of this operation.
	 */
	public int performParallel(List<LogicTask> parallels, LogicRunner successAction) {
		Throwables.checkEmpty(parallels);
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
		final ParallelCallback callback = new ParallelCallback(key, parallels, successAction);
		final Object data = getContextData();
		for (LogicTask task : parallels) {
			task.setContextData(data);
			task.addStateCallback(callback);
			task.perform();
		}
		return key;
	}

	/**
	 * perform the tasks in sequence with the end action.
	 * 
	 * @param tag
	 *            the tag
	 * @param logicAction
	 *            state performer
	 * @param lp
	 *            the logic parameter
	 * @param endAction
	 *            the end action, called when perform the all target logic tasks
	 *            done. can be null.
	 * @return the key of this operation.
	 */
	public int performSequence(int tag, LogicAction logicAction, LogicParam lp, LogicRunner endAction) {
		return performSequence(new LogicTask[] { LogicTask.of(tag, logicAction, lp) }, endAction);
	}

	/**
	 * perform the tasks in sequence with the end action.
	 *
	 * @param task
	 *            the logic task
	 * @param endAction
	 *            the end action, called when perform the all target logic tasks
	 *            done. can be null.
	 * @return the key of this operation.
	 */
	public int performSequence(LogicTask task, LogicRunner endAction) {
		return performSequence(new LogicTask[] { task }, endAction);
	}

	/**
	 * perform the tasks in sequence with the end action.
	 *
	 * @param first
	 *            the first logic task
	 * @param second
	 *            the second logic task
	 * @param endAction
	 *            the end action, called when perform the all target logic tasks
	 *            done. can be null.
	 * @return the key of this operation.
	 */
	public int performSequence(LogicTask first, LogicTask second, LogicRunner endAction) {
		return performSequence(new LogicTask[] { first, second }, endAction);
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
	 * @param endAction
	 *            the end action, called when perform the all target logic tasks
	 *            done. can be null.
	 * @return the key of this operation.
	 */
	public int performSequence(LogicTask first, LogicTask second, LogicTask third, LogicRunner endAction) {
		return performSequence(new LogicTask[] { first, second, third }, endAction);
	}

	/**
	 * perform the tasks in sequence with the end action.
	 *
	 * @param tasks
	 *            the logic tasks
	 * @param endAction
	 *            the end action, called when perform the all target logic tasks
	 *            done. can be null.
	 * @return the key of this operation.
	 */
	public int performSequence(LogicTask[] tasks, LogicRunner endAction) {
		return performSequence(Arrays.asList(tasks), endAction);
	}

	/**
	 * perform the tasks in sequence with the end action.
	 *
	 * @param tasks
	 *            the logic tasks, you can't modify this list outside. or else
	 *            you may cause bug.
	 * @param endAction
	 *            the end action, called when perform the all target logic tasks
	 *            done. can be null.
	 * @return the key of this operation.
	 */
	public int performSequence(List<LogicTask> tasks, LogicRunner endAction) {
		Throwables.checkNull(tasks);
		if (tasks.size() == 0) {
			throw new IllegalArgumentException("must assign a logic action");
		}
		final int key = mIndex.incrementAndGet();

		performSequenceImpl(tasks, key, 0, endAction, null);
		return key;
	}

	//lastResult the perform result of last logic task.
	private void performSequenceImpl(List<LogicTask> tasks, int key, int currentIndex, 
			LogicRunner endAction, Object lastResult) {
		final LogicTask target = tasks.get(currentIndex);
		target.logicParam.setLastResult(lastResult);
		target.setContextData(getContextData());
		target.addStateCallback(new SequenceCallback(tasks, key, currentIndex, endAction));
		target.perform();
	}

	// this key is blur
	private void cancelByKey(int key) {
		final int type = (key & MASK_TYPE) >> 24;
		switch (type) {
		case TYPE_PARALLEL:
			int baseKey = key & MASK_MAIN;
			int count = (key & MASK_SECONDARY) >> SHIFT_SECONDARY;
			for (int i = 0; i < count; i++) {
				cancelByRealKey(baseKey + i);
			}
			break;

		case 0:
			cancelByRealKey(key);
		}
		synchronized (mResultMap) {
			mResultMap.remove(key);
		}
	}

	private void cancelByRealKey(int realKey) {
		LogicTask task;
		synchronized (mLogicMap) {
			task = mLogicMap.getAndRemove(realKey);
		}
		if (task != null) {
			task.cancel();
		} else {
			System.err.println(
					TAG + " >>> called [ cancelByRealKey() ]" + "cancel task .but key not exists , key = " + realKey);
			// Logger.w(TAG,"cancelByRealKey","cancel task .but key not exists ,
			// key = " + realKey);
		}
	}

	/**
	 * process the result . if is end .remove mapping from the result map.
	 * 
	 * @param key
	 *            the unique key of parallel/sequence tasks.
	 * @param result
	 *            the logic result. comes from
	 *            {@linkplain LogicAction#dispatchResult(int, LogicResult)}.
	 * @param theEnd
	 *            true if the parallel/sequence tasks have run done. false otherwise.
	 * @param next 
	 *            the next logic parameter.  null when in parallel tasks.        
	 * @return the result list of all tasks perform.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ArrayList processResult(int key, LogicResult result, boolean theEnd, LogicParam next) {
		// put result to result pool if need
		final int flags = result.getFlags();
		final boolean shareNext = (flags & FLAG_SHARE_TO_NEXT) == FLAG_SHARE_TO_NEXT;
		if (next == null && shareNext) {
			// parallel tasks may be async. so can't make sure the order of run.
			throw new UnsupportedOperationException("flag SHARE_TO_NEXT only support sequence tasks.");
		}
		final boolean sharePool = (flags & FLAG_SHARE_TO_POOL) == FLAG_SHARE_TO_POOL;
		final Object resultData = result.getData();
		
		ArrayList results = null;
		if (resultData != null) {
			if (shareNext) {
				next.setLastResult(resultData);
			}
			if(sharePool){
				if (theEnd) {
					synchronized (mResultMap) {
						results = mResultMap.getAndRemove(key);
					}
					if (results == null) {
						results = new ArrayList<>();
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
	 * the internal parallel callback
	 * 
	 * @author heaven7
	 *
	 */
	private class ParallelCallback extends SimpleLogicCallback {
		/** the key of this parallel tasks. */
		final int key;
		final List<LogicTask> parallelTasks;
		final LogicRunner mEnd;
		final AtomicInteger finishCount;

		public ParallelCallback(int key, List<LogicTask> parallelTasks, LogicRunner endAction) {
			this.key = key;
			this.parallelTasks = parallelTasks;
			this.mEnd = endAction;
			this.finishCount = new AtomicInteger(0);
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

			// remove from task pool 
			removeTask(LogicTask.of(tag, action, param));
			final boolean theEnd = finishCount.incrementAndGet() == getTaskCount();
			//handle perform result
			ArrayList results = processResult(key, result, theEnd, null);
			
			// callback if need
			if (theEnd && mEnd != null) {
				mEnd.run(tag, result.getData(), results);
				// System.out.println("task ok. " + param);
			}
		}

		@Override
		protected void onFailed(LogicAction action, int tag, LogicParam param, LogicResult result) {
			// remove task and result
			removeTask(LogicTask.of(tag, action, param));
			//TODO cancel(key); //one failed cancel all ?
		}

		private void removeTask(LogicTask task) {
			synchronized (mLogicMap) {
				int index = mLogicMap.indexOfValue(task, false);
				if (index >= 0) {
					mLogicMap.removeAt(index);
					// Logger.i(TAG,"removeTask","task is removed . task = " +
					// task);
				}
			}
			task.removeStateCallback(this);
		}
	}

	/**
	 * the sequence callback
	 * 
	 * @author heaven7
	 */
	private class SequenceCallback extends SimpleLogicCallback {

		final int key;
		final int curIndex;
		final List<LogicTask> mTasks;
		final LogicRunner endAction;

		public SequenceCallback(List<LogicTask> tasks, int key, int currentIndex, LogicRunner endAction) {
			this.key = key;
			this.curIndex = currentIndex;
			this.mTasks = tasks;
			this.endAction = endAction;
		}

		@Override
		public void onLogicStart(LogicAction action, int tag, LogicParam param) {
			synchronized (mLogicMap) {
				mLogicMap.put(key, mTasks.get(curIndex));
			}
		}

		@Override
		protected void onSuccess(LogicAction action, int tag, LogicParam param, LogicResult result) {
			//remove task
			removeTask();
			final boolean theEnd = curIndex == mTasks.size() - 1;
			//handle perform result.
			LogicTask task = mTasks.get(curIndex + 1);
			ArrayList list = processResult(key, result, theEnd, task.logicParam);
			if (theEnd) {
				// all end
				if (endAction != null) {
					endAction.run(tag, result.getData(), list);
				}
			} else {
				// perform next
				performSequenceImpl(mTasks, key, curIndex + 1, endAction, result.getData());
			}
		}

		@Override
		protected void onFailed(LogicAction action, int tag, LogicParam param, LogicResult result) {
			removeTask();
			//TODO one failed cancel all ?
			synchronized (mResultMap) {
				mResultMap.remove(key);
			}
		}

		private void removeTask() {
			synchronized (mLogicMap) {
				mLogicMap.remove(key);
			}
			// unregister.
			mTasks.get(curIndex).removeStateCallback(this);
		}
	}

}
