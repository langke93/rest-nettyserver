package org.langke.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/*
 * @copyright (c) langke 2011 
 * @author langke    2011-5-1 
 */
public class ExecutorFactory {

	public static ScheduledExecutorService scheduledExecutor = Executors.newSingleThreadScheduledExecutor();
	public static ExecutorService fixedExecutor = Executors.newFixedThreadPool(10);
	public static ExecutorService cachedExecutor = Executors.newCachedThreadPool();
	
}
