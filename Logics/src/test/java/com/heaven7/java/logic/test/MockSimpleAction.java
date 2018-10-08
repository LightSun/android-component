package com.heaven7.java.logic.test;

import com.heaven7.java.logic.LogicManager;
import com.heaven7.java.logic.LogicParam;
import com.heaven7.java.logic.LogicResult;
import com.heaven7.java.logic.SimpleLogicAction;

public class MockSimpleAction extends SimpleLogicAction {

	public MockSimpleAction() {
	}
	
	@Override
	protected void cancelImpl(int tag) {
		System.out.println("MockSimpleAction_" + hashCode() + " >>> called cancelImpl()");
	}

	@Override
	protected void performImpl(LogicManager lm, int tag, int count, LogicParam param) {
		System.out.println("MockSimpleAction_" + hashCode() + " >>> start perform: thread = "
				+ Thread.currentThread().getName() + " , param = " + param);
		System.out.println(
				"MockSimpleAction_" + hashCode() + " >>> start perform: time = " + Schedulers.getCurrentTime());
		try {
			Thread.sleep(3000);
			dispatchResult(tag, LogicResult.SUCCESS);
			System.out
					.println("MockSimpleAction_" + hashCode() + " >>> end perform: time = " + Schedulers.getCurrentTime());
		} catch (InterruptedException e) {
			dispatchResult(tag, LogicResult.SUCCESS);
			e.printStackTrace();
		}
	}
	
	@Override
	protected void onCancel(int tag, LogicParam param, LogicResult result) {
		System.out.println("MockSimpleAction_" + hashCode() + " >>> called onCancel(): " + param);
	}
	
}
