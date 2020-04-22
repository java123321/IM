package com.im.websocket;

import java.io.IOException;
import java.sql.ResultSet;

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
	String stuId;
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
        stuId=session.getQueryString();
        System.out.println("the id is"+stuId);
        	//如果当前医生数量为零，则通知学生当前医生数量为零，让他稍后再来
        	if(WebSocket_Doc.getCount() == 0) {
            	sendMessage("当前没有医生在线，请稍后再来！");
            }
        	else {//如果有医生在线
        		//如果当前学生不在队列中
        		if(!WebSocketMapUtil.webSocketMap.containsKey(stuId)) {
        			//将学生放入队列中
        			WebSocketMapUtil.put(stuId, this);          			
//        			sendMessageToUser(stuId, "挂号成功！");
//		        sendMessageToUser(stuId, "您当前排队位次为" + getCount());
//		        sendMessageToUser(stuId, "当前医生在线人数：" + WebSocket_Doc.getCount() + "人");//		        
		        sendMessage("挂号成功！");
		        sendMessage("您当前排队位次为" + WebSocketMapUtil.queue.size());
		        sendMessage("当前医生在线人数：" + WebSocket_Doc.getCount() + "人");		        		   
		        //给医生发通知更新挂号学生信息
		        updateStuNumber();
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
    	WebSocketMapUtil.remove(stuId);
//    	WebSocketMapUtil.queue.remove(stuId);
    	System.out.println("the remove id is "+stuId);
    	
    	//当学生被接诊或者取消挂号之后，更新剩余学生位次
    	updateStuRank();
    	System.out.println("mywebsocket.close.updateStuRank");
    	//给医生发通知，更新当前挂号排队学生
    	updateStuNumber();

    }
    private void updateStuRank() {
    	int i=1;
    	for(String stuId:WebSocketMapUtil.queue) {
    		WebSocketMapUtil.get(stuId).sendMessageToUser(stuId, "您当前排队位次为"+i);
    		i++;
    	}
    }

    
     
    //该方法用来给医生发通知更新当前挂号学生信息
    private void updateStuNumber() {
    	   //获取当前挂号学生数量
		int stuNumber=WebSocketMapUtil.webSocketMap.size();
		System.out.println("the guahao size is:"+stuNumber);
         //当学生挂号成功之后，开始向所有在线的医生发通知开始更新挂号学生
    	for(String docId : WebSocketMapUtil_Doc.webSocketMap.keySet()) {
    		WebSocket_Doc docWebSocket=WebSocketMapUtil_Doc.get(docId);
    		try {
				docWebSocket.sendMessage("updateStu"+String.valueOf(stuNumber));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//给在线医生发送更新挂号学生的消息
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
    	sendMessage(message);		
    }
     
    /**
     * 发生错误时调用
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error){
        error.printStackTrace();
        sendMessageToUser(stuId, error.getMessage());
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
    

    
//    public int getCount() {
//    	
//		return WebSocketMapUtil.getValues().size();
//    	
//    }
    
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
