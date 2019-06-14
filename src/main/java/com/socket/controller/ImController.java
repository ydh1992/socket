package com.socket.controller;

import com.socket.pojo.Instancemessageday;
import com.socket.service.*;
import com.socket.util.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yeauty.pojo.Session;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/im")
@SessionAttributes(names = {"uuid","name"})
public class ImController{
	@Autowired
	private FileUtils fileUtils;
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
			return "login";
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
		model.put("uuid",uuid);
		model.put("name",json.getString("name"));
		return "layim";
	}

	/** 
	 *  取得所有聊天用户
	 */
	@RequestMapping(value = "/getUsers")
	@ResponseBody
	public Object getAllUser(HttpSession session){
		//获取用户分组 及用户
		JSONArray array= JSONArray.fromObject(Constant.GROUP);
		//个人
		List<Map<String, Object>> userList=authUserService.selectAllList(session.getAttribute("uuid").toString());
		//公司
		List<Map<String, Object>> orgList=authOrgService.selectAllList(session.getAttribute("uuid").toString());
		//获取已登录用户，添加登录状态
		Map<String, Session> sessions= WebSocket.sessionMap;
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
		return Result.putValue(DataUtil.mapOf("mine", DataUtil.mapOf("id",session.getAttribute("uuid").toString(),
				"username",session.getAttribute("name").toString(),"status","online","avatar", Constant.USER_AVATAR), "friend", array));
	}
	
	
	/** 
	 * 图片上传
	 */
	@RequestMapping(value = "/imgUpload", method = RequestMethod.POST)
	@ResponseBody
	public Object uploadImgFile(@RequestParam MultipartFile file){
		return  Result.putValue(DataUtil.mapOf("src",fileUtils.uploadFileToService(file,1)));
	}
	
	/** 
	 * 文件上传
	 */
	@RequestMapping(value = "/fileUpload",method = RequestMethod.POST)
	@ResponseBody
	public Object uploadAllFile(@RequestParam MultipartFile file){
		return  Result.putValue(DataUtil.mapOf("src",fileUtils.uploadFileToService(file,2)));
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
			return Result.putValue(JSONArray.fromObject(list));
		}
		return Result.putValue();
	}

	@RequestMapping(value = "/chatLog")
	public String chatlog(String id,ModelMap map,HttpSession session) {
		if (DataUtil.isNotBlank(id)) {
			int size = instancemessagelogService.countLog(DataUtil.mapOf("sendUserId", session.getAttribute("uuid"), "recUserId", id));
			map.put("size",size);
			return "chatlog";
		}
		return null;
	}
	
	/**
	 * 聊天记录
	 */
	@RequestMapping(value = "/chatLogs",method = RequestMethod.POST)
	@ResponseBody
	public Object chatlog(HttpServletRequest request,@RequestParam(defaultValue = "0")Integer page,String id,HttpSession session){
		if(!DataUtil.isNotBlank(id)){
			return Result.putValue(1,"缺少参数");
		}
		Map<String,Object> user= (Map<String, Object>) request.getSession().getAttribute(session.getAttribute("uuid").toString());
		Map<String,Object> map =DataUtil.mapOf("offset",page>0?page*Constant.PAGE_SIZE:0,"limit",Constant.PAGE_SIZE,
				"sendUserId",session.getAttribute("uuid"),"recUserId", Long.parseLong(id));
		//查询好友名称
		Map<String,Object> name=authOrgService.selectUser(id);
		if(name==null){
			name=authUserService.selectUser(id);
		}
		return Result.putValue(DataUtil.mapOf("list",instancemessagelogService.selectLogList(map),"recName",name!=null?name.get("username"):null));
	}

	/**
	 * 过滤关键字
	 */
	@RequestMapping(value = "/getKeyword")
	@ResponseBody
	public Object getKeyword(){
		String contextPath = null;
		try {
			String serverpath= ResourceUtils.getURL("classpath:templates").getPath().replace("%20"," ").replace('/', '\\');
			//从路径字符串中取出工程路径
			contextPath=serverpath.substring(1);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		if(contextPath!=null){
			fileUtils.createJsonFile(JSONArray.fromObject(codeService.getKeyword()),contextPath,"keyword");
		}
		return Result.putValue();
	}

	/**
	 *发送系统消息
	 */
	@RequestMapping(value = "/sedSystemMsg", method = RequestMethod.POST)
	@ResponseBody
	public JSONObject sedSystemMsg(@RequestParam String content, @RequestParam String ruuid){
		if(!DataUtil.isNotBlank(content,ruuid)){
			return Result.putValue(1,"缺少参数");
		}
		JSONObject data=DataUtil.jsonOf("sId",Constant.ADMIN_UUID,"rId",ruuid,"msg",content,"msgType",Instancemessageday.MSGTYPE_ENUM.SYSTEM.getMsgType());
		Session session=WebSocket.sessionMap.get(ruuid);
		if(session!=null){
			session.sendText(DataUtil.jsonOf("cmd",Constant.CMD_ENUM.MSG.getCmd(),"type",Constant.TYPE_ENUM.FRIEND.getType(),"data",data).toString());
			data.put("isRead",Instancemessageday.ISREAD_ENUM.YES.getIsRead());
		}
		instancemessagedayService.insertSelective(new Instancemessageday(data));
		return Result.putValue();
	}

	/**
	 *单聊
	 */
	@RequestMapping(value = "/chat")
	public String chat(@RequestParam String suuid, @RequestParam String ruuid,@RequestParam Integer ownerOrCompany,ModelMap model){
		if(DataUtil.isNotBlank(suuid,ruuid,ownerOrCompany)){
			model.put("uuid",suuid);
			//标识符  0-个人，1-企业，2-管理员
			Map<String,Object> map=null;
			if(ownerOrCompany.equals(Constant.PERSONAL_TYPE)){
				map=authUserService.selectUser(ruuid);
			}
			if(ownerOrCompany.equals(Constant.COMPANY_TYPE)){
				map=authOrgService.selectUser(ruuid);
			}
			if(map!=null&&DataUtil.isNotBlank(map.get("username"))){
				model.put("user",DataUtil.mapOf("uuid",ruuid,"name",map.get("username").toString(),"avatar",Constant.USER_AVATAR));
			}
		}
		return "chat";
	}

}
