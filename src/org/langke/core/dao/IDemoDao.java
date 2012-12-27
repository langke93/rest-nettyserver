package org.langke.core.dao;

import java.util.List;
import java.util.Map;

public interface IDemoDao {
	public void create();
	public int insert();
	public int update(String name);
	public List<Map<String,Object>> select();
	public void drop();
}
