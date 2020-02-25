package com.im.servlet;

import java.io.IOException;
import java.sql.ResultSet;



import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.google.gson.Gson;
import com.im.db.DBUtils;
import com.im.domain.BaseBean;
import com.im.domain.UserBean;


public class LoginDataServlet extends HttpServlet {


	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doPost(request, response);
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		System.out.println("request--->"+request.getRequestURL()+"===="+request.getParameterMap().toString());
		String no = request.getParameter("no"); // 获取客户端传过来的参数
		String name = request.getParameter("name");
		String pwd = request.getParameter("pwd");
		String sex = request.getParameter("sex");
		String birth = request.getParameter("birth");
		String height = request.getParameter("height");
		String weight = request.getParameter("weight");
		String sno = request.getParameter("sno");
		response.setContentType("text/html;charset=utf-8");
		if (no == null || no.equals("") || pwd == null || pwd.equals("")) {
			System.out.println("用户名或密码为空");
			return;
		} // 请求数据库
		DBUtils dbUtils = new DBUtils();
		dbUtils.openConnect();
		// 打开数据库连接
		BaseBean data = new BaseBean(); // 基类对象，回传给客户端的json对象
		UserBean userBean = new UserBean(); // user的对象
		if (dbUtils.isExistInDB(no, pwd)) {
			// 判断账号是否存在
			data.setCode(-1);
			data.setData(userBean);
			data.setMsg("该账号已存在");
		} else if (!dbUtils.insertDataToDB(no, name, pwd, sex, birth, height, weight, sno)) {
			// 注册成功
			data.setCode(0);
			data.setMsg("注册成功!!");
			ResultSet rs = dbUtils.getUser();
			
			if (rs != null) {
				try {
					
					
					userBean.setName(name);
					userBean.setSex(sex);
					userBean.setBirth(birth);
					userBean.setHeight(height);
					userBean.setWeight(weight);
					userBean.setSno(sno);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			userBean.setNo(no);
			userBean.setPwd(pwd);
			data.setData(userBean);
		} else {
			// 注册不成功，这里错误没有细分，都归为数据库错误
			data.setCode(500);
			data.setData(userBean);
			data.setMsg("数据库错误");
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
		dbUtils.closeConnect(); // 关闭数据库连接}
	}
	

}