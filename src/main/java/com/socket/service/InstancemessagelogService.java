package com.socket.service;

import com.socket.mapper.InstancemessagelogMapper;
import com.socket.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class InstancemessagelogService {

	@Autowired
	private InstancemessagelogMapper instancemessagelogMapper;


	public List<Map<String, Object>> selectLogList(Map<String, Object> param) {
		List<Map<String, Object>> list=instancemessagelogMapper.selectLogList(param);
		if(list!=null&&list.size()>0){
			list.forEach(e->{
				e.put("avatar", Constant.USER_AVATAR);
			});
			return list;
		}
		return null;
	}


	public int countLog(Map<String, Object> param) {
		return instancemessagelogMapper.countLog(param);
	}

}