package com.heaven7.java.logic.test;

import com.heaven7.java.base.util.DefaultPrinter;
import com.heaven7.java.logic.AbstractLogicAction;
import com.heaven7.java.logic.LogicParam;
import com.heaven7.java.logic.LogicResult;
import com.heaven7.java.logic.test.MockLogicAction2.FromTo;

public class MockSequenceAction extends AbstractLogicAction {
	
	private final boolean mockFailed ;
	
	public MockSequenceAction(boolean mockFailed ) {
		super(false);
		this.mockFailed = mockFailed;
	}

	@Override
	protected void performImpl(int tag, int count, LogicParam param) {
		DefaultPrinter.getDefault().debug(TAG, "performImpl", "tag = "+ tag + " ,last perform result = " + param.getLastResult());
		
		int lastVal = (Integer) (param.getLastResult() !=null ? param.getLastResult() : 0);
		
		FromTo data = (FromTo) param.getData();
		int sum = 0;
		for(int i = data.from ; i <= data.to ; i++){
			sum += i;
		}
		if(mockFailed){
			if(sum > 5000000){
				dispatchResult(tag, new LogicResult(RESULT_FAILED, 0));
			}else{
			    dispatchResult(tag, new LogicResult(RESULT_SUCCESS, sum + lastVal));
			}
		}else{
			dispatchResult(tag, new LogicResult(RESULT_SUCCESS, sum + lastVal));
		}
	}
}
