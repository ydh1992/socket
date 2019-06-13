package com.socket.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
@Mapper
public interface UtilMapper {

	List<Map<String,Object>> select(@Param("sql") String sql);
	
}
