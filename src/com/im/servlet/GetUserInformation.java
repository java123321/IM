package com.im.servlet;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;


import com.im.db.DBUtils;
import com.im.domain.BaseBean;


@WebServlet(name="GetUserInformation",urlPatterns = {"/GetUserInformation"})
public class GetUserInformation extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doPost(request, response);
	}	
	
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ResultSet rs = null;
		DBUtils a = new DBUtils();
		String code = "500";
		String msg = "未初始化！";
		String info = null;
		
		
		System.out.println("request--->"+request.getRequestURL()+"===="+request.getParameterMap().toString());
		String name = request.getParameter("name");
		String type = request.getParameter("type");
		response.setContentType("text/html;charset=utf-8");
		if (name == null) {
			System.out.println("参数为空！");
			return;
		} // 请求数据库
		
		a.openConnect();
		// 打开数据库连接
		BaseBean data = new BaseBean(); // 基类对象，回传给客户端的json对象
		String sql = null;
		if(type.equals("Stu")) {
			sql = "select * from im_stu,im_time where Stu_No = '"+name+"';";
			name = null;
			type = null;
		}
		else if(type.equals("Doc")) {
			sql = "select * from im_doc where Doc_No = '"+name+"';";
			name = null;
			type = null;
		}				
		try {
			System.out.println(sql);
			rs = a.getData(sql);
			
			data.setCode(0);
			data.setMsg("获取成功！");
			code = "0";
			msg = "获取成功！";
		}
		catch(Exception ee) {
			System.out.println("获取数据出错啦！");
			ee.printStackTrace();
			code = "-1";
			msg = "获取失败！";
		}
		
		
		try {
			info = a.resultSetToJson(rs,code,msg);
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		
		String json = info;
		
		try {
			response.getWriter().println(json);
			// 将j son数据传给客户端
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			response.getWriter().close(); // 关闭这个流，不然会发生错误的
		}
		a.closeConnect(); // 关闭数据库连接}
	}
	

}