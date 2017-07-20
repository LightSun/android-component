package com.heaven7.java.logic.test;

import com.heaven7.java.logic.LogicManager;
import com.heaven7.java.logic.LogicParam;
import com.heaven7.java.logic.LogicTask;

import junit.framework.TestCase;

public class TestSimpleLogic extends TestCase{

	private static final String TAG = "TestSimpleLogic";
	private final LogicManager mLm = new LogicManager();
	private final MockSimpleAction mAction = new MockSimpleAction();
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testNormalAsync(){
		LogicTask task = LogicTask.ofSimple(mAction, new LogicParam().setData("testNormalAsync"))
				.schedulerOn(Schedulers.ASYNC)
				.delay(3000)
				;
		System.out.println("============ testNormalAsync() >>> start time = " + Schedulers.getCurrentTime());
		mLm.executeSequence(task, null);
	}
	
	public void testNormal(){
		LogicTask task = LogicTask.ofSimple(mAction, new LogicParam().setData("testNormal"))
				.schedulerOn(Schedulers.DEFAULT)
				.delay(3000)
				;
		System.out.println("============ testNormal() >>> start time = " + Schedulers.getCurrentTime());
		mLm.executeSequence(task, null);
	}
	
	public void testCancel(){
		LogicTask task1 = LogicTask.ofSimple(new MockSimpleAction(), new LogicParam().setData("testCancel_1"))
				.schedulerOn(Schedulers.ASYNC)
				.delay(2500)
				;
		LogicTask task2 = LogicTask.ofSimple(mAction, new LogicParam().setData("testCancel_2"))
				.schedulerOn(Schedulers.newAsyncScheduler())
				.delay(3000)
				;
		System.out.println("============ testCancel() >>> start time = " + Schedulers.getCurrentTime());
		final int key = mLm.executeParallel(new LogicTask[]{task1, task2 }, new Runnable() {
			@Override
			public void run() {
				Logger.i(TAG, "testCancel", "all Task done! thread = " + Thread.currentThread().getName());
			}
		});
		Logger.i(TAG, "testCancel", "key = " + key);
		Schedulers.newAsyncScheduler().postDelay(2400, new Runnable() {
			@Override
			public void run() {
				mLm.cancel(key);
			    assertNull(mAction.getLogicParameter(0));
			}
		});
	}
	public void testCancelSequence(){
		LogicTask task1 = LogicTask.ofSimple(new MockSimpleAction(), new LogicParam().setData("testCancelSequence_1"))
				.schedulerOn(Schedulers.ASYNC)
				.delay(2500)
				;
		LogicTask task2 = LogicTask.ofSimple(mAction, new LogicParam().setData("testCancelSequence_2"))
				.schedulerOn(Schedulers.newAsyncScheduler())
				.delay(3000)
				;
		System.out.println("============ testCancelSequence() >>> start time = " + Schedulers.getCurrentTime());
		final int key = mLm.executeSequence(new LogicTask[]{task1, task2 }, new Runnable() {
			@Override
			public void run() {
				Logger.i(TAG, "testCancelSequence", "all Task done! thread = " + Thread.currentThread().getName());
			}
		});
		Logger.i(TAG, "testCancel", "key = " + key);
		Schedulers.newAsyncScheduler().postDelay(2600, new Runnable() {
			@Override
			public void run() {
				mLm.cancel(key);
			    assertNull(mAction.getLogicParameter(0));
			}
		});
	}
	
	public void testScheduler(){
		LogicTask task1 = LogicTask.ofSimple(new MockSimpleAction(), new LogicParam().setData("testScheduler_1"))
				.schedulerOn(Schedulers.ASYNC)
				.observeOn(Schedulers.newAsyncScheduler())
				.delay(2500)
				;
		LogicTask task2 = LogicTask.ofSimple(mAction, new LogicParam().setData("testScheduler_2"))
				.schedulerOn(Schedulers.newAsyncScheduler())
				.delay(3000)
				;
		mLm.executeSequence(new LogicTask[]{task1, task2 }, new Runnable() {
			@Override
			public void run() {
				Logger.i(TAG, "testScheduler", "all Task done! thread = " + Thread.currentThread().getName());
			}
		});
	}
	
	public static void main(String[] args) {
		TestSimpleLogic logic = new TestSimpleLogic();
		//logic.testNormalAsync();
		//logic.testNormal();
		//logic.testCancel();
		logic.testCancelSequence();
		//logic.testScheduler();
	}
}
