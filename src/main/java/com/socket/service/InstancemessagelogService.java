package com.socket.service;

import com.socket.mapper.InstancemessagelogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class InstancemessagelogService {

	@Autowired
	private InstancemessagelogMapper instancemessagelogMapper;


	public List<Map<String, Object>> selectLogList(Map<String, Object> param) {
		return instancemessagelogMapper.selectLogList(param);
	}


	public int countLog(Map<String, Object> param) {
		return instancemessagelogMapper.countLog(param);
	}

}