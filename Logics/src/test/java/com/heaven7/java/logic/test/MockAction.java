package com.heaven7.java.logic.test;

import com.heaven7.java.logic.AbstractLogicAction;
import com.heaven7.java.logic.LogicParam;

public class MockAction extends AbstractLogicAction{
	
	private static final String TAG = "MockAction";
	public static final int TAG_OK    = 1;
	public static final int TAG_ERROR = 2;

	public MockAction(boolean wantCount) {
		super(wantCount);
	}

	@Override
	protected void performImpl(int tag, int count, LogicParam param) {
		String ts = tagToString(tag);
		System.out.println("MockSimpleAction_" + hashCode() + "_" + ts + " >>> start perform: thread = "
				+ Thread.currentThread().getName() + " , param = " + param);
		System.out.println(
				"MockSimpleAction_" + hashCode() + "_" + ts+ " >>> start perform: time = " + Schedulers.getCurrentTime());
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		dispatchResult(RESULT_SUCCESS, tag);
		System.out
				.println("MockSimpleAction_" + hashCode()+ "_" + ts + " >>> end perform: time = " + Schedulers.getCurrentTime());
	}
	
	@Override
	protected void cancelImpl(int tag) {
		Logger.i(TAG, "cancelImpl", "tag = " + tagToString(tag));
	}
	
	@Override
	protected void onCancel(int tag, LogicParam param) {
		Logger.i(TAG, "onCancel", "tag = " + tagToString(tag) + " ," + param);
	}

	static String tagToString(int tag){
		switch (tag) {
		case TAG_ERROR:
			return "TAG_ERROR";
			
		case TAG_OK:
			return "TAG_OK";
		}
		throw new UnsupportedOperationException("wrong tag = " + tag);
	}
}
