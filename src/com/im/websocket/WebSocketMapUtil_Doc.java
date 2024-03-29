package com.im.websocket;


import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

public class WebSocketMapUtil_Doc {
	
	public static ConcurrentMap<String, WebSocket_Doc> webSocketMap = new ConcurrentHashMap<>();
    	
    public static String show() {
    	String str = "";
		for(String key : webSocketMap.keySet()) {
			str = str + key + "---";
		}
		return str;
    }
 
    public static WebSocket_Doc get(String key){
    	 return webSocketMap.get(key);
    }
    	 
	
	public static Collection<WebSocket_Doc> getValues(){
		return webSocketMap.values();
	}
	
 
}

