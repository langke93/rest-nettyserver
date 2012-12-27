package org.langke.core.dao;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class DemoDaoImpl extends JdbcDaoSupport implements IDemoDao{
	public void create(){
		drop();
		String sql = "create table hellotable(name varchar(40), score int)";
		this.getJdbcTemplate().execute(sql);
	}
	
	public int insert(){
        int res = this.getJdbcTemplate().update("insert into hellotable values('Ruth Cao', 86)");
        //this.getJdbcTemplate().execute("insert into hellotable values ('Flora Shi', 92)");
        return res;
	}
	
	public int update(String name){
		int res = 0;
		if(name != null)
			res = this.getJdbcTemplate().update("update hellotable set name=? where score=86",name);
		return res;
	}
	
	public List<Map<String,Object>> select(){
		String sql = "SELECT name, score FROM hellotable ORDER BY score";
		return this.getJdbcTemplate().queryForList(sql);
	}
	
	public void drop(){
		this.getJdbcTemplate().execute("DROP TABLE IF EXISTS hellotable");
	}
}
