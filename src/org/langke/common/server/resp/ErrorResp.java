package org.langke.common.server.resp;

import java.nio.charset.Charset;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.langke.common.CommonException;
import org.langke.common.Strings;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;


public class ErrorResp extends Resp {
	private static final long serialVersionUID = 34687954757L;
	private RespData respData = new RespData();
	
	public RespData getRespData() {
		return respData;
	}

	public void setRespData(RespData respData) {
		this.respData = respData;
	}

	@Override
	public ChannelBuffer toJson() {
		return ChannelBuffers.copiedBuffer(JSONObject.toJSONString(respData, SerializerFeature.BrowserCompatible), Charset.forName("UTF-8"));
	}

	public ErrorResp(String msg) {
		respData.setMsg(Strings.quote(msg));
		respData.setCode((short) 500);
	}
	
	public ErrorResp(String msg, int errorCode) {
		respData.setMsg(Strings.quote(msg));
		respData.setCode((short) errorCode);
	}

	public ErrorResp(Throwable t) {
		this(Strings.throwableToString(t));
		if (t instanceof CommonException) {
			respData.setMsg(((CommonException) t).getMessage());
			respData.setCode((short)((CommonException) t).errorCode);
		}
	}

	public ErrorResp(String errors, Throwable t) {
		if (t instanceof CommonException) {
			respData.setMsg(errors + "\n" + Strings.throwableToString(t));
			respData.setCode((short)((CommonException) t).errorCode);
		}
	}

	public ErrorResp(Throwable t, int errorCode) {
		this(Strings.throwableToString(t), errorCode);
	}

}
