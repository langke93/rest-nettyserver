package org.langke.core.server;

import java.util.concurrent.TimeUnit;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.ChannelUpstreamHandler;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;
import org.jboss.netty.handler.stream.ChunkedWriteHandler;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.handler.timeout.WriteTimeoutHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;
import org.langke.common.Config;
import org.langke.common.server.BaseNioServer;
import org.langke.common.server.Handler;
import org.langke.common.server.NettyHttpRequest;
import org.langke.common.server.RestChannelHandler;
import org.langke.common.server.ServerAddress;
import org.langke.common.server.resp.Resp;
import org.langke.core.handler.DemoHandler;


/**
 * 
 * @author langke
 * @date 2012-9-11
 *
 */
public class RestNettyServer extends BaseNioServer {

	private RestChannelHandler restHandler = null;

	public RestNettyServer() {
		restHandler = new RestChannelHandler();
		restHandler.registerHandler(HttpMethod.GET, "/", new Handler() {
			@Override
			public Resp handleRequest(NettyHttpRequest request) {
				return new Resp("rest netty server 0.1");
			}
		});
		DemoHandler demo = new DemoHandler();
		restHandler.registerHandler(HttpMethod.GET, "/demo/{label}", demo);
		restHandler.registerHandler(HttpMethod.POST, "/demo/{label}", demo);
		
	}

	@Override
	protected ChannelPipelineFactory getChannelPipelineFactory() {
		final Timer timer = new HashedWheelTimer(Config.get().getInt("readTimeout", 60), TimeUnit.SECONDS);
		return new ChannelPipelineFactory() {
			public ChannelPipeline getPipeline() throws Exception {
				// Create a default pipeline implementation.
				ChannelPipeline pipeline = Channels.pipeline();
				pipeline.addLast("readTimeout", new ReadTimeoutHandler(timer, Config.get().getInt("readTimeout", 60)));
				pipeline.addLast("writeTimeout", new WriteTimeoutHandler(timer, Config.get().getInt("writeTimeout", 60)));
				pipeline.addLast("decoder", new HttpRequestDecoder());
				pipeline.addLast("encoder", new HttpResponseEncoder());
				pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
				pipeline.addLast("chunkedWriter", new ChunkedWriteHandler());
				pipeline.addLast("query", restHandler);
				return pipeline;
			}
		};
	}

	@Override
	protected int defaultPort() {
		return 9005;
	}

	@Override
	public String serverName() {
		return Config.get().get("server.name", "rest-netty-server");
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public void start() {
		super.start();
	}

	protected ServerAddress getServerAddress() {
		String listen = Config.get().get("server.listen");
		String address = Config.get().get("server.listen.address");
		String port = Config.get().get("server.listen.port");
		if(listen != null){
			return new ServerAddress(listen);
		}
		return new ServerAddress(address, (port!=null?Integer.parseInt(port):defaultPort()));
	}

	@Override
	protected ChannelUpstreamHandler finalChannelUpstreamHandler() {
		return null;
	}

	public static void main(String[] args) {
		SpringApplicationContext.getInstance();
		RestNettyServer o = new RestNettyServer();
		o.init();
		o.start();
	}
}
