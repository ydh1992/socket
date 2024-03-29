package com.socket.service;

import com.socket.mapper.AuthUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AuthUserService {

	@Autowired
	private AuthUserMapper authUserMapper;

	/**用户uuid和用户类型，查询所在分类的用户数据*/
	public List<Map<String, Object>> selectAllList(String uuId) {
		return authUserMapper.selectAllList(uuId);
	}

	public Map<String, Object> selectUser(String uuId) {
		return authUserMapper.selectUser(uuId);
	}
}