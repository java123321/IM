package com.im.servlet;

import java.io.IOException;
import java.sql.ResultSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.im.db.DBUtils;
import com.im.websocket.WebSocketMapUtil;
import com.im.websocket.WebSocketMapUtil_Doc;
import com.mysql.cj.jdbc.result.ResultSetMetaData;

/**
 * Servlet implementation class GetOnlineStu
 */
@WebServlet("/GetOnlineStu")
public class GetOnlineStu extends HttpServlet {

protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int i=1;
		String sql;
		JSONArray array;
		DBUtils a = new DBUtils();
		response.setContentType("text/html;charset=utf-8");
		array = new JSONArray();
		System.out.println("start.array.value:"+array.toString());
		System.out.println("start.i.value:"+i);
		i++;
		a.openConnect();
		//获取在线学生的所有id
		System.out.println("getonlinestu.stusize:"+WebSocketMapUtil.queue.size());
		
	for(String stuId : WebSocketMapUtil.queue) {
		sql="select Stu_Name,Stu_Sex,Stu_Birth,Stu_Height,Stu_Weight,Stu_Icon,Stu_Phone,Stu_Address from im_stu where Stu_No='"+stuId+"'";
		System.out.println("onlinestu"+sql);
		System.out.println("getonlinestu.count.i:"+i);
		
		ResultSet rs=a.getData(sql);
		//将获取的数据集存放到json数组中
			handleRS(rs,array);
	}
	System.out.println("thestusize"+ WebSocketMapUtil.queue.size());
	if(array.length()==0) {
		JSONObject jsonObj = new JSONObject();
		try {
			jsonObj.put("#x", "当前没有学生问诊");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		array.put(jsonObj);
	}
	//json数组中的在线医生信息返回给客户端
	String result=array.toString();
	try {
		response.getWriter().println(result);
		// 将j son数据传给客户端
	} catch (Exception e) {
		e.printStackTrace();
	} finally {
		response.getWriter().close(); // 关闭这个流，不然会发生错误的
	}
	System.out.println("在线学生为"+result);
	a.closeConnect(); // 关闭数据库连接}
	}
	
	//将获取的数据集存放到json数组中
	private void handleRS(ResultSet rs,JSONArray array) {
		  // 获取列数
	       try {
	       ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();
	       int columnCount = metaData.getColumnCount();
	      System.out.println("get.online.stu.before.handle.rs:"+array.toString());

	       // 遍历ResultSet中的每条数据     
	        while (rs.next()) {
	        	JSONObject jsonObj = new JSONObject();
	            // 遍历每一列
	            for (int i = 1; i <= columnCount; i++) {
	                String columnName =metaData.getColumnLabel(i);
	                String value = rs.getString(columnName);
	                jsonObj.put(columnName, value);            
	            } 
	            array.put(jsonObj); 
	        }
	       }
	       catch(Exception ee) {
	    	   System.out.println(ee.getMessage()+"122"+ee.toString());
	    	   //System.out.println("122");
	       }
	}


}
