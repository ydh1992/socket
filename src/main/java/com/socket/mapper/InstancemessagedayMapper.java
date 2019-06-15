package com.socket.mapper;

import com.socket.pojo.Instancemessageday;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface InstancemessagedayMapper {

	long insertSelective(Instancemessageday entity);

	int updateByExampleSelective(Instancemessageday entity);

	void insertList(List<Instancemessageday> entity);

	int updatemsgstate(String recUserId);

	List<Map<String,Object>>  getOfflineMessageList(String recUserId);

	void delete();
}