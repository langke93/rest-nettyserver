package org.langke.core.dao.mappers;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.langke.bean.HelloTable;

public interface HelloTableMapper {

	public int insert(@Param("demo") HelloTable helloTable);
	
	public List<Map<String,Object>> select(HelloTable helloTable);
}
