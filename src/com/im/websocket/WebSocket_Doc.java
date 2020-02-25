package com.im.websocket;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import com.google.gson.Gson;
import com.im.domain.BaseBean;

@ServerEndpoint(value = "/websocketdoc")  
public class WebSocket_Doc{
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private static Session session;
    

    BaseBean data = new BaseBean(); // 基类对象，回传给客户端的json对象
    Gson gson = new Gson();
    String json =null;
    /**
     * 连接建立成功调用的方法
     * @param session  可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     * @throws Exception 
     */
    @OnOpen
    public void onOpen(Session session) throws Exception{
    	WebSocket_Doc.session = session;
    	WebSocketMapUtil_Doc.put(session.getQueryString(),this);
        sendMessageToUser(session.getQueryString(), "上线成功！");
        System.out.println(getSessionId());
    }
     
    /**
     * 连接关闭调用的方法
     * @throws Exception 
     */
    @OnClose
    public void onClose() throws Exception{
    	WebSocketMapUtil_Doc.remove(session.getQueryString());
    }
     
    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     * @throws IOException 
     */
    @OnMessage
    
    public void onMessage(String message, Session session) throws IOException {
        WebSocket_Doc sockettest= ((WebSocket_Doc) WebSocketMapUtil_Doc.get(session.getQueryString()));
		if(sockettest != null){
			MyWebSocket my = new MyWebSocket();
			if(message.equals("next")) {
//				WebSocketMapUtil.remove(session.getQueryString());
//		    	WebSocketMapUtil.queue.poll();
		    	String next = null;
		    	 next = WebSocketMapUtil.queue.poll();
		    	if(next != null) {
		    		sendMessageToUser(session.getQueryString(), next+"向您发送了接诊邀请！");
		    		my.sendMessageToUser(next, "到你啦！");
		    		my.sendMessageToUser(next, "等待医生接受邀请，请等待！");
		    		next = null;
		    	}else {
		    		sendMessageToUser(session.getQueryString(), "当前没有人在挂号，请稍等！");
		    	}
			}
			else {
				
				String show = WebSocketMapUtil.show();
				sendMessageToUser(session.getQueryString(), "当前排队人数："+String.valueOf(my.getCount()));
				sendMessageToUser(session.getQueryString(), "队列："+show);
			}
		}
    }
     
    /**
     * 发生错误时调用
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error){
        error.printStackTrace();
        sendMessageToUser(session.getQueryString(), "Doc:    "+error.getMessage());
    }
     
    
    /**
     * 发送消息方法。
     * @param message
     * @throws IOException
     */
    public void sendMessage(String message) throws IOException{
    	
		data.setCode(0);
		data.setMsg(message);
    	
		json = gson.toJson(data);
		
        WebSocket_Doc.session.getBasicRemote().sendText(json);
    }
    
    /**
     * 群发消息方法。
     * @param message
     * @throws IOException
     */
    public static void sendMessageAll(String message) throws IOException{
    	for(WebSocket_Doc sockettest : WebSocketMapUtil_Doc.getValues()){
    		sockettest.sendMessage(message);
    	}
    }
    
    public static int getCount() {
    	
		return WebSocketMapUtil_Doc.getValues().size();
    	
    }
    
    public static void sendMessageToUser(String id,String message) {
    	WebSocket_Doc sockettest;
    	try {
    		
    		sockettest = WebSocketMapUtil_Doc.get(id);
    		sockettest.sendMessage(message);
    	}
    	catch(Exception ee) {
    		System.out.println("出错啦");
    		ee.printStackTrace();
    	}
    	
    }
    
    
    public static Session getSession() {
    	return session;
    }
    
    public static String getSessionId() {
    	return session.getQueryString();
    }
}
