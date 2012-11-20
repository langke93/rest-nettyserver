package org.langke.core.server;

import java.io.File;

import org.langke.util.logging.ESLogger;
import org.langke.util.logging.Loggers;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
 

public class SpringApplicationContext {
	private ESLogger log = Loggers.getLogger(SpringApplicationContext.class);
	
	private static ApplicationContext ctx;
	private static SpringApplicationContext instance=new SpringApplicationContext();
	
	public  static SpringApplicationContext getInstance(){
		return instance;
	}
	
	private SpringApplicationContext() {
		initCtx();
	}
	
	private void initCtx(){
		if(ctx == null) {
			String location = null;
			if(System.getProperty("os.name").toLowerCase().contains("windows")){
				location = "conf/applicationContext.xml";
			}else{
				location = "../conf/applicationContext.xml";
			}
			File file = new File(location);
			log.info("applicationContext: [{}]", file.getAbsolutePath() );
	    	ctx = new FileSystemXmlApplicationContext(location);
	     }  
	}
	
	public Object  getService(String serviceName){
		return ctx.getBean(serviceName);
	}
	public ApplicationContext getApplicationContext(){
		return ctx;
	}
 
}
