package com.heaven7.java.logic.test;

import com.heaven7.java.base.util.DefaultPrinter;
import com.heaven7.java.logic.AbstractLogicAction;
import com.heaven7.java.logic.LogicParam;
import com.heaven7.java.logic.LogicResult;

public class MockLogicAction2 extends AbstractLogicAction {

	public MockLogicAction2() {
		super(false);
	}

	@Override
	protected void performImpl(int tag, int count, LogicParam param) {
		//last result may used to sequence task.
		//DefaultPrinter.getDefault().debug(TAG, "performImpl", "last perform result = " + param.getLastResult());
		DefaultPrinter.getDefault().debug(TAG, "performImpl", "tag = "+ tag + " ," + param.getData());
		
		FromTo data = (FromTo) param.getData();
		int sum = 0;
		for(int i = data.from ; i <= data.to ; i++){
			sum += i;
		}
		//dispatchResult(tag, new LogicResult(RESULT_SUCCESS, sum));
		if(sum > 5000000){
			dispatchResult(tag, new LogicResult(RESULT_FAILED, 0));
		}else{
		    dispatchResult(tag, new LogicResult(RESULT_SUCCESS, sum));
		}
	}

	public static class FromTo{
		final int from;
		final int to; // exclude
		public FromTo(int from, int to) {
			super();
			this.from = from;
			this.to = to;
		}
		@Override
		public String toString() {
			return "FromTo [from=" + from + ", to=" + to + "]";
		}
		
	}
}
