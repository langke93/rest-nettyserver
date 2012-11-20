package org.langke.util;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.langke.common.StrIntBag;
import org.langke.common.Strings;
import org.langke.util.logging.ESLogger;
import org.langke.util.logging.Loggers;


/*
 * @copyright (c) langke 2011 
 * @author langke    Aug 9, 2011 
 */
public class HttpClientUtil {

	private static ESLogger log = Loggers.getLogger(HttpClientUtil.class);
	private static HttpClient client = null;
	private static MultiThreadedHttpConnectionManager conn_manager = null;
	private static HttpConnectionManagerParams cmanager_params = null;
	static{
		if(client==null) init();
	}
	private static void init(){
		if (conn_manager == null)
			conn_manager = new MultiThreadedHttpConnectionManager();
		if (cmanager_params == null)
			cmanager_params = new HttpConnectionManagerParams();
		// config the HTTP client visit performance.
		cmanager_params.setDefaultMaxConnectionsPerHost(90);
		cmanager_params.setMaxTotalConnections(256);
		cmanager_params.setConnectionTimeout(6000);
		conn_manager.setParams(cmanager_params);
		if (client == null)
			client = new HttpClient(conn_manager);
	}
	
	public static StrIntBag execute(String uri, String requestBody){
		PostMethod post = null;
		StrIntBag bag = new StrIntBag();
		try {
			post = new PostMethod(uri);
			post.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
			post.getParams().setParameter(HttpMethodParams.USE_EXPECT_CONTINUE, false);
			if(requestBody != null){
				RequestEntity reqEntity = new StringRequestEntity(requestBody, "application/json", "UTF-8");
				post.setRequestEntity(reqEntity);
			}
			int status = client.executeMethod(post);
			String resp = post.getResponseBodyAsString();
			bag._str = resp;
			bag._int = status;
			return bag;
		} catch (Exception e) {
			log.error("{}", Strings.throwableToString(e));
		} finally{
			if(post != null){
				post.releaseConnection();
			}
		}
		return null;
	}
	/**
	 * 失败后，重试3次
	 * @param uri
	 * @param json
	 * @return
	 */
	public static StrIntBag tryExecute(String uri, String json){
		StrIntBag bag = execute(uri, json);
		if(bag != null){
			return bag;
		}
		int tryCount = 3;
		while(bag == null && (tryCount--) > 0 ){
			bag = execute(uri, json);
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				log.error("{}", Strings.throwableToString(e));
			}
		}
		return bag;
	}
	
	public static StrIntBag executeGet(String url){
		GetMethod get = null;
		StrIntBag bag = new StrIntBag();
		try {
			get = new GetMethod(url);
			get.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"UTF-8");
			get.getParams().setParameter(HttpMethodParams.USE_EXPECT_CONTINUE, false);
			int status = client.executeMethod(get);
			String resp = get.getResponseBodyAsString();
			bag._str = resp;
			bag._int = status;
			return bag;
		} catch (Exception e) {
			log.error("{}", Strings.throwableToString(e));
		} finally{
			if(get != null){
				get.releaseConnection();
			}
		}
		return null;
	}
	
	public static StrIntBag tryExecuteGet(String url){
		StrIntBag bag = executeGet(url);
		if(bag != null){
			return bag;
		}
		int tryCount = 3;
		while(bag == null && (tryCount--) > 0 ){
			bag = executeGet(url);
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				log.error("{}", Strings.throwableToString(e));
			}
		}
		return bag;
	}
	
	public static void main(String[] args) {
		StrIntBag resp = execute("http://10.10.10.201:9200/item/_search","{\"query\":\"地板\"}");
		System.out.println(resp._str);
	}
	
}
