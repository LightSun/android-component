package com.heaven7.java.logic;

import java.util.List;

/**
 * the logic runner. called on all logic action perform success.
 * @author heaven7
 * @see LogicManager#performParallel(List, LogicRunner)
 * @see LogicManager#performSequence(List, LogicRunner)
 */
public interface LogicRunner {

	/**
	 * called on all logic tasks perform success.
	 * @param tag the tag of logic
	 * @param result the last task perform result. is tasks is run async and 
	 *        {@linkplain LogicManager#performParallel(List, LogicRunner)} is called. this result
	 *         can't be sure to a fixed object.
	 * @param results the results of all logic-action perform. may be null.
	 */
	void run(int tag, Object result, List<?> results);
}
