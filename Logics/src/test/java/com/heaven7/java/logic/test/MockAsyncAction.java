package com.heaven7.java.logic.test;

import com.heaven7.java.logic.AbstractLogicAction;
import com.heaven7.java.logic.LogicParam;
import com.heaven7.java.logic.Scheduler;

public class MockAsyncAction extends AbstractLogicAction{
	
	private Thread mThread;

	public MockAsyncAction(boolean wantCount) {
		super(wantCount);
	}

	@Override
	protected void performImpl(final int tag, int count, LogicParam param) {
		mThread = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				dispatchResult(RESULT_SUCCESS, tag);
			}
		});
		mThread.start();
	}

	@Override
	protected void cancelImpl(int tag, boolean immediately) {
		mThread.interrupt();
	}

	@Override
	public void observeOn(int tag, Scheduler scheduler) {
		// TODO Auto-generated method stub
		
	}

}
