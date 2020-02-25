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

public class PasswordModify extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	DBUtils a = new DBUtils();
	String no;
	String pwd;
	String isStu;
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
		pwd = request.getParameter("pwd");
		isStu = request.getParameter("isStu");
		response.setContentType("text/html;charset=utf-8");
		
		if(isStu.equals("true")) {
			if (no == null || no.equals("") || pwd == null || pwd.equals("")) {
				System.out.println("用户名或密码为空");
				return;
			}
			a.openConnect();
			// 打开数据库连接
			
		
			if(a.isExistInDB(no, pwd)) {
				data.setCode(-1);
				
				data.setMsg("密码重复!!");
			}
			else if(!a.pwdModify(no,pwd,isStu)){
				data.setCode(0);
				data.setMsg("修改成功!!");
				
				userBean.setNo(no);
				userBean.setPwd(pwd);
			}
			else {
				data.setCode(100);
				data.setMsg("未知错误!!");
			}
		}
		else if(isStu.equals("false")) {
			if (no == null || no.equals("") || pwd == null || pwd.equals("")) {
				System.out.println("用户名或密码为空");
				return;
			}
			a.openConnect();
			// 打开数据库连接
			
		
			if(a.isExistInDB_Doc(no, pwd)) {
				data.setCode(-1);
				
				data.setMsg("密码重复!!");
			}
			else if(!a.pwdModify(no,pwd,isStu)){
				data.setCode(0);
				data.setMsg("修改成功!!");
				
				userBean.setNo(no);
				userBean.setPwd(pwd);
			}
			else {
				data.setCode(100);
				data.setMsg("未知错误!!");
			}
		}
		else {
			return;
		}
		
		
		data.setData(userBean);
		
		
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
