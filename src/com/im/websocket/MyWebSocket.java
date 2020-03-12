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

@ServerEndpoint(value = "/websocket")  
public class MyWebSocket{
    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;
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
        this.session = session;
        System.out.println("the id is"+session.getQueryString());
        if(session.getQueryString().equals(WebSocketMapUtil.queue.peek())) {
        	sendMessageToUser(session.getQueryString(), "请勿重复点击挂号！");
        }
        else {
        	WebSocketMapUtil.put(session.getQueryString(),this);
        	if(WebSocket_Doc.getCount() == 0) {
            	sendMessageToUser(session.getQueryString(), "当前没有医生在线，请稍后再来！");
            	WebSocketMapUtil.remove(session.getQueryString());
            	WebSocketMapUtil.queue.poll();
            }
        	else {
		        sendMessageToUser(session.getQueryString(), "挂号成功！");
		        sendMessageToUser(session.getQueryString(), "您当前排队位次为" + getCount());
		        sendMessageToUser(session.getQueryString(), "当前医生在线人数：" + WebSocket_Doc.getCount() + "人");
		        
//		        if(WebSocketMapUtil.queue.peek() == session.getQueryString()) {
//		        	
//		        	sendMessageToUser(session.getQueryString(), "到你啦！");
//		        	sendMessageToUser(session.getQueryString(), "等待医生接受邀请，请等待！");
//		        	//sendMessageToUser(sessionDoc.getQueryString(), WebSocketMapUtil.queue.peek() + "向您发送了接诊邀请！");
//		        	if(session.isOpen()) {
//		        		try {
//		        			WebSocket_Doc.sendMessageToUser(WebSocket_Doc.getSessionId(),session.getQueryString()+"向您发送了接诊邀请！");
//		        		}
//		        		catch(Exception eee){
//		        			sendMessageToUser(session.getQueryString(), "当前没有医生在线，请稍后再来！");
//		        		}
//		        	}
//		        }
        	}
        }
    }
     
    /**
     * 连接关闭调用的方法
     * @throws Exception 
     */
    @OnClose
    public void onClose() throws Exception{
    
    	//将关闭的socket会话从map和queue中移除
    	String id=session.getQueryString();
    	WebSocketMapUtil.remove(id);
    	WebSocketMapUtil.queue.remove(id);
    	System.out.println("the remove id is "+session.getQueryString());
    	
    	int i=1;
    	for(String stuId:WebSocketMapUtil.queue) {
    		WebSocketMapUtil.get(stuId).sendMessageToUser(stuId, "您当前排队位次为"+i);
    		i++;
    	}

    }
     
    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     * @throws IOException 
     */
    @OnMessage
    
    public void onMessage(String message, Session session) throws IOException {
    	String id=session.getQueryString();
        MyWebSocket myWebSocket= ((MyWebSocket) WebSocketMapUtil.get(id));
        if(myWebSocket != null){
			//myWebSocket.sendMessage("挂号成功！");
			//myWebSocket.sendMessageAll("挂号人数：" + getCount() + "人");
        	//如果用户发送了取消，则将他从队列中拿出来
//        	if(message.equals("cancel")) {
//        		WebSocketMapUtil.remove(id);
//        		myWebSocket.sendMessage("取消挂号成功");
//        		System.out.println("the stu persion is"+getCount());
//        	}
//        	
        		String show = WebSocketMapUtil.show();
        		myWebSocket.sendMessage(show);
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
        sendMessageToUser(session.getQueryString(), error.getMessage());
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
        this.session.getBasicRemote().sendText(json);
    }
    
    /**
     * 群发消息方法。
     * @param message
     * @throws IOException
     */
    public void sendMessageAll(String message) throws IOException{
    	for(MyWebSocket myWebSocket : WebSocketMapUtil.getValues()){
    		myWebSocket.sendMessage(message);
    	}
    }
    
    public int getCount() {
    	
		return WebSocketMapUtil.getValues().size();
    	
    }
    
    public void sendMessageToUser(String id,String message) {
    	MyWebSocket myWebSocket;
    	try {
    		
    		myWebSocket = WebSocketMapUtil.get(id);
    		myWebSocket.sendMessage(message);
    	}
    	catch(Exception ee) {
    		System.out.println("出错啦");
    		ee.printStackTrace();
    	}
    	
    }
    
    public Session getStuSession() {
    	return session;
    }
}
