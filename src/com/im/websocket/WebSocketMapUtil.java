package com.im.websocket;


import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

public class WebSocketMapUtil {
	public static ConcurrentMap<String, MyWebSocket> webSocketMap = new ConcurrentHashMap<>();
	public static Queue<String> queue =new LinkedBlockingQueue();
    public static void put(String key, MyWebSocket myWebSocket){
    	webSocketMap.put(key, myWebSocket);
    	queue.offer(key);
    }
    	
    public static String show() {
    	String str = "";
		for(String key : webSocketMap.keySet()) {
			str = str + key + "---";
		}
		return str;
    }
 
    public static MyWebSocket get(String key){
    	 return webSocketMap.get(key);
    }
    	 
	public static void remove(String key){
		 webSocketMap.remove(key);
	}
	
	public static Collection<MyWebSocket> getValues(){
		return webSocketMap.values();
	}
}

