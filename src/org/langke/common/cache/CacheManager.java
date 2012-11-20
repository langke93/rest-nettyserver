package org.langke.common.cache;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ClassUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.langke.common.Config;
import org.langke.common.CostTime;
import org.langke.common.ExecutorFactory;
import org.langke.util.logging.ESLogger;
import org.langke.util.logging.Loggers;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * 
 * @author langke
 * @created 2012-8-28
 *	如果参数是Map 取Map中的memKey作为key ，如果Map中没有memKey则取id作为key
 *	如果参数是基础类型java.lang.* 、数组、原始类型或基础类型: 所有参数相加作为key
 *  如果参数是实体类，取实体类中的PK,如果没有取实体类中id作为key
 *  缓存默认10分钟刷新,可以config.properties 设置 memcachedExpiryTime
 *  
 *  在需要缓存的方法加注解：@MemCaching
 *  在需要清除缓存的方法加注解：@MemFlush
 *  
 *  配置文件加：
isUsedMemcached=true
tt.cache.server = newmall.mem.tg.local:11211
memcachedPre = api_review_
#memcached expiry time ms
memcachedExpiryTime=600000

 *  @version 0.1 入缓存走异步
 *  @lastmodify 2012-09-28
 */

@Aspect
@Component
public class CacheManager {
	private static ESLogger log = Loggers.getLogger(CacheManager.class);

	MemcachedUtil memcachedClient = MemcachedUtil.getInstance();
	@SuppressWarnings("unused")
	@Pointcut("@annotation (com.qjmall.common.cache.MemCaching) || @annotation (com.qjmall.common.cache.MemFlush)")
    private void anyMethod() {}
    
	@Around(value="anyMethod()")
    public Object doAccessCheck(ProceedingJoinPoint pjp) throws Throwable{
    	Object target=pjp.getTarget();
    	String methodName=pjp.getSignature().getName();
    	Object[] args=pjp.getArgs();
    	Method method=getMethod(target,methodName,args);
    	if(method == null){//找不到方法
    		log.warn("mot match method:{} {}", methodName,args);
    		return pjp.proceed(args);
    	}
		String memKey = null;
    	boolean needCache=method.isAnnotationPresent(MemCaching.class);//有加些注解的加缓存
    	boolean flushCache=method.isAnnotationPresent(MemFlush.class); //刷新缓存
    	boolean isUsedMemcached = Config.get().getBoolean("isUsedMemcached", true);//是否启用缓存
    	if((!needCache && !flushCache) ||!isUsedMemcached){//不需要走缓存
    		return pjp.proceed(args);
    	}
		CostTime cost = new CostTime();
		cost.start();
		for (Object obj :args){
    		if(obj instanceof Map){//如果参数是Map，取Map中的id作为key
    			if(((Map<?, ?>)obj).containsKey("memKey")){//取Map中的memKey作为key
    				memKey = (String) ((Map<?, ?>)obj).get("memKey");
    			}
    			if(memKey == null && ((Map<?, ?>)obj).containsKey("id")){//如果没有设置，取Map中的id作为key
    				memKey = (String) ((Map<?, ?>)obj).get("id");
    			}
    			break;
    		}
    	}
		if(memKey==null){//参数不是map
			Class<?> entryclass = null;//=args[0].getClass();
			for(Object arg:args){
				if(arg == null)
					continue;
				entryclass = arg.getClass();//找到一个非空的参数
				break;
			}
			if(entryclass!=null){
				if(entryclass.isArray() || entryclass.isPrimitive() || entryclass.toString().indexOf("java.lang") > 0 || ClassUtils.isAssignable(entryclass, Map.class)){//数组、原始类型或基础类型
					memKey = "";
					for(Object obj : args){
						if(obj == null)
							continue;
						else if(obj.getClass().isArray())
							obj = ArrayUtils.toString(obj);
						memKey += String.valueOf(obj);//所有参数相加做为memKey
					}
				}else if(entryclass.toString().indexOf("java.util") > 0){
					log.warn("{}不支持java.util类型除Map以外的参数",methodName);
				}else{//实体类
					//如果参数是实体类，取实体类中的PK
		    		if(entryclass instanceof Serializable){
		    			//取实体类中带PK注解的属性作为memkey
						Method[] Entrymethods = entryclass.getDeclaredMethods();
						for(Method mhd : Entrymethods){
							if(mhd.isAnnotationPresent(PK.class)){
								memKey = (String) mhd.invoke(args[0]);
								break;
							}
						}
						if(memKey == null){
			    			try{
			    				Method m = entryclass.getMethod("getId");
			    				memKey = (String) m.invoke(args[0]);//取属性名为id作为key
			    			}catch(Exception e){
			    				log.warn("{}中没有id属性",args[0]);
			    			}
						}
		    		}
				}		
			}//end if entryclass != null
		}     
    	if(memKey == null){
    		log.warn("方法{}运行时没有取到缓存key！",methodName);
    		return pjp.proceed(args);
    	}
    	//取缓存
    	memKey = getCacheKey(target, method.getName(), memKey);
    	if(needCache){
    		Object value = memcachedClient.get(memKey);
			if(value != null){
				value = JSONObject.parseObject(value.toString(), method.getGenericReturnType());
				log.info("get data from cache:{} costTime:{}", memKey ,cost.cost());
				return value;
			}else{
				Object obj = pjp.proceed(args);
/*				if(obj instanceof NormalReturn){//不缓存错误结果
					if(((NormalReturn) obj).getStatusCode().equals("200"))
						return obj;
					((NormalReturn) obj).setCache(true);//标记为已缓存
				}*/
				int timeout = MemcachedUtil.MEMCACHED_EXPIRY_TIME;//默认10分钟刷新
				//boolean cached = memcachedClient.set(key, JSONObject.toJSONString(obj, SerializerFeature.BrowserCompatible),timeout);
				boolean cached = asycSetCache(memKey, obj, timeout);
/*				if(!cached && obj instanceof NormalReturn)
					((NormalReturn) obj).setCache(false);//标记为未缓存
*/				log.info("cached:{} timeout:{} costTime:{}", memKey,timeout,cost.cost());
				return obj;
			}
    	}
    	//刷新缓存 
    	if(flushCache){
        	List<String> methodList = new ArrayList<String>();
    		//取要刷新的key前缀，取不到则刷新所有带有@MemCaching注解的方法
    		String flushKey = method.getAnnotation(MemFlush.class).key();
    		if(StringUtils.isNotEmpty(flushKey)){
    			String[] str1=flushKey.split(",");
    		    for(int i=0;i<str1.length;i++){
    		    	methodList.add(str1[i]);
    		     }
    		}else{
    			Method[] methods = target.getClass().getDeclaredMethods();
        		for(Method mhd : methods){
        			if(mhd.isAnnotationPresent(MemCaching.class)){
        				methodList.add(mhd.getName());
    				}
        		}
    		}
    		
    		if(memKey != null){
    			for(String list : methodList){
    				memKey = getCacheKey(target, list, memKey);
    				log.info("flush cache:{}", memKey);
        			//memcachedClient.delete(memKey);
    				asycDelCache(memKey);
    			}
    		}
    		
    	}
    	return pjp.proceed(args);
		 
    }

	/**
	 * 异步存入缓存
	 * @param key
	 * @param obj
	 * @param timeout
	 * @return
	 */
	public boolean asycSetCache(final String key,final Object obj,final int timeout) {
		Callable<Boolean> cmd = new Callable<Boolean>(){
			@Override
			public Boolean call() throws Exception {
				return memcachedClient.set(key, JSONObject.toJSONString(obj, SerializerFeature.BrowserCompatible),timeout);
			}
		};
		ExecutorFactory.fixedExecutor.submit(cmd);
		return true;
	}
	
	/**
	 * 异步删除缓存
	 * @param key
	 * @return
	 */
	public boolean asycDelCache(final String key) {
		Callable<Boolean> cmd = new Callable<Boolean>(){
			@Override
			public Boolean call() throws Exception {
				return memcachedClient.delete(key);
			}
		};
		ExecutorFactory.fixedExecutor.submit(cmd);
		return true;
	}
	
    private Method getMethod(Object target,String methodName,Object[] args){
    	Method method = null;
    	Class<?>[] classes=new Class<?>[args.length];
    	try {
        	for(int i=0,j=args.length;i<j;i++){
        		if(args[i]!=null){
            		classes[i]=args[i].getClass();
            		if(args[i] instanceof Map){
            			classes[i] = Map.class;
            		}
        		}else{
        			;
        		}
        	}
			//method = target.getClass().getMethod(methodName, classes);
		} catch (Exception e) {
			method=null;
		}
    	if(method == null){
    		Method ms[] = target.getClass().getDeclaredMethods();
    		for(int i=0;i<ms.length;i++){//遍历方法
    			Class<?>[] parameterTypes = ms[i].getParameterTypes();
    			if(ms[i].getName().equals(methodName) && parameterTypes.length == classes.length){
    				Boolean parmIsMatch = true;
    				for(int j=0;j<parameterTypes.length;j++){//遍历参数
    					if(classes[j]!=null && !ClassUtils.isAssignable( parameterTypes[j], classes[j] , true) ){//参数类型比对，忽略null参数
    						parmIsMatch = false;
    						break;
    					}
    				}
    				if(parmIsMatch)
    					return ms[i];
    			}
    		}
     	}
    	return method;
    }
    private String getCacheKey(Object target,String method,String arg){
    	StringBuilder sb=new StringBuilder();
    	sb.append(MemcachedUtil.MEMCACHED_PRE);
    	sb.append(ClassUtils.getShortClassName(target.getClass())).append("_").append(method);
    	sb.append("_").append(arg.hashCode());//get hash code arg too long	   	
    	return sb.toString();
    }
} 
