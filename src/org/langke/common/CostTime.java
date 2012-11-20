package org.langke.common;

/*
 * @copyright (c) langke 2011 
 * @author langke    Aug 11, 2011 
 */

public class CostTime {

	private transient long start;
	
	public void start(){
		this.start = System.currentTimeMillis();
	}
	
	public long cost(){
		return System.currentTimeMillis() - start;
	}
}
