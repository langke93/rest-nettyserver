package org.langke.common.bdb;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.langke.util.logging.ESLogger;
import org.langke.util.logging.Loggers;

import com.sleepycat.bind.EntryBinding;
import com.sleepycat.bind.serial.SerialBinding;
import com.sleepycat.collections.StoredMap;
import com.sleepycat.je.DatabaseException;

public class BrekeleyDBImpl extends AbstractBerkeleyDB implements BrekeleyDB {
	private StoredMap storedMap = null;
	private static ESLogger log = Loggers.getLogger(BrekeleyDBImpl.class);
	
	//使用默认的路径和缓存大小构造函数
	public BrekeleyDBImpl(String homeDirectory,String dbname,Class valueBindClass) {
			super(homeDirectory,dbname);
			EntryBinding keyBinding =new SerialBinding (javaCatalog,String.class);
			EntryBinding valueBinding =new SerialBinding(javaCatalog, 	valueBindClass);
			storedMap = new StoredMap(database,keyBinding, valueBinding, 	true);
			log.info(homeDirectory+"	dbname:"+dbname+"	alivedb:"+env.getDatabaseNames());
	}

	public Object poll()  {
		if(!storedMap.isEmpty()){
			Entry<String,?> entry=(Entry<String,?>)storedMap.entrySet().iterator().next();
			delete(entry.getKey());
			return entry;
		}
		return null;
	}
	
	public List<?> poll(int limit)  {
		List<Entry>  list = new ArrayList<Entry>();
		for(int i=0;i<limit;i++){
			if(!storedMap.isEmpty()){
				Entry<String,Object> entry=(Entry<String,Object>)storedMap.entrySet().iterator().next();
				list.add(entry);
				delete(entry.getKey());
			}
		}
		return list;
	}
	
	// 存入
    public void put(Object key,Object value) {
		storedMap.put(key, value);
	}
    //取出
	public Object get(Object key){
		return storedMap.get(key);
	}
	public List<?> get(int limit){
		List<Entry>  list = new ArrayList<Entry>();
		for(int i=0;i<limit;i++){
			if(!storedMap.isEmpty()){
				Entry<String,Object> entry=(Entry<String,Object>)storedMap.entrySet().iterator().next();
				list.add(entry);
			}
		}
		return list;
	}
	//删除
	public Object delete(Object key){
		return storedMap.remove(key);
	}
	public int size(){
		return storedMap.size();
	}
	public boolean isEmpty(){
		return storedMap.isEmpty();
	}
	public boolean contains(Object key){
		return storedMap.containsKey(key);
	}
	public void clear(){
		storedMap.clear();
	}
	
	// 测试函数
	public static void main(String[] strs) {
		BrekeleyDBImpl  bdb = null ;
		try {
			bdb = new BrekeleyDBImpl("bdb","db_test",String.class);
			log.info("{}",bdb.env.getDatabaseNames());
			bdb.put("key", "value");
			log.info("{}", bdb.get("key"));
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if (bdb != null)
					bdb.close();
			} catch (DatabaseException e) {
				e.printStackTrace();
			}
		}
		
	}


}
