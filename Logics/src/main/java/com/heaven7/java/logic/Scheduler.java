package com.heaven7.java.logic;

/**
 * 
 * the scheduler class help we handle asynchronous or synchronous task.
 * 
 * @author heaven7
 */
public interface Scheduler {
	/**
	 * post the task by target delay.
	 * 
	 * @param delay
	 *            the delay in mills
	 * @param task
	 *            the runnable task,
	 */
	void postDelay(long delay, Runnable task);

	/**
	 * remove the task from message pool.
	 * 
	 * @param task
	 *            the task.
	 */
	void remove(Runnable task);
}
