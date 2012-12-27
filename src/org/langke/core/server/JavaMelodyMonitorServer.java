package org.langke.core.server;

import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;

import org.langke.common.Config;
import org.langke.util.logging.ESLogger;
import org.langke.util.logging.Loggers;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.jetty.servlet.Context;
import org.mortbay.jetty.servlet.DefaultServlet;
import org.mortbay.jetty.servlet.FilterHolder;
import org.mortbay.thread.QueuedThreadPool;


/**
 * 启动一个jetty容器，结合javamelody用于监控应用性能
 * @author langke
 * 2012-12-21
 */
public class JavaMelodyMonitorServer {
	private ESLogger log = Loggers.getLogger(JavaMelodyMonitorServer.class);
	Server webServer;
	/**
	 * 
	 * @param serverName 应用名称
	 * @param host	绑定的IP地址
	 * @param serverPort	应用端口，jetty启动的端口默认会在此基础上加1000,如果配置文件有配置jetty.listen.port则配置优先
	 */
	public JavaMelodyMonitorServer(String serverName,String host,int serverPort) {
		init(serverName, host, serverPort);
		start();
		final JavaMelodyMonitorServer server = this;
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				try {
					log.info("shutdown mointorServer:{}", server);
					server.stop();
				} catch (Exception e) {
					log.error("run main stop error!", e);
				}
			}

		});
	}
	
	private void init(String serverName,String host,int serverPort){
		int defaultValue = serverPort+1000;
		int port = Config.get().getInt("jetty.listen.port", defaultValue);
    	Connector connector = new SocketConnector();
	    webServer = new Server();
	    QueuedThreadPool pool = new QueuedThreadPool();
	    pool.setMinThreads(Config.get().getInt("jetty.pool.MinThread", 3));
	    pool.setMaxThreads(Config.get().getInt("jetty.pool.MaxThread", 32));
	    String server = host;
	    pool.setName(serverName+"-monitor");
	    pool.setDaemon(true);
	    webServer.setThreadPool(pool);
	    connector = new SocketConnector();

	    connector.setPort(port);
	    connector.setHost(server);
	    connector.setMaxIdleTime(60000); // 1 min
	    webServer.addConnector(connector);
	    
        ContextHandlerCollection col = new ContextHandlerCollection();
        Context context = new Context(col, "/", Context.SESSIONS);
        ResourceHandler resourceHandler = new ResourceHandler();
        webServer.setHandlers(new Handler[] {col,resourceHandler });
        webServer.addHandler(context);
	      // Set Java Melody storage Directory
        String user_dir = System.getProperty("user.dir","");
	    System.setProperty("javamelody.storage-directory", user_dir+"/logs/javamelody-"+pool.getName());
	    
	    //add filter
	    Filter monitoringFilter = new net.bull.javamelody.MonitoringFilter();
	    context.addFilter(new FilterHolder(monitoringFilter), "/monitoring", Handler.REQUEST);
	    
	   // Map<String,String> initParams = new HashMap<String,String>();
	   // initParams.put("contextConfigLocation", "classpath:net/bull/javamelody/monitoring-spring.xml,classpath:resource/*.xml,classpath:conf/applicationContext.xml");
	   // context.setInitParams(initParams);

	    //add listener
	    //EventListener listener = new ContextLoaderListener();
	    //context.addEventListener(listener);
	    
	    context.addServlet(DefaultServlet.class, "/*");
	}

    public void start(){
        try{
            webServer.join();
            webServer.start();
        }catch (Exception e){
            log.error("Error starting httpserver", e);
        }
    }

    public void stop(){
        try{
            webServer.stop();
            webServer.destroy();
        }catch (Exception e){
            log.error("Error stop httpserver", e);
        }

    }
}
