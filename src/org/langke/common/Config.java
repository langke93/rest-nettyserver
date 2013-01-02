package org.langke.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Config
{
	public static Logger log = LoggerFactory.getLogger(Config.class);
	public static String getConfigDir(){
    	String configDir = null;
    	String userDir = System.getProperty("user.dir");
    	if(System.getProperty("os.name").toLowerCase().contains("windows")){
    		configDir = userDir+"/conf";
    	}else{
    		configDir = userDir+"/../conf";
    	}
    	return configDir;
    }
	public static String getConfigPath(){
    	return getConfigDir()+"/config.properties";
    }
    private static final Config CONFIG = new Config()
    {
        Properties properties = new Properties();
        boolean modified = false;
        private final File cf = new File(getConfigPath());
    	long time;
		{
	        try{
	            if (!cf.exists()){
	                cf.getParentFile().mkdirs();
	                cf.createNewFile();
	            }
	            //当进程关闭，如果properties有修改，则写入文件
	            Runtime.getRuntime().addShutdownHook(new Thread("store-config"){
	                    public void run(){
	                        try{
	                            if(modified){
	                                boolean autoUpdate = properties.containsKey("autoUpdate");
	                                if (autoUpdate){
	                                    FileOutputStream fos = new FileOutputStream(cf);
	                                    properties.store(fos, "add an <autoUpdate> key to auto update config form default values");
	                                    fos.close();
	                                }
	                            }
	                        }catch (Exception ex){
	                            log.warn("store config", ex);
	                        }
	                    }
	                });
	            properties.load(new java.io.FileInputStream(cf));
	            log.info("loading config from:" + cf.getAbsolutePath());
	            time = cf.lastModified();
	            //检测配置文件是否被修改，自动reload
	            Thread t = new Thread(new Runnable(){
	                    public void run(){
	                        try{
	                            Thread.sleep(60000);
	                        }catch (InterruptedException e){
	                        }
	                        long newlmd = cf.lastModified();
	                        if (newlmd > time){
	                            time = newlmd;
	                            log.info("Config file {} is modified,reloading ...", cf.getAbsolutePath());
	                            try{
	                            	properties.load(new java.io.FileInputStream(cf));
	                            }catch (IOException e){
	                                log.error("Error while loading config file:{}", cf.getAbsolutePath());
	                            }
	                        }
	                    }
	                }, "Config file refresher");
	            t.setDaemon(true);
	            t.start();
	        }catch (IOException ex){
	            log.warn("cannot create log file", ex);
	        }
		}

        public String get(String key) {
            return properties.getProperty(key);
        }
        
        public String get(String k, String defaultValue){
            String s = properties.getProperty(k);
            if (s == null){
            	properties.setProperty(k, defaultValue);
                modified = true;
                return defaultValue;
            }
            return s;
        }

        public int getInt(String k, int defaultValue){
            String s = this.get(k, defaultValue + "");
            try{
                return Integer.parseInt(s);
            }catch (Exception e){
                return defaultValue;
            }
        }

        public boolean getBoolean(String k, boolean defaultValue){
            String s = this.get(k, defaultValue + "");
            try{
                return Boolean.parseBoolean(s);
            }catch (Exception e){
                return defaultValue;
            }
        }
        
        public boolean setProperty(String key, String value) {
        	properties.setProperty(key, value);
    		try{
                FileOutputStream fos = new java.io.FileOutputStream(cf);
                properties.store(fos, "");
                fos.close();
                return true;
            }catch (Exception ex) {
                log.warn("store config", ex);
                return false;
            }
        }

    };

    private Config(){
    }
    public static final Config get(){
    	return CONFIG;
    }
    abstract public String get(String key);
    abstract public String get(String k, String defaultValue);
    abstract public int getInt(String k, int defaultValue);
    abstract public boolean getBoolean(String k, boolean defaultValue);
    abstract public boolean setProperty(String key, String value);

}
