package org.langke.common.server;

/*
 * @copyright (c) langke 2011 
 * @author langke    2011-5-1 
 */
public interface Server {
	
	public void init();

	public void start();

	public void stop();

	public String serverName();
}
