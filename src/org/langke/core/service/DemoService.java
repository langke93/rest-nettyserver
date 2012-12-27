package org.langke.core.service;

import org.langke.common.server.NettyHttpRequest;
import org.langke.common.server.resp.RespData;

public interface DemoService extends IService{

	public RespData test(NettyHttpRequest request);
	public RespData create(NettyHttpRequest request);
	public RespData select(NettyHttpRequest request);
	public RespData insert(NettyHttpRequest request);
	public RespData update(NettyHttpRequest request);
	public RespData mybatis(NettyHttpRequest request);

}
