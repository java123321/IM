package com.im.websocket;


import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;
import com.im.domain.BaseBean;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;


/**
 * @ServerEndpoint 注解是一个类层次的注解，它的功能主要是将目前的类定义成一个websocket服务器端,
 * 注解的值将被用于监听用户连接的终端访问URL地址,客户端可以通过这个URL来连接到WebSocket服务器端
 * @ServerEndpoint 可以把当前类变成websocket服务类
 */
@ServerEndpoint("/message/{userno}")
public class Message {

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。若要实现服务端与单一客户端通信的话，可以使用Map来存放，其中Key可以为用户标识
    private static ConcurrentHashMap<String, Message> webSocketSet = new ConcurrentHashMap<String, Message>();
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session WebSocketsession;
    //当前发消息的人员编号
    private String userno = "";
    BaseBean data = new BaseBean(); // 基类对象，回传给客户端的json对象
    Gson gson = new Gson();
    String json =null;


    /**
     * 连接建立成功调用的方法
     *
     * @param session 可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    @OnOpen
    public void onOpen(@PathParam(value = "userno") String param, Session WebSocketsession, EndpointConfig config) {
        userno = param;//接收到发送消息的人员编号
        System.out.println("chat interface userno:"+userno);
        this.WebSocketsession = WebSocketsession;
        webSocketSet.put(param, this);//加入map中
        System.out.println("Message有新连接加入！当前在线人数为" + webSocketSet.size());
        System.out.println("message.open.id:"+userno);
        try {
			sendMessage("上线成功!");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }


    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (!userno.equals("")) {
            webSocketSet.remove(userno);  //从set中删除
            System.out.println("有一连接关闭！当前在线人数为" + webSocketSet.size());
            System.out.println("Message.closed.id:"+userno+"-----------------------------------------------------------------------------------------");
        }
    }


    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @SuppressWarnings("unused")
//	@OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("message.from.client:" + message);
            //给指定的人发消息
            sendToUser(message);
        
    }


    /**
     * 给指定的人发送消息
     * @param message
     */
    @OnMessage
    public void sendToUser(String message) {
        String sendUserno = message.split("[|]")[0];
        String sendMessage = message.split("[|]")[1];
        String now = getNowTime();
        System.out.println("message.sendmessage.aimnois:"+sendUserno);
        try {
        	Message msgObject=webSocketSet.get(sendUserno);
        	//如果要聊天的人在线，则给他发送消息
            if (msgObject != null) {
            	msgObject.sendMessage(sendMessage);          
            	System.out.println("chat:当前用户在线");
               // webSocketSet.get(sendUserno).sendMessage(now + "用户" + userno + "发来消息：" + sendMessage);
                //webSocketSet.get(userno).sendMessage(now + "向用户" + sendUserno + "发送消息：" + sendMessage);
            } else {            	
            	webSocketSet.get(userno).sendMessage("当前用户不在线!");
                System.out.println("chat:当前用户不在线");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取当前时间
     *
     * @return
     */
    private String getNowTime() {
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(date);
        return time;
    }
    /**
     * 发生错误时调用
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("message.occure.error.id"+userno);
        error.printStackTrace();
    }


    /**
     * 这个方法与上面几个方法不一样。没有用注解，是根据自己需要添加的方法。
     *
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException {
    	data.setCode(0);
		data.setMsg(message);
    	
		json = gson.toJson(data);
        this.WebSocketsession.getBasicRemote().sendText(json);
        //this.session.getAsyncRemote().sendText(message);
    }

}