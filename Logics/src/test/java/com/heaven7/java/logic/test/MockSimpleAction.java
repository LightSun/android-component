package com.heaven7.java.logic.test;

import com.heaven7.java.logic.LogicParam;
import com.heaven7.java.logic.SimpleLogicAction;

public class MockSimpleAction extends SimpleLogicAction {

	public MockSimpleAction() {
	}
	
	@Override
	protected void cancelImpl(int tag) {
		System.out.println("MockSimpleAction_" + hashCode() + " >>> called cancelImpl()");
	}

	@Override
	protected void performImpl(int tag, int count,LogicParam param) {
		System.out.println("MockSimpleAction_" + hashCode() + " >>> start perform: thread = "
				+ Thread.currentThread().getName() + " , param = " + param);
		System.out.println(
				"MockSimpleAction_" + hashCode() + " >>> start perform: time = " + Schedulers.getCurrentTime());
		try {
			Thread.sleep(3000);
			dispatchResult(RESULT_SUCCESS, 0);
			System.out
					.println("MockSimpleAction_" + hashCode() + " >>> end perform: time = " + Schedulers.getCurrentTime());
		} catch (InterruptedException e) {
			dispatchResult(RESULT_SUCCESS, 0);
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onCancel(int resultCode, int tag, LogicParam param) {
		System.out.println("MockSimpleAction_" + hashCode() + " >>> called onCancel(): " + param);
	}
	
}
