package com.im.servlet;

import java.io.IOException;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.im.db.DBUtils;
import com.im.domain.BaseBean;

@WebServlet("/UpdateAddress")
public class UpdateAddress extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBUtils a = new DBUtils();		
			response.setContentType("text/html;charset=utf-8");
			
//			String name = new String(request.getParameter("name").getBytes("ISO-8859-1"), "UTF-8");
		String name = new String(request.getParameter("name").getBytes("ISO-8859-1"),"UTF-8");
		String address =new String(request.getParameter("address").getBytes("ISO-8859-1"),"UTF-8");
		String phone = request.getParameter("phone");
		String stuId=request.getParameter("stuId");
		System.out.println("update.address.name:"+name);
		System.out.println("update.address.phone:"+phone);
		System.out.println("update.address.address:"+address);
		
		if (address == null || name == null || phone == null) {
			System.out.println("参数为空！");
			return;
		} 
			
		BaseBean data = new BaseBean(); // 基类对象，回传给客户端的json对象
		a.openConnect();
		// 打开数据库连接
		
		
		try {
			a.updateDataToDB_Adress(name, address, phone,stuId);
			data.setCode(0);
			data.setMsg("地址更新成功！");
		}
		catch(Exception ee) {
			
			ee.printStackTrace();
			data.setCode(-1);
			data.setMsg("地址更新失败！");
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
		a.closeConnect(); // 关闭数据库连接}
	}



	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
