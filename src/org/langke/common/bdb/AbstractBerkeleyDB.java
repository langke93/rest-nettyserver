package org.langke.common.bdb;

import java.io.File;
import java.io.FileNotFoundException;

import com.sleepycat.bind.serial.StoredClassCatalog;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;

public abstract class AbstractBerkeleyDB {
	public Environment env;
	private static final String CLASS_CATALOG = "java_class_catalog";
	protected StoredClassCatalog javaCatalog;
	protected Database catalogdatabase;
	protected Database database;

	public AbstractBerkeleyDB(String homeDirectory,String dbname) {
		// 打开env
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setTransactional(true);
		envConfig.setAllowCreate(true);
		File bdbHomeFile = new File(homeDirectory);
		if(!bdbHomeFile.exists())
			bdbHomeFile.mkdirs();
		env = new Environment(bdbHomeFile, envConfig);
		
		if(database != null)
			return;
		// 设置DatabaseConfig
		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setTransactional(true);
		dbConfig.setAllowCreate(true);
		// 打开
		catalogdatabase = env.openDatabase(null, CLASS_CATALOG, dbConfig);
		javaCatalog = new StoredClassCatalog(catalogdatabase);
		// 设置DatabaseConfig
		DatabaseConfig dbConfig0 = new DatabaseConfig();
		dbConfig0.setTransactional(true);
		dbConfig0.setAllowCreate(true);
		// 打开
		database = env.openDatabase(null, dbname, dbConfig);
	}
    //关闭数据库，关闭环境
	public void close() throws DatabaseException {
		if(database!=null)
			database.close();
		if(javaCatalog!=null)
			javaCatalog.close();
		if(env!=null)
			env.close();
	}
	//put方法
	protected abstract void put(Object key,Object value);
	//get方法
	protected abstract Object get(Object key);
	//delete方法
	protected abstract Object delete(Object key);
}
