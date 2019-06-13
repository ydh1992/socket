package com.socket.controller;

import com.socket.service.*;
import com.socket.util.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.yeauty.pojo.Session;

import javax.servlet.http.HttpServletRequest;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/im")
public class ImController{
	@Autowired
	private CodeService codeService;
	@Autowired
	private AuthUserService authUserService;
	@Autowired
	private AuthOrgService authOrgService;
	@Autowired
	private InstancemessagedayService instancemessagedayService;
	@Autowired
	private InstancemessagelogService instancemessagelogService;

	/**
	 * 登录IM
	 */
	@RequestMapping(value ="/login", method = RequestMethod.POST)
	public String login(@RequestParam String uuid, @RequestParam String unionid, @RequestParam String ownerOrCompany, HttpServletRequest request
	, ModelMap model){
		if(StringUtils.isBlank(uuid)&& StringUtils.isBlank(unionid)&& StringUtils.isBlank(ownerOrCompany)){
			return null;
		}
		//判断是否登录
		String key=String.format(Constant.REDIS_USER_KEY,uuid,unionid);
		String str= RedisUtil.get(key);
		if(StringUtils.isBlank(str)){
			return "login";
		}
		JSONObject json= JSONObject.fromObject(str);
		Map<String,Object> map=DataUtil.mapOf("uuid",uuid,"unionid",unionid,"name",json.getString("name"),"type",ownerOrCompany);
		request.getSession().setAttribute(uuid,map);
		model.put("user",map);
		return "test";
	}

	/** 
	 *  取得所有聊天用户
	 */
	@RequestMapping(value = "/getUsers")
	@ResponseBody
	public Object getAllUser(@RequestParam String uuid, HttpServletRequest request) throws Exception{
		//获取用户分组 及用户
		JSONArray array= JSONArray.fromObject(Constant.GROUP);
		//个人
		List<Map<String, Object>> userList=authUserService.selectAllList(uuid);
		//公司
		List<Map<String, Object>> orgList=authOrgService.selectAllList(uuid);
		//获取已登录用户，添加登录状态
		Map<String, Session> sessions=WebSocketController.sessionMap;
		sessions.keySet().stream().forEach(o->{
			userList.stream().forEach(e->{
				e.put("avatar", Constant.USER_AVATAR);
				if(o.equals(e.get("id").toString())){
					e.put("status", Constant.USER_ONLINE);
				}else{
					e.put("status", Constant.USER_OFFLINE);
				}
			});
			orgList.stream().forEach(e->{
				e.put("avatar", Constant.USER_AVATAR);
				if(o.equals(e.get("id").toString())){
					e.put("status", Constant.USER_ONLINE);
				}else{
					e.put("status", Constant.USER_OFFLINE);
				}
			});
		});
		array.forEach(e->{
			JSONObject jsonObject=(JSONObject)e;
			// 0-个人，1-企业，2-管理员
			if(jsonObject.getInt("id")== Constant.COMPANY_TYPE&&orgList.size()>0){
				jsonObject.put("list",orgList);
				jsonObject.getJSONArray("list").add(orgList);
			}
			if(jsonObject.getInt("id")== Constant.PERSONAL_TYPE&&userList.size()>0){
				jsonObject.put("list", userList);
			}
		});
		//暂时没有群组
		Map<String,Object> map= (Map<String, Object>) request.getSession().getAttribute(uuid);
		return Result.putValue(DataUtil.mapOf("mine", DataUtil.mapOf("id",uuid,"username",map.get("name"),"status","online",
				"avatar", Constant.USER_AVATAR), "friend", array));
	}
	
	
	/** 
	 * 图片上传
	 */
	@RequestMapping(value = "/imgUpload", method = RequestMethod.POST)
	@ResponseBody
	public Object uploadImgFile(@RequestParam MultipartFile file, HttpServletRequest request,@RequestParam String uuid){

		Session session=WebSocketController.sessionMap.get(uuid);
		if(session==null){
			return null;
		}
		return  JSONObject.fromObject(FileUtils.uploadFileToService(file, request,1));
	}
	
	/** 
	 * 文件上传
	 */
	@RequestMapping(value = "/fileUpload",method = RequestMethod.POST)
	@ResponseBody
	public Object uploadAllFile(@RequestParam MultipartFile file, HttpServletRequest request,@RequestParam String uuid){
		Session session=WebSocketController.sessionMap.get(uuid);
		if(session==null) {
			return null;
		}
		return  JSONObject.fromObject(FileUtils.uploadFileToService(file,request,2));
	}

	/**
	 * 取得离线消息
	 */
	@RequestMapping(value = "/getOfflineMsg")
	@ResponseBody
	public Object userMessageCount(HttpServletRequest request,@RequestParam String uuid){
		Map<String,Object> map= (Map<String, Object>) request.getSession().getAttribute(uuid);
		List<Map<String, Object>> list=instancemessagedayService.getOfflineMessageList(uuid,Integer.valueOf(map.get("type").toString()));
		if(list!=null){
			return JSONArray.fromObject(list);
		}
		return null;
	} 
	
	/**
	 * 聊天记录
	 */
	@RequestMapping(value = "/historyMessageAjax",method = RequestMethod.POST)
	@ResponseBody
	public Object userHistoryMessages(HttpServletRequest request,Integer skipToPage,@RequestParam(defaultValue = "10") Integer pageSize
			,String uuid){
		Map<String,Object> session= (Map<String, Object>) request.getSession().getAttribute(uuid);
		Map<String,Object> map =DataUtil.mapOf("page",skipToPage>0?skipToPage*pageSize:0,"limit",pageSize,"types",Integer.valueOf(session.get("type").toString()),
				"sendUserId",uuid,"recUserId", Long.parseLong(request.getParameter("id")));
		return Result.putValue(instancemessagelogService.selectLogList(map));
	}
	
	/**
	 * 聊天记录页面
	 */
	@RequestMapping(value = "/historyMessage", method = RequestMethod.GET)
	public String userHistoryMessagesPage(HttpServletRequest request,Integer skipToPage,@RequestParam(defaultValue = "10") Integer pageSize
			,@RequestParam String uuid,ModelMap model){
		int totalsize = instancemessagelogService.countLog(DataUtil.mapOf("sendUserId",uuid, "recUserId", Long.parseLong(request.getParameter("id"))));
		model.addAttribute("pager", DataUtil.mapOf("pageNo",skipToPage>0?skipToPage*pageSize:0,"pageSize",pageSize
				,"totalSize",totalsize));
		return "historymessage";
	}

	/**
	 * 过滤关键字
	 */
	@RequestMapping(value = "/getKeyword")
	@ResponseBody
	public Object getKeyword(HttpServletRequest request){
		String contextPath = null;
		try {
			String serverpath= ResourceUtils.getURL("classpath:static").getPath().replace("%20"," ").replace('/', '\\');
			//从路径字符串中取出工程路径
			contextPath=serverpath.substring(1)+"//json";
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if(contextPath!=null){
			FileUtils.createJsonFile(JSONArray.fromObject(codeService.getKeyword()),contextPath,"keyword");
		}
		return Result.putValue();
	}

	/**
	 * 单聊
	 */
	@RequestMapping(value = "/sedMsg", method = RequestMethod.POST)
	public String sedSystemMsg(@RequestParam String content, @RequestParam(defaultValue = Constant.ADMIN_UUID ) String suuid, @RequestParam String ruuid, HttpServletRequest request){

		return "chat";
	}

}
