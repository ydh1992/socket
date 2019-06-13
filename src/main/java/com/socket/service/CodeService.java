package com.socket.service;

import com.socket.mapper.CodeMapper;
import com.socket.util.DataUtil;
import com.socket.util.RedisUtil;
import net.sf.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CodeService {

    @Autowired
    private CodeMapper codeMapper;


	public String getKeyword() {
		String keyword= RedisUtil.get("keyword");
		if(DataUtil.isNotBlank(keyword)){
			return keyword;
		}else{
			JSONArray jsonArray=JSONArray.fromObject(codeMapper.getKeyWord());
			RedisUtil.set("keyword",jsonArray.toString());
		}
		return RedisUtil.get("keyword");
	}

}