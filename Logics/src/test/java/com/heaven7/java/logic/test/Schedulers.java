package com.heaven7.java.logic.test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.WeakHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.heaven7.java.logic.Scheduler;

public class Schedulers {
	
	public static final DateFormat DF = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
	public static final Scheduler ASYNC = new AsyncScheduler();
	public static final Scheduler DEFAULT = new DefaultScheduler();
	public static final Scheduler GROUP_ASYNC = new GroupAsyncScheduler();

	public static Scheduler newAsyncScheduler(){
		return new AsyncScheduler();
	}
	
	public static String getCurrentTime(){
		return DF.format(new Date(System.currentTimeMillis()));
	}
	
    private static class GroupAsyncScheduler implements Scheduler{
		
		final ScheduledExecutorService pool = Executors.newScheduledThreadPool(5);
		final WeakHashMap<Runnable,Future<?>> map = new WeakHashMap<Runnable,Future<?>>();
		
		@Override
		public void postDelay(long delay, Runnable task) {
			map.put(task, pool.schedule(task, delay, TimeUnit.MILLISECONDS));
		}

		@Override
		public void post(Runnable task) {
			map.put(task, pool.submit(task));
		}
		@Override
		public void remove(Runnable task) {
			Future<?> future = map.get(task);
			if(future != null){
				future.cancel(true);
			}
		}
		
	}
	
	private static class AsyncScheduler implements Scheduler{
		
		final ScheduledExecutorService pool = Executors.newScheduledThreadPool(1);
		final WeakHashMap<Runnable,Future<?>> map = new WeakHashMap<Runnable,Future<?>>();
		
		@Override
		public void postDelay(long delay, Runnable task) {
			map.put(task, pool.schedule(task, delay, TimeUnit.MILLISECONDS));
		}

		@Override
		public void post(Runnable task) {
			map.put(task, pool.submit(task));
		}
		@Override
		public void remove(Runnable task) {
			Future<?> future = map.get(task);
			if(future != null){
				future.cancel(true);
			}
		}
		
	}
	private static class DefaultScheduler implements Scheduler{

		@Override
		public void postDelay(long delay, Runnable task) {
			try {
				Thread.sleep(delay);
				task.run();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void post(Runnable task) {
			task.run();
		}

		@Override
		public void remove(Runnable task) {
			
		}
		
	}
	
}
