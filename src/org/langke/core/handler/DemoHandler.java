package org.langke.core.handler;

import java.lang.reflect.Method;

import org.langke.common.server.Handler;
import org.langke.common.server.NettyHttpRequest;
import org.langke.common.server.resp.Resp;
import org.langke.common.server.resp.RespData;
import org.langke.core.server.SpringApplicationContext;
import org.langke.core.service.IService;

public class DemoHandler implements Handler{

	
	private IService service = (IService) SpringApplicationContext.getInstance().getService("demoService");

	@Override
	public Resp handleRequest(NettyHttpRequest request) {
		Method method;
		RespData data;
		try {
			method = service.getClass().getMethod(request.param("label"), NettyHttpRequest.class);
			data = (RespData) method.invoke(service, request);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return new Resp(data);
	}

}
