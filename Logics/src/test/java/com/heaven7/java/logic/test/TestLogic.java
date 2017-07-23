package com.heaven7.java.logic.test;

import static com.heaven7.java.logic.test.MockAction.TAG_ERROR;
import static com.heaven7.java.logic.test.MockAction.TAG_OK;

import com.heaven7.java.logic.LogicManager;
import com.heaven7.java.logic.LogicParam;
import com.heaven7.java.logic.LogicTask;

import junit.framework.TestCase;;

public class TestLogic extends TestCase{

	private static final String TAG = "TestSimpleLogic";
	private final LogicManager mLm = new LogicManager();
	private final MockAction mAction = new MockAction(true);
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}
	
	public void testNormalAsync(){
		LogicTask task = LogicTask.of(TAG_OK, mAction, new LogicParam().setData("testNormalAsync"))
				.schedulerOn(Schedulers.ASYNC)
				.delay(3000)
				;
		System.out.println("============ testNormalAsync() >>> start time = " + Schedulers.getCurrentTime());
		mLm.performSequence(task, null);
	}
	
	public void testNormal(){
		LogicTask task = LogicTask.of(TAG_OK, mAction, new LogicParam().setData("testNormal"))
				.schedulerOn(Schedulers.DEFAULT)
				.delay(3000)
				;
		System.out.println("============ testNormal() >>> start time = " + Schedulers.getCurrentTime());
		mLm.performSequence(task, null);
	}
	
	public void testCancel(){
		LogicTask task1 = LogicTask.of(TAG_OK, mAction, new LogicParam().setData("testCancel_1"))
				.schedulerOn(Schedulers.ASYNC)
				.delay(2500)
				;
		LogicTask task2 = LogicTask.of(TAG_ERROR, mAction , new LogicParam().setData("testCancel_2"))
				.schedulerOn(Schedulers.newAsyncScheduler())
				.delay(3000)
				;
		System.out.println("============ testCancel() >>> start time = " + Schedulers.getCurrentTime());
		final int key = mLm.performParallel(new LogicTask[]{task1, task2 }, new LogRunner("testCancel"));
		
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
		LogicTask task1 = LogicTask.of(TAG_OK, mAction, new LogicParam().setData("testCancelSequence_1"))
				.schedulerOn(Schedulers.ASYNC)
				.delay(2500)
				;
		LogicTask task2 = LogicTask.of(TAG_ERROR, mAction , new LogicParam().setData("testCancelSequence_2"))
				.schedulerOn(Schedulers.newAsyncScheduler())
				.delay(3000)
				;
		System.out.println("============ testCancelSequence() >>> start time = " + Schedulers.getCurrentTime());
		final int key = mLm.performSequence(new LogicTask[]{task1, task2 }, new LogRunner("testCancelSequence"));
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
		LogicTask task1 = LogicTask.of(TAG_OK,  mAction, new LogicParam().setData("testScheduler_1"))
				.schedulerOn(Schedulers.ASYNC)
				.observeOn(Schedulers.newAsyncScheduler())
				.delay(2500)
				;
		LogicTask task2 = LogicTask.of(TAG_ERROR, mAction, new LogicParam().setData("testScheduler_2"))
				.schedulerOn(Schedulers.newAsyncScheduler())
				.delay(3000)
				;
		mLm.performSequence(new LogicTask[]{task1, task2 }, new LogRunner("testScheduler"));
	}
	
	public static void main(String[] args) {
		TestLogic logic = new TestLogic();
		//logic.testNormalAsync();
		//logic.testNormal();
		//logic.testCancel();
	    logic.testCancelSequence();
		//logic.testScheduler();
	}
}
