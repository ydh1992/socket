package com.socket.controller;


import com.socket.pojo.Instancemessageday;
import com.socket.service.InstancemessagedayService;
import com.socket.util.Constant;
import com.socket.util.DataUtil;
import com.socket.util.DateUtil;
import io.netty.handler.timeout.IdleStateEvent;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.yeauty.annotation.*;
import org.yeauty.pojo.ParameterMap;
import org.yeauty.pojo.Session;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
/**
 * 简单来讲，Netty是一个提供了易于使用的API的客户端/服务端框架。Netty并发非常高，一个非阻塞的IO，Netty传输速度也非常快，因为他是0拷贝，什么是零拷贝？NIO中的特性之一就是零拷贝，在Java中，内存分为堆和栈以及字符串常量值等等，
 * 如果有一些数据从IO中读取并且放到堆里面，中间会经过一些缓冲区。
 *
 *具体来讲，如果要从IO中读取数据，分为两个步骤：
 *
 * (1）从IO流中读取出来放到缓冲区，程序从缓冲区中读取，再放到堆中，此时数据就会被拷贝两次才能到达堆或者堆内存中。如果数据量很大，那么就会造成资源的浪费
 * (2）Netty其实就是利用NIO中的零拷贝特性，当Netty需要接收数据或者传输数据的时候，就会新开辟一块堆内存，然后数据就直接通过IO读取到了新开辟的堆内存中，这样也就加快了数据传输的速度。
 * */
@ServerEndpoint(prefix = "netty-websocket")
@Component
public class WebSocket {

    private  Logger log= LoggerFactory.getLogger(WebSocket.class);

    /**concurrent包的线程安全Map，用来存放每个客户端对应的MyWebSocket对象。*/
    public static Map<String, Session> sessionMap = new ConcurrentHashMap<String, Session>();

    /**与某个客户端的连接会话，需要通过它来给客户端发送数据*/
    private Session session;

    /**静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。*/
    private static int onlineUser;

   /**此处是解决无法注入的*/
    private static ApplicationContext context;

    /**当前用户id*/
    private String uuid;
    @Autowired
    private InstancemessagedayService instancemessagedayService;

    public static void setApplicationContext(ApplicationContext context){
        context=context;
    }
    /**
     * 连接建立成功调用的方法
     * */
    @OnOpen
    public void onOpen(Session session, ParameterMap parameterMap) {
        onlineUser++;
        this.session = session;
        this.uuid = parameterMap.getParameter("uuid");
        //把自己的信息加入map
        sessionMap.put(this.uuid,session);
        //设置uid
        session.setAttribute("uuid",uuid);
        //通知在线用户
        sendListMessage(onlineOrOffline(Constant.CMD_ENUM.ONLINE.getCmd(),this.uuid), null);
        log.info("有新连接加入！当前在线人数为：" + onlineUser);
    }

    /**
     * 连接关闭调用的方法
     * */
    @OnClose
    public void onClose(Session session) {
        onlineUser--;
        //通知在线用户
        sendListMessage(onlineOrOffline(Constant.CMD_ENUM.OFFLINE.getCmd(),this.uuid), null);
        sessionMap.remove(this.uuid);
        log.info("当前在线人数为：" + onlineUser);
    }

    /**
     * 收到客户端消息
     * */
    @OnMessage
    public void onMessage(String message, Session session) {
        if(DataUtil.isNotBlank(message)&&session!=null){
            JSONObject jsonObject = JSONObject.fromObject(message);
            if(jsonObject.getInt("cmd")== Constant.CMD_ENUM.MSG.getCmd()){
                JSONObject json = JSONObject.fromObject(jsonObject.getJSONObject("data"));
                if(DataUtil.isNotBlank(json.get("rId"))){
                    json.put("timestamp", DateUtil.nowDate());
                    Session rsession=sessionMap.get(json.getString("rId"));
                    if(rsession!=null){
                        log.info("发送数据" + message.toString());
                        rsession.sendText(message.toString());
                        json.put("isRead",Instancemessageday.ISREAD_ENUM.YES.getIsRead());
                    }
                    //存储
                    instancemessagedayService.insertSelective(new Instancemessageday(json));
                }
            }
        }
    }

    /**
     * 服务端发生异常*/
    @OnError
    public void onError(Session session, Throwable throwable) {
        log.error("当前用户:"+this.uuid+"---session:"+session,throwable);
    }

    /**
     * 当接收到二进制消息时，对该方法进行回调 注入参数的类型:Session、byte[]*/
    @OnBinary
    public void onBinary(Session session, byte[] bytes) {
        session.sendBinary(bytes);
    }

    /**
     * 当接收到Netty的事件时，对该方法进行回调 注入参数的类型:Session、Object*/
    @OnEvent
    public void onEvent(Session session, Object evt) {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            switch (idleStateEvent.state()) {
                case READER_IDLE:
                    System.out.println("读空闲");
                    break;
                case WRITER_IDLE:
                    System.out.println("写空闲");
                    break;
                case ALL_IDLE:
                    System.out.println("全部空闲");
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 实现服务器主动批量推送
     * */
    public void sendListMessage(JSONObject message, List<Integer> userIds) {
        if (message != null) {
            List<Instancemessageday> list = new ArrayList<>();
            //指定用户群发
            JSONObject json=JSONObject.fromObject(message.getJSONObject("data"));
            json.put("timestamp", DateUtil.nowDate());
            if (userIds != null&&userIds.size()>0) {
                userIds.forEach(o -> {
                    // 判断是否登录
                    Session ol = sessionMap.get(o.longValue());
                    if (ol!=null) {
                        json.put("isRead",Instancemessageday.ISREAD_ENUM.YES.getIsRead());
                        ol.sendText(message.toString());
                    }else{
                        if(json.containsKey("isRead")){
                            json.remove("isRead");
                        }
                    }
                    //过滤非消息类型通知
                    if(message.getInt("cmd")==Constant.CMD_ENUM.MSG.getCmd()){
                        list.add(new Instancemessageday(json));
                    }
                });
            } else {
                sessionMap.keySet().forEach(o -> {
                    sessionMap.get(o).sendText(message.toString());
                    //过滤非消息类型通知
                    if(message.getInt("cmd")==Constant.CMD_ENUM.MSG.getCmd()){
                        json.put("isRead",Instancemessageday.ISREAD_ENUM.YES.getIsRead());
                        list.add(new Instancemessageday(json));
                    }
                });
            }
            if (list.size() > 0) {
                instancemessagedayService.insertList(list);
            }
        }
    }

    /**拼装上线下线用户消息*/
    private JSONObject onlineOrOffline(int cmd,String uuid){
        return DataUtil.jsonOf("cmd",cmd,"data",DataUtil.jsonOf("sId",uuid));
    }
    public static synchronized int getOnlineUser() {
        return onlineUser;
    }
}
