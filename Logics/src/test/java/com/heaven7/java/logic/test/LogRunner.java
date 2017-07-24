package com.heaven7.java.logic.test;

import java.util.List;

import com.heaven7.java.logic.LogicResultListener;
import com.heaven7.java.logic.LogicTask;

public class LogRunner implements LogicResultListener{
	
	private final String superMethod;
	
	public LogRunner(String superMethod) {
		super();
		this.superMethod = superMethod;
	}

	@Override
	public void onFailed(List<LogicTask> failedTask, Object lastResult, List<?> results) {
		Logger.i("LogRunner", superMethod, "FAILED ! failedTask = " + failedTask + " ,already results = " + 
	              results + " ,thread = " + Thread.currentThread().getName());
	}

	@Override
	public void onSuccess(LogicTask lastTask, Object lastResult, List<?> results) {
		Logger.i("LogRunner", superMethod, "SUCCESS! all Task done! results = "+ results + " ,thread = " + 
		           Thread.currentThread().getName());
	}


}
