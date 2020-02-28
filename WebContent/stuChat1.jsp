<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>
<!DOCTYPE HTML>
<html>
  <head>
    <base href="<%=basePath%>">
    <title>stuChat1</title>
  </head>
   
  <body>
    Welcome<br/>
    <input id="text" type="text" />
    <button onclick="register()">上线</button>
    <button onclick="send()">Send</button>    
    <button onclick="closeWebSocket()">Close</button>
    <div id="message">
    </div>
  </body>
   
  <script type="text/JavaScript">
      var websocket = null;
      //判断当前浏览器是否支持WebSocket
      if('WebSocket' in window){
          
      }
      else{
          alert('不支持！');
      }
       
      function register(){
    	  websocket = new WebSocket("ws://localhost:8080/IM/message/12345");
   
    //连接发生错误的回调方法
      websocket.onerror = function(){
          setMessageInnerHTML("error");
      };
      
      
      //连接发生错误的回调方法
      websocket.onerror = function(){
          //setMessageInnerHTML("error");
      };
       
      //连接成功建立的回调方法
      websocket.onopen = function(event){
          //setMessageInnerHTML("open");
      };
       
      //接收到消息的回调方法
      websocket.onmessage = function(event){
          setMessageInnerHTML(event.data);
      };
       
      //连接关闭的回调方法
      websocket.onclose = function(){
          //setMessageInnerHTML("close");
      };
       
      //监听窗口关闭事件，当窗口关闭时，主动去关闭websocket连接，防止连接还没断开就关闭窗口，server端会抛异常。
      window.onbeforeunload = function(){
          websocket.close();
      };
      }
       
      //将消息显示在网页上
      function setMessageInnerHTML(innerHTML){
          document.getElementById('message').innerHTML += innerHTML + '<br/>';
      }
       
      //关闭连接
      function closeWebSocket(){
          websocket.close();
      }
       
            //发送消息
      function send(){
          var message = document.getElementById('text').value;
          websocket.send(message);
      }
  </script>
</html>
