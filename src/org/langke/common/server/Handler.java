package org.langke.common.server;

import org.langke.common.server.resp.Resp;


/*
 * @copyright (c) langke 2011 
 * @author langke    2011-5-1 
 */
public interface Handler {

	 public Resp handleRequest(NettyHttpRequest request);
	 
}
