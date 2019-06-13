package com.socket.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
@Mapper
public interface AuthOrgMapper {

	List<Map<String, Object>> selectAllList(String uuId);

}