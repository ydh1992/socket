package com.socket.service;

import com.socket.mapper.InstancemessagedayMapper;
import com.socket.pojo.Instancemessageday;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class InstancemessagedayService {

	@Autowired
	private InstancemessagedayMapper instancemessagedayMapper;

	@Transactional
	public long insertSelective(Instancemessageday record) {
		return instancemessagedayMapper.insertSelective(record);
	}

	public int updateByExampleSelective(Instancemessageday entity) {
		return instancemessagedayMapper.updateByExampleSelective(entity);
	}


	public int updatemsgstate(String recUserId) {
		return instancemessagedayMapper.updatemsgstate(recUserId);
	}


	public List<Map<String, Object>> getOfflineMessageList(String recUserId) {
		List<Map<String, Object>> list=instancemessagedayMapper.getOfflineMessageList(recUserId);
		if(list!=null&&list.size()>0){
			//更新状态为已读状态
			instancemessagedayMapper.updatemsgstate(recUserId);
			return list;
		}
		return list;
	}

	@Transactional
	public void insertList(List<Instancemessageday> entity){
		instancemessagedayMapper.insertList(entity);
	}

	public void delete(){
		instancemessagedayMapper.delete();
	}

}