package com.heaven7.java.logic;

/**
 * Created by heaven7 on 2017/6/19 0019.
 */

public abstract class SimpleLogicCallback extends LogicAction.LogicCallback {
	
	@Override
	public void onLogicResult(LogicAction action, int tag, LogicParam param, LogicResult result) {
		switch (result.getResultCode()){
        case LogicAction.RESULT_SUCCESS:
            onSuccess(action, tag, param, result);
            break;

        case LogicAction.RESULT_FAILED:
            onFailed(action, tag, param, result);
            break;

        default:
            onLogicResultIml(action, tag, param, result);
    }
	}

    protected void onLogicResultIml(LogicAction action, int tag, LogicParam param, LogicResult result) {
		
	}

    protected void onSuccess(LogicAction action, int tag, LogicParam param, LogicResult result) {

    }
    protected void onFailed(LogicAction action, int tag, LogicParam param, LogicResult result) {

    }
}
