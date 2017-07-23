package com.heaven7.java.logic.test;

import java.util.List;

import com.heaven7.java.logic.LogicRunner;

public class LogRunner implements LogicRunner{
	
	private final String superMethod;
	
	public LogRunner(String superMethod) {
		super();
		this.superMethod = superMethod;
	}

	@Override
	public void run(int tag, Object result, List<?> results) {
		Logger.i("LogRunner", superMethod, "all Task done! thread = " + 
	           Thread.currentThread().getName());
	}

}
