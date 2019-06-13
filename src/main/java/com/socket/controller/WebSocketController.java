package com.socket.controller;


import com.socket.pojo.Instancemessageday;
import com.socket.service.AuthUserService;
import com.socket.service.InstancemessagedayService;
import com.socket.util.Constant;
import com.socket.util.DataUtil;
import com.socket.util.DateUtil;
import io.netty.handler.codec.http.HttpHeaders;
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
public class WebSocketController {

    private  Logger log= LoggerFactory.getLogger(WebSocketController.class);

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
    private AuthUserService authUserService;
    @Autowired
    private InstancemessagedayService instancemessagedayService;

    public static void setApplicationContext(ApplicationContext context){
        context=context;
    }
    /**
     * 连接建立成功调用的方法
     * */
    @OnOpen
    public void onOpen(Session session, ParameterMap parameterMap, HttpHeaders httpHeaders) {
        onlineUser++;
        this.session = session;
        this.uuid = parameterMap.getParameter("uuid");
        System.out.println(uuid+"ssssssssssssssssssssssssssssssssssssssssssss"+parameterMap.getParameter("uuid"));
        System.out.println(uuid+"ssssssssssssssssssssssssssssssssssssssssssss"+parameterMap.getParameter("cmd"));
        System.out.println(uuid+"ssssssssssssssssssssssssssssssssssssssssssss"+parameterMap.getParameter("groupId"));
        System.out.println(uuid+"ssssssssssssssssssssssssssssssssssssssssssss"+parameterMap.getParameter("groupIdss"));
        System.out.println(session);
        System.out.println(httpHeaders.get("uuid")+"");
        //把自己的信息加入map
        sessionMap.put(this.uuid,session);
        //设置uid
//        session.setAttribute(key,"uid001");
        //获取uid
//        String uid = session.getAttribute(key);
        //通知在线用户
        sendListMessage(onlineOrOffline(Constant.CMD_ENUM.ONLINE.getCmd(),this.uuid), null);
        log.info("有新连接加入！当前在线人数为：" + onlineUser);
    }

    /**
     * 连接关闭调用的方法
     * */
    @OnClose
    public void onClose() {
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
                json.put("timestamp", DateUtil.nowDate());
                session.sendText(message.toString());
                //存储
                instancemessagedayService.insertSelective(new Instancemessageday(json));
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
        for (byte b : bytes) {
            System.out.println(b);
        }
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
     *
     *
     * 在推送的场景(对多个会话发送一样的消息)时,使用堆外内存能非常简单高效的提升很大的性能(并发越大,和tomcat差距越明显).
     *
     *             // 二进制推送 模板代码 (字符串也基本一样)
     *             // byte[] dataByte  数据
     *             // sessions 为 List<Session>
     *             ByteBuf buf = PooledByteBufAllocator.DEFAULT.buffer(dataByte.length).writeBytes(dataByte);
     *             try {
     *                 for (Session session : sessions) {
     *                     if (session.isWritable()) {
     *                         session.sendBinary(buf.retainedDuplicate());
     *                     }
     *                 }
     *             } catch (Exception e) {
     *                 e.printStackTrace();
     *             } finally {
     *                 ReferenceCountUtil.release(buf);
     *             }
     *
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
                        ol.sendText(message.toString());
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
                        list.add(new Instancemessageday(json));
                    }
                });
            }
            if (list.size() > 0) {
//                instancemessagedayService = context.getBean(InstancemessagedayService.class);
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
