package com.socket.util;

import net.sf.json.JSONObject;

public class Result {

	public static JSONObject putValue() {
		return JSONObject.fromObject(DataUtil.mapOf("code",0,"msg","操作成功"));
	}

	public static JSONObject putValue(Object data) {
		return JSONObject.fromObject(DataUtil.mapOf("code",0,"msg","操作成功","data",data));
	}

	public static JSONObject putValue(Integer code, String msg) {
		return JSONObject.fromObject(DataUtil.mapOf("code",code,"msg",msg));
	}

	public static JSONObject putValue(Integer code, String msg, Object data) {
		return JSONObject.fromObject(DataUtil.mapOf("code",code,"msg",msg,"data",data));
	}
}
