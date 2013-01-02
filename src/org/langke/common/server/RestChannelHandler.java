package org.langke.common.server;
 
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.langke.common.Config;
import org.langke.common.CostTime;
import org.langke.common.server.resp.ErrorResp;
import org.langke.common.server.resp.Resp;
import org.langke.util.logging.ESLogger;
import org.langke.util.logging.Loggers;

import com.alibaba.fastjson.JSONException;


public class RestChannelHandler extends SimpleChannelHandler {
	
	private static ESLogger log = Loggers.getLogger(RestChannelHandler.class);
	private static final String CONTENT_TYPE = "application/json;charset=utf-8";
	private final PathTrie<Handler> getHandlers = new PathTrie<Handler>();
	private final PathTrie<Handler> postHandlers = new PathTrie<Handler>();
	private final PathTrie<Handler> putHandlers = new PathTrie<Handler>();
	private final PathTrie<Handler> deleteHandlers = new PathTrie<Handler>();
	
	public RestChannelHandler(){
	}

	public void messageReceived(ChannelHandlerContext ctx, final MessageEvent me) throws Exception {
		CostTime cost = new CostTime();
		cost.start();
		final HttpRequest httpRequest = (HttpRequest) me.getMessage();
		NettyHttpRequest request = new NettyHttpRequest(httpRequest);
		Channel channel = me.getChannel();
		final Handler handler = getHandler(request);
		Resp response = null;
		int code = 200,content_length;
		if (handler == null) {
			code = 404;
			response = new ErrorResp("No handler found for uri ["+ request.uri() + "] and method [" + request.method() + "]",code);
			content_length = sendResponse(request, response, httpRequest, channel,cost);
		}else{
			try {
				response = handler.handleRequest(request);
			} catch (JSONException e){
				code = 400;
				response = new ErrorResp(e, 400);
			} catch (Exception e) {
				code = 500;
				response = new ErrorResp(e);
			}finally{
				content_length = sendResponse(request, response, httpRequest, channel,cost);
			}
		}
		String referer = request.header("Referer");
		String userAgent = request.header("User-Agent");
		String content = request.contentAsString();
		if(content!=null && content.length()>0 && content.indexOf('\n')!=-1)
			content = content.replace("\n", "");
		StringBuffer sb = new StringBuffer();
		sb.append(userAgent==null?"":userAgent).append(',');
		sb.append(referer==null?"":referer).append(',');
		sb.append(content);
		if(cost.cost()>100)
			log.warn("{} {} {} {} {} {} {}",channel.getRemoteAddress().toString(),httpRequest.getMethod().getName(),httpRequest.getUri(),code,content_length,"\""+sb.toString()+"\"",cost.cost());
		else
			log.info("{} {} {} {} {} {} {}",channel.getRemoteAddress().toString(),httpRequest.getMethod().getName(),httpRequest.getUri(),code,content_length,"\""+sb.toString()+"\"",cost.cost());

	}
	
	@SuppressWarnings("unused")
	public int sendResponse(NettyHttpRequest nettyRequest, Resp response, HttpRequest httpRequest, Channel channel,CostTime cost){
		 // Decide whether to close the connection or not.
        boolean http10 = httpRequest.getProtocolVersion().equals(HttpVersion.HTTP_1_0);
        boolean close = HttpHeaders.Values.CLOSE.equalsIgnoreCase(httpRequest.getHeader(HttpHeaders.Names.CONNECTION)) ||
                        (http10 && !HttpHeaders.Values.KEEP_ALIVE.equalsIgnoreCase(httpRequest.getHeader(HttpHeaders.Names.CONNECTION)));
        
        // Build the response object.
        HttpResponseStatus status = getStatus(response.getRespData().getCode());
        org.jboss.netty.handler.codec.http.HttpResponse resp;
        if (http10) {
            resp = new DefaultHttpResponse(HttpVersion.HTTP_1_0, status);
            if (!close) {
                resp.addHeader(HttpHeaders.Names.CONNECTION, "Keep-Alive");
            }
        } else {
            resp = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
        }
        response.getRespData().setTime(cost.cost());
        // Convert the response content to a ChannelBuffer.
        ChannelFutureListener releaseContentListener = null;
        ChannelBuffer buf = response.toJson();
        resp.setContent(buf);
        resp.setHeader(HttpHeaders.Names.CONTENT_TYPE, CONTENT_TYPE);
        int content_length = buf.readableBytes();
        resp.setHeader(HttpHeaders.Names.CONTENT_LENGTH, String.valueOf(content_length));
        resp.setHeader(HttpHeaders.Names.SERVER,Config.get().get("server.name", "rest-netty-server"));
        resp.setHeader("Via", NetworkUtils.getLocalAddress().getHostAddress());

        // Write the response.
        ChannelFuture future = channel.write(resp);
		
        if (releaseContentListener != null) {
            future.addListener(releaseContentListener);
        }
        // Close the connection after the write operation is done if necessary.
        if (close) {
            future.addListener(ChannelFutureListener.CLOSE);
        }
        return content_length;
	}
	
	 private HttpResponseStatus getStatus(int status) {
        switch (status) {
            case 100:
                return HttpResponseStatus.CONTINUE;
            case 101:
                return HttpResponseStatus.SWITCHING_PROTOCOLS;
            case 200:
                return HttpResponseStatus.OK;
            case 201:
                return HttpResponseStatus.CREATED;
            case 202:
                return HttpResponseStatus.ACCEPTED;
            case 203:
                return HttpResponseStatus.NON_AUTHORITATIVE_INFORMATION;
            case 204:
                return HttpResponseStatus.NO_CONTENT;
            case 205:
                return HttpResponseStatus.RESET_CONTENT;
            case 206:
                return HttpResponseStatus.PARTIAL_CONTENT;
            case 207:
                // no status for this??
                return HttpResponseStatus.INTERNAL_SERVER_ERROR;
            case 300:
                return HttpResponseStatus.MULTIPLE_CHOICES;
            case 301:
                return HttpResponseStatus.MOVED_PERMANENTLY;
            case 302:
                return HttpResponseStatus.FOUND;
            case 303:
                return HttpResponseStatus.SEE_OTHER;
            case 304:
                return HttpResponseStatus.NOT_MODIFIED;
            case 305:
                return HttpResponseStatus.USE_PROXY;
            case 307:
                return HttpResponseStatus.TEMPORARY_REDIRECT;
            case 400:
                return HttpResponseStatus.BAD_REQUEST;
            case 401:
                return HttpResponseStatus.UNAUTHORIZED;
            case 402:
                return HttpResponseStatus.PAYMENT_REQUIRED;
            case 403:
                return HttpResponseStatus.FORBIDDEN;
            case 404:
                return HttpResponseStatus.NOT_FOUND;
            case 405:
                return HttpResponseStatus.METHOD_NOT_ALLOWED;
            case 406:
                return HttpResponseStatus.NOT_ACCEPTABLE;
            case 407:
                return HttpResponseStatus.PROXY_AUTHENTICATION_REQUIRED;
            case 408:
                return HttpResponseStatus.REQUEST_TIMEOUT;
            case 409:
                return HttpResponseStatus.CONFLICT;
            case 410:
                return HttpResponseStatus.GONE;
            case 411:
                return HttpResponseStatus.LENGTH_REQUIRED;
            case 412:
                return HttpResponseStatus.PRECONDITION_FAILED;
            case 413:
                return HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE;
            case 414:
                return HttpResponseStatus.REQUEST_URI_TOO_LONG;
            case 415:
                return HttpResponseStatus.UNSUPPORTED_MEDIA_TYPE;
            case 416:
                return HttpResponseStatus.REQUESTED_RANGE_NOT_SATISFIABLE;
            case 417:
                return HttpResponseStatus.EXPECTATION_FAILED;
            case 422:
                return HttpResponseStatus.BAD_REQUEST;
            case 423:
                return HttpResponseStatus.BAD_REQUEST;
            case 424:
                return HttpResponseStatus.BAD_REQUEST;
            case 500:
                return HttpResponseStatus.INTERNAL_SERVER_ERROR;
            case 501:
                return HttpResponseStatus.NOT_IMPLEMENTED;
            case 502:
                return HttpResponseStatus.BAD_GATEWAY;
            case 503:
                return HttpResponseStatus.SERVICE_UNAVAILABLE;
            case 504:
                return HttpResponseStatus.GATEWAY_TIMEOUT;
            case 505:
                return HttpResponseStatus.HTTP_VERSION_NOT_SUPPORTED;
            default:
                return HttpResponseStatus.INTERNAL_SERVER_ERROR;
        }
	}
	
	public void registerHandler(HttpMethod method, String path, Handler handler) {
		if (method == HttpMethod.GET) {
			getHandlers.insert(path, handler);
		} else if (method == HttpMethod.POST) {
			postHandlers.insert(path, handler);
		} else if (method == HttpMethod.PUT) {
			putHandlers.insert(path, handler);
		} else if (method == HttpMethod.DELETE) {
			deleteHandlers.insert(path, handler);
		} else {
			throw new RuntimeException("HttpMethod is not supported");
		}
	}
	
	private Handler getHandler(NettyHttpRequest request) {
		String path = request.path();
		HttpMethod method = request.method();
		if (method == HttpMethod.GET) {
			return getHandlers.retrieve(path, request.params());
		} else if (method == HttpMethod.POST) {
			return postHandlers.retrieve(path, request.params());
		} else if (method == HttpMethod.PUT) {
			return putHandlers.retrieve(path, request.params());
		} else if (method == HttpMethod.DELETE) {
			return deleteHandlers.retrieve(path, request.params());
		} else {
			return null;
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		if (log.isTraceEnabled())
			log.trace("Connection exceptionCaught:{}", e.getCause().toString());
		e.getChannel().close();
	}
}
