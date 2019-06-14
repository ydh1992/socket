package com.socket.util;

public class Constant {

	public static final int PAGE_SIZE = 10;
	/**跨域访问域名*/
	public final static String TRUST_CROSS_ORIGINS = "*";
	
	public final static String HTTP_PARAM = "http_param";
	public final static String REQUEST_BODY = "request_body";
	/**用户登录redis存储key   user_uuid_openid*/
	public static final String REDIS_USER_KEY = "user_%s_%s";
	/**用户头像*/
	public static final String USER_AVATAR = "/layui/images/avatar/default.gif";
	/**若值为offline代表离线，online或者不填为在线*/
	public static final String USER_OFFLINE = "offline";
	/**online或者不填为在线*/
	public static final String USER_ONLINE= "online";
	/**分组*/
	public static final String GROUP ="[{\"groupname\":\"管理员\",\"id\":\"2\"},{\"groupname\":\"公司\",\"id\":\"1\"},{\"groupname\":\"个人\",\"id\":\"0\"}]";
	/**账号类型：  0-个人，1-企业，2-管理员*/
	public static final int ADMIN_TYPE=2;
	public static final int COMPANY_TYPE=1;
	public static final int PERSONAL_TYPE=0;
	//管理员uuid
	public static final String ADMIN_UUID="111";

	/** 是或否*/
	public static enum YES_OR_NO_ENUM {
		NO(0), YES(1);
		private Integer type;

		private YES_OR_NO_ENUM(Integer type) {
			this.type = type;
		}

		public Integer getType() {
			return type;
		}
	}
	/**消息类型*/
	public static enum CMD_ENUM {
		//0心跳  1上线  2下线  3异常消息  4发送消息
		HEARTBEAT(0), ONLINE(1), OFFLINE(2), ERROR(3), MSG(4);
		private int cmd;

		private CMD_ENUM(int cmd) {
			this.cmd = cmd;
		}

		public int getCmd() {
			return cmd;
		}
	}
	/**发送类型*/
	public static enum TYPE_ENUM {
		//0朋友  1群组
		FRIEND(0), GROUP(1);
		private int type;

		private TYPE_ENUM(int type) {
			this.type = type;
		}

		public int getType() {
			return type;
		}
	}
}
