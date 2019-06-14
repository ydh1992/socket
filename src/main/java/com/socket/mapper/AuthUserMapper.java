package com.socket.mapper;


import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;
@Mapper
public interface AuthUserMapper {

	List<Map<String, Object>> selectAllList(String uuId);

	Map<String, Object> selectUser(String uuId);
}