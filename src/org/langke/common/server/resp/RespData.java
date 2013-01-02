package org.langke.common.server.resp;

import java.io.Serializable;

public class RespData implements Serializable{
	private static final long serialVersionUID = 2018006821325666212L;
	private short code = 200;
	private long time;
	private Object content;
	private String msg;
	public short getCode() {
		return code;
	}
	public void setCode(short code) {
		this.code = code;
	}
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	public Object getContent() {
		return content;
	}
	public void setContent(Object content) {
		this.content = content;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	
}
