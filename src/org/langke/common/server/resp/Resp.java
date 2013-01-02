package org.langke.common.server.resp;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.langke.common.Strings;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

public class Resp implements Serializable {
	
	private static final long serialVersionUID = 34543654757L;
	protected transient List<String> warningList = null;
	private RespData respData = new RespData();
	public Resp() {
	}	
	public Resp(String content) {
		super();
		respData.setContent(content);
	}
	public Resp(RespData data) {
		respData = data;
	}
	
	public RespData getRespData() {
		return respData;
	}
	public void setRespData(RespData respData) {
		this.respData = respData;
	}
	public ChannelBuffer body(){
		return ChannelBuffers.copiedBuffer(JSONObject.toJSONString(respData.getContent(), SerializerFeature.BrowserCompatible), Charset.forName("UTF-8"));
	}

	public ChannelBuffer toJson() {
		return ChannelBuffers.copiedBuffer(JSONObject.toJSONString(respData, SerializerFeature.BrowserCompatible), Charset.forName("UTF-8"));
	}
	
	public void addWarning(String s) {
		if (s == null || s.trim().equals("")) {
			return;
		}
		if (warningList == null) {
			warningList = new ArrayList<String>(4);
		}
		warningList.add(s);
	}

	protected void addWarnings(StringBuilder sb) {
		if (this.warningList != null && warningList.size() > 0) {
			Strings.quoteSafeJson(sb, "warnings");
			sb.append(":[");
			for (String s : warningList) {
				if (s != null) {
					Strings.quoteSafeJson(sb, s);
					sb.append(",");
				}
			}
			sb.setCharAt(sb.length() - 1, ']');
		}
	}

	public String toString() {
		return this.toJson().toString(Charset.forName("UTF-8"));
	}
	 
}
