package org.langke.core.service.impl;

import org.apache.commons.lang.math.RandomUtils;
import org.langke.bean.HelloTable;
import org.langke.common.bdb.BrekeleyDB;
import org.langke.common.server.NettyHttpRequest;
import org.langke.common.server.resp.RespData;
import org.langke.core.dao.IDemoDao;
import org.langke.core.dao.mappers.HelloTableMapper;
import org.langke.core.service.DemoService;
import org.langke.util.logging.ESLogger;
import org.langke.util.logging.Loggers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("demoService")
public class DemoServiceImpl implements DemoService{
	private static ESLogger log = Loggers.getLogger(DemoServiceImpl.class);

	@Autowired
	private BrekeleyDB brekeleyDB;
	
	@Autowired
	private HelloTableMapper helloTableMapper;
	
	@Autowired
	private IDemoDao demoDao;
	public void setDemoDao(IDemoDao demoDao) {
		this.demoDao = demoDao;
	}
	@Override
	public RespData test(NettyHttpRequest request) {
		RespData data = new RespData();
		data.setContent(request.params());
		return data;
	}
	@Override
	public RespData create(NettyHttpRequest request) {
		RespData data = new RespData();
		try{
			data.setContent(request.params());
			demoDao.create();
			demoDao.insert();
			demoDao.update(request.param("name"));
			data.setContent(demoDao.select());
		}catch(Exception e){
			log.error("{}", e);
			throw new RuntimeException(e);
		}
		return data;
	}
	@Override
	public RespData select(NettyHttpRequest request) {
		RespData data = new RespData();
		try{
			data.setContent(demoDao.select());
		}catch(Exception e){
			log.error("{}", e);
			throw new RuntimeException(e);
		}
		return data;
	}
	@Override
	public RespData insert(NettyHttpRequest request) {
		RespData data = new RespData();
		try{
			data.setContent(demoDao.insert());
		}catch(Exception e){
			log.error("{}", e);
			throw new RuntimeException(e);
		}
		return data;
	}
	@Override
	public RespData update(NettyHttpRequest request) {
		RespData data = new RespData();
		try{
			data.setContent(demoDao.update(request.param("name")));
		}catch(Exception e){
			log.error("{}", e);
			throw new RuntimeException(e);
		}
		return data;
	}


	public RespData mybatis(NettyHttpRequest request) {
		RespData data = new RespData();
		try{
			HelloTable helloTable = new HelloTable();
			helloTable.setName("ibatis");
			helloTable.setScore(100);
			helloTableMapper.insert(helloTable);
			data.setContent(helloTableMapper.select(new HelloTable()));
		}catch(Exception e){
			log.error("{}", e);
			throw new RuntimeException(e);
		}
		return data;
	}
	

	/**
	 * http://localhost:9090/demo/bdb?key=value
	 * @param key
	 * @param value
	 * @return
	 */

	public RespData bdb(NettyHttpRequest request) {
		RespData data = new RespData();
		try{
			String key = request.param("key","key");
			String value = request.param("value","value");
			int random = RandomUtils.nextInt();
			brekeleyDB.put(key+random, value+random);
			data.setContent(brekeleyDB.get(10));
		}catch(Exception e){
			log.error("{}", e);
			throw new RuntimeException(e);
		}
		return data;
	}
	
}
