package org.langke.common.cache;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.langke.common.Config;

import com.danga.MemCached.MemCachedClient;
import com.danga.MemCached.SockIOPool;
 
public class MemcachedUtil {
	public static String MEMCACHED_PRE = Config.get().get("memcachedPre","api_");//cache key pre
	public static int MEMCACHED_EXPIRY_TIME = Config.get().getInt("memcachedExpiryTime",600000);//默认缓存过期时间,600s
	private static String ttcache_server = Config.get().get("tt.cache.server");
	private static MemCachedClient memcachedClient;
	private static int initConn = 10;
	private static int minConn = 10;
	private static int maxConn = 200;
	protected static MemcachedUtil cache = new MemcachedUtil();  
	private final static String poolName = "memcached_pool";//poolName需要一至，否则会报attempting to get SockIO from uninitialized pool! 
	/**
	 * 保护型构造方法，不允许实例化!
	 * */
	protected MemcachedUtil() {

	}
	/**
	 * 获取唯一实例
	 * @return
	 */
	public static MemcachedUtil getInstance(){
		return cache;
	}
	static{
		if(memcachedClient == null)
			init();
	}
	private static synchronized void init(){
		try {
			if(ttcache_server == null){
				throw new Exception("tt.cache.server is not in config");
			}
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
        String[] servers = ttcache_server.replaceAll(",", " ").split("\\s+");   
        SockIOPool pool = SockIOPool.getInstance(poolName);
        pool.setServers(servers);
        pool.setInitConn(initConn);
        pool.setMinConn(minConn);
        pool.setMaxConn(maxConn);
        pool.setHashingAlg(SockIOPool.CONSISTENT_HASH);
        pool.setMaintSleep(30 * 1000);
		// 设置TCP的参数，连接超时等
		pool.setNagle(false);
		pool.setSocketTO(3000);
		pool.setSocketConnectTO(0);
        pool.setAliveCheck(true);
        pool.initialize();
        memcachedClient = new MemCachedClient(poolName); 
        memcachedClient.setCompressEnable(true);   
        memcachedClient.setCompressThreshold(64 * 1024);
    }

	public Object get(String key) {
		return getInstance().memcachedClient.get(key, key.hashCode(), true);
	}

	public Map<String, Object> gets(Collection<String> keys) {
		if(keys == null || keys.size() == 0){
			return null;
		}
		String[] param1 = new String[keys.size()];
		Integer[] param2 =new Integer[keys.size()];
		Iterator<String> iterator = keys.iterator();
		Object obj = null;
		int i = 0;
		while(iterator.hasNext()){
			obj = iterator.next();
			param1[i] =  (String)obj;
			param2[i] =  param1[i].hashCode();
			i++;
		}
		return getInstance().memcachedClient.getMulti(param1, param2, true);
	}

	public boolean set(String key, Object obj) {
		return getInstance().memcachedClient.set(key, obj);
	}

	public boolean set(String key, Object obj,int expiry) {
		return getInstance().memcachedClient.set(key, obj, new Date(expiry));
	}
	
	public boolean setDefault(String key, Object obj,int expiry) {
		return getInstance().memcachedClient.set(key, obj, new Date(MEMCACHED_EXPIRY_TIME));
	}
	
	public boolean add(String key, Object value) {
		return getInstance().memcachedClient.add(key, value);
	}

	public boolean add(String key, Object value, int expiry) {
		return getInstance().memcachedClient.add(key, value, new Date(expiry));
	}
	
	public boolean addDefault(String key, Object value) {
		return getInstance().memcachedClient.add(key, value, new Date(MEMCACHED_EXPIRY_TIME));
	}

	public boolean replace(String key, Object value) {
		return getInstance().memcachedClient.replace(key, value);
	}

	public boolean replace(String key, Object value, int expiry) {
		return getInstance().memcachedClient.replace(key, value, new Date(expiry));
	}
	
	public boolean replaceDefault(String key, Object value) {
		return getInstance().memcachedClient.replace(key, value, new Date(MEMCACHED_EXPIRY_TIME));
	}
	
	public boolean delete(String key){
		return getInstance().memcachedClient.delete(key);
	}
	
	public boolean flush() {
		return getInstance().memcachedClient.flushAll();
	}

	public boolean shutdown() {
		try{
			SockIOPool pool = SockIOPool.getInstance(poolName);
			if(pool != null){
				pool.shutDown();
			}
			return true;
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
	}
	
	public static void main(String[] args) throws InterruptedException{
		final MemcachedUtil cache = MemcachedUtil.getInstance();
		final String key = MEMCACHED_PRE+"FF121";
		final AtomicInteger count = new AtomicInteger();
		final Object lock = new Object();
		//cache.delete(key);
		final int concurrent;
		final int run ;
		if(args.length==2){
			concurrent = Integer.valueOf(args[0]);
			run = Integer.valueOf(args[1]);
		}else{
			concurrent = 100;
			run = 1000;
		}
		for(int i=0;i<concurrent;i++){
			new Thread(
				new Runnable() {
					@Override
					public void run() {
						int i;
						for(i=0;i<run;i++){
							Object obj = cache.get(key);
							if(obj!=null)
								;
							else
								cache.addDefault(key,"jjjk");
							count.incrementAndGet();
						} 
					}
				}
			).start();
		}
		new Thread(){
			public void run() {
				int total = 0 ;
				List<Map<Date,Integer>> countList = new LinkedList<Map<Date,Integer>>();
				Map<Date,Integer> countMap ; 
				while(total < run * concurrent ) {
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					synchronized(lock) {
						System.out.println(Calendar.getInstance().getTime()+"	count:"+count);
						countMap = new HashMap<Date, Integer>();
						countMap.put(Calendar.getInstance().getTime(),count.get());
						countList.add(countMap);
						total += count.get();
						count.set(0);
					}
					
				}
				cache.shutdown();
				for(Map<Date, Integer> map:countList){
					System.out.println(map);
				}
			}
		}.start();
		//Thread.sleep(7000);
		//System.out.println("	count:"+count);
	}
	
}
