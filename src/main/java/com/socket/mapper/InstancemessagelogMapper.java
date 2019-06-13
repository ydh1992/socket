package com.socket.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
@Mapper
public interface InstancemessagelogMapper {

	List<Map<String, Object>> selectLogList(@Param("param")Map<String, Object> param);

	int countLog(Map<String, Object> param);
}