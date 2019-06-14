package com.socket.service;

import com.socket.mapper.AuthOrgMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AuthOrgService {

	@Autowired
	private AuthOrgMapper authOrgMapper;

	public List<Map<String, Object>> selectAllList(String uuId) {
		return authOrgMapper.selectAllList(uuId);
	}

	public Map<String, Object> selectUser(String uuId) {
		return authOrgMapper.selectUser(uuId);
	}

}