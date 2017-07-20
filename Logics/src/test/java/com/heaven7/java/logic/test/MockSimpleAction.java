package com.heaven7.java.logic.test;

import com.heaven7.java.logic.LogicParam;
import com.heaven7.java.logic.SimpleLogicAction;

public class MockSimpleAction extends SimpleLogicAction {

	public MockSimpleAction() {
	}

	@Override
	protected void cancelImpl() {
		System.out.println("MockSimpleAction_" + hashCode() + " >>> called cancelImpl()");
	}

	@Override
	protected void performImpl(LogicParam param) {
		System.out.println("MockSimpleAction_" + hashCode() + " >>> start perform: thread = "
				+ Thread.currentThread().getName() + " , param = " + param);
		System.out.println(
				"MockSimpleAction_" + hashCode() + " >>> start perform: time = " + Schedulers.getCurrentTime());
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		dispatchResult(RESULT_SUCCESS, 0);
		System.out
				.println("MockSimpleAction_" + hashCode() + " >>> end perform: time = " + Schedulers.getCurrentTime());
	}

	@Override
	protected void onCancel(LogicParam lp) {
		System.out.println("MockSimpleAction_" + hashCode() + " >>> called onCancel(): " + lp);
	}

}
