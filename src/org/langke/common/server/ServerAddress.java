package org.langke.common.server;

import java.io.Serializable;

/*
 * @copyright (c) langke 2011 
 * @author langke    2011-5-1 
 */
public class ServerAddress implements Serializable {
	
	private static final long serialVersionUID = 5142207361335126957L;
	private String host;
	private int port;

	public ServerAddress(String host, int port){
		this.host = host;
		this.port = port;
	}
	
	public ServerAddress(String addressAndPort) {
		int pIndex = addressAndPort.indexOf(':');
		if (pIndex < 0) {
			throw new IllegalArgumentException("Must have a : in addressAndPort,got:" + addressAndPort);
		}
		this.host = addressAndPort.substring(0, pIndex);
		this.port = Integer.parseInt(addressAndPort.substring(pIndex + 1));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ServerAddress)) {
			return false;
		}
		ServerAddress n = (ServerAddress) obj;
		return n.host.equals(this.host) && n.port == this.port;
	}

	@Override
	public int hashCode() {
		return (host+":"+port).hashCode();
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	@Override
	public String toString() {
		return this.host + ":" + this.port;
	}
	
	public static void main(String[] p) {
		System.out.println(new ServerAddress("192.178.2.4:12").toString());
	}

}
