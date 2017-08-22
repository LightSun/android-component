package com.heaven7.java.logic;

import java.util.List;

/**
 * the logic runner. called on all logic action perform done(success or failed).
 * 
 * @author heaven7
 * @see LogicManager#performParallel(List, int, LogicResultListener)
 * @see LogicManager#performSequence(List, int, LogicResultListener)
 */
public interface LogicResultListener {

	/**
	 * called on all logic tasks perform success.
	 * @param lm
	 *          the logic manager
	 * @param failedTask
	 *            the all failed tasks. may be null or length = 0, if all tasks
	 *            performed success. for example: in parallel , this list can be
	 *            empty or one or multiply.
	 * 
	 * @param lastResult
	 *            the last task performed result. if tasks is run asynchronous
	 *            and
	 *            {@linkplain LogicManager#performParallel(List, int, LogicResultListener)} or relative method
	 *            is called. this result can't be sure to a fixed type.
	 * @param results
	 *            the pool results of all logic-action performed. may be null.
	 *            this results is determined by
	 *            {@linkplain LogicManager#FLAG_SHARE_TO_POOL} and performed
	 *            state(success or failed), only perform success result can share to
	 *            pool.
	 * @see LogicTask#setFlags(int)
	 * @see LogicManager#performParallel(List, int, LogicResultListener)
	 * @see LogicManager#performSequence(List, int, LogicResultListener)
	 */
	void onFailed(LogicManager lm ,List<LogicTask> failedTask, Object lastResult, List<?> results);

	/**
	 * called on perform the all logic tasks success.
	 * @param lm
	 *          the logic manager
	 * @param lastTask
	 *            the last logic task.
	 * @param lastResult
	 *            the performed result of last task.
	 * @param results
	 *            the results of all tasks performed. may be null, this results
	 *            is determined by {@linkplain LogicManager#FLAG_SHARE_TO_POOL}
	 *            and must performed success. That means only flag of {@linkplain LogicManager#FLAG_SHARE_TO_POOL} is assigned 
	 *            and performed success, the perform result will put to this results(pool). 
	 */
	void onSuccess(LogicManager lm ,LogicTask lastTask, Object lastResult, List<?> results);

}
