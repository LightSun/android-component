package com.heaven7.java.logic.test;

import java.util.ArrayList;
import java.util.List;

import com.heaven7.java.logic.LogicManager;
import com.heaven7.java.logic.LogicParam;
import com.heaven7.java.logic.LogicResultListener;
import com.heaven7.java.logic.LogicTask;
import com.heaven7.java.logic.test.MockLogicAction2.FromTo;

public class MultiplyTasksTest {
	private static final String TAG = "MultiplyTasksTest";
	
	private final LogicManager mLm = new LogicManager();
	private final MockLogicAction2 mAction = new MockLogicAction2();
	
	public static void main(String[] args) {
		MultiplyTasksTest test = new MultiplyTasksTest();
		test.testDistributed();
		//test.testShareToNext();
	}
	
	public void testShareToNext(){
		
		final String method = "testShareToNext";
		int sum = 0;
		for(int i = 1; i <= 10000 ; i++){ //1-10000
			sum += i;
		}
		Logger.i(TAG, method, "Normal Sum = " + sum);
		
		
		final MockSequenceAction action = new MockSequenceAction();
		Logger.i(TAG, method, "start time = " + Schedulers.getCurrentTime());
		List<LogicTask> tasks = new ArrayList<LogicTask>();
		final int step = 999;
		int last = 0;
		for(int i = 0 ; i < 10 ; i++){
			//1-10000
			//1-1000, 1001-2000,2001-3000...
			final int from = last + 1;
			final int to = from + step;
			last = to;
			LogicTask task = LogicTask.of(i, action, LogicParam.create(new FromTo(from, to)))
					.setFlags(LogicManager.FLAG_SHARE_TO_NEXT)
					.schedulerOn(Schedulers.GROUP_ASYNC)
					;
			tasks.add(task);
		}
		
		mLm.performSequence(tasks, -1, new LogicResultListener() {
			
			@Override
			public void onSuccess(LogicTask lastTask, Object lastResult, List<?> results) {
				Logger.i(TAG, method + "_onSuccess", "end time = " + Schedulers.getCurrentTime());
				Logger.i(TAG, method + "_onSuccess", "lastResult = " + lastResult);
			}
			
			@Override
			public void onFailed(List<LogicTask> failedTask, Object lastResult, List<?> results) {
				Logger.w(TAG, method + "_onFailed", "end time = " + Schedulers.getCurrentTime() + " ,failed size = " + failedTask.size());
				Logger.w(TAG, method + "_onFailed", "failedTask = " + failedTask);
			}
		});
	}
	
	public void testDistributed(){
		final String method = "testDistributed";
		int sum = 0;
		for(int i = 1; i <= 10000 ; i++){ //1-10000
			sum += i;
		}
		Logger.i(TAG, method, "Normal Sum = " + sum);
		
		
		Logger.i(TAG, method, "start time = " + Schedulers.getCurrentTime());
		
		List<LogicTask> tasks = new ArrayList<LogicTask>();
		final int step = 999;
		int last = 0;
		for(int i = 0 ; i < 10 ; i++){
			//1-10000
			//1-1000, 1001-2000,2001-3000...
			final int from = last + 1;
			final int to = from + step;
			last = to;
			LogicTask task = LogicTask.of(i, mAction, LogicParam.create(new FromTo(from, to)))
					.setFlags(LogicManager.FLAG_SHARE_TO_POOL)
					.schedulerOn(Schedulers.GROUP_ASYNC)
					;
			tasks.add(task);
		}
		mLm.performParallel(tasks, -1, new LogicResultListener() {
			@SuppressWarnings("unchecked")
			@Override
			public void onSuccess(LogicTask lastTask, Object lastResult, List<?> results) {
				Logger.i(TAG, method + "_onSuccess", "end time = " + Schedulers.getCurrentTime());
				List<Integer> list = (List<Integer>) results;
				Logger.i(TAG, method + "_onSuccess", "results = " + list);
				int sum = 0;
				for(int i = 0, size = list.size() ; i < size ; i++){
					sum += list.get(i).intValue();
				}
				Logger.i(TAG, method + "_onSuccess", "Distributed Sum = " + sum);
			}
			
			@Override
			public void onFailed(List<LogicTask> failedTask, Object lastResult, List<?> results) {
				Logger.w(TAG, method + "_onFailed", "end time = " + Schedulers.getCurrentTime() + " ,failed size = " + failedTask.size());
				Logger.w(TAG, method + "_onFailed", "failedTask = " + failedTask);
			}
		});
	}
	

}
