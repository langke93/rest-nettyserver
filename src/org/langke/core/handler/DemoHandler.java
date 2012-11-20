package org.langke.core.handler;

import org.langke.common.server.Handler;
import org.langke.common.server.NettyHttpRequest;
import org.langke.common.server.resp.Resp;
import org.langke.common.server.resp.RespData;

public class DemoHandler implements Handler{

	@Override
	public Resp handleRequest(NettyHttpRequest request) {
		RespData data = new RespData();
		data.setContent(request.params());
		return new Resp(data);
	}

}
