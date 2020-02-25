package com.im.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.im.db.*;
import com.im.domain.BaseBean;
import com.im.domain.UserBean;

public class IsExist_Register extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	DBUtils a = new DBUtils();
	String no;
	
	BaseBean data = new BaseBean(); // 基类对象，回传给客户端的json对象
	UserBean userBean = new UserBean(); // user的对象
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("request--->"+request.getRequestURL()+"===="+request.getParameterMap().toString());
		no = request.getParameter("no"); // 获取客户端传过来的参数
		
		response.setContentType("text/html;charset=utf-8");
		
		a.openConnect();
		// 打开数据库连接
		
		if (no == null || no.equals("")) {
			System.out.println("用户名为空");
			data.setCode(1);
			data.setMsg("用户名为空!!");
			return;
		}
		
		
	
		if(a.isExistInDB_Register(no)) {
			data.setCode(-1);
			data.setMsg("用户名已存在!!");
		}
		else {
			data.setCode(0);
			data.setMsg("用户名未存在!!");
		}
		Gson gson = new Gson();
		String json = gson.toJson(data);
		// 将对象转化成json字符串
		try {
			response.getWriter().println(json);
			// 将j son数据传给客户端
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			response.getWriter().close(); // 关闭这个流，不然会发生错误的
		}
	}
}