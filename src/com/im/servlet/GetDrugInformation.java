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


import com.im.db.*;
import com.im.domain.BaseBean;
import com.im.domain.UserBean;
@WebServlet(name="GetDrugInformation",urlPatterns = {"/GetDrugInformation"})
public class GetDrugInformation extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	DBUtils a = new DBUtils();
	String start = null;
	String count = null;
	String type = null;
	String name0 = null;
	String name = null;
	String sum;
	String code = "-1";
	String msg = "错误！！";;
	BaseBean data = new BaseBean(); // 基类对象，回传给客户端的json对象
	UserBean userBean = new UserBean(); // user的对象
	ResultSet rs;
	ResultSet rs2;
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		doPost(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("utf-8");
		System.out.println("request--->"+request.getRequestURL()+"===="+request.getParameterMap().toString());
		start = request.getParameter("start"); // 获取客户端传过来的参数
		count = request.getParameter("count");
		type = request.getParameter("type");
		name0 = request.getParameter("name");
		try {
			name = a.hexStr2Str(name0);
		}
		catch(Exception e1){
			System.out.println(e1.toString());
		}
		response.setContentType("text/html;charset=utf-8");
		if (start == null || start.equals("") || count == null || count.equals("")) {
			System.out.println("参数为空");
			return;
		}
		a.openConnect(); 
		// 打开数据库连接
		
		int s = Integer.parseInt(start); 
		s-=1;//使客户端从1开始计数，便于理解(统计药品的数目)
		int c = Integer.parseInt(count); 
		int e = s+c;
		if (s >= c || s < 0 || c < 0) {
			System.out.println("参数错误！！");
			data.setCode(100);
			data.setMsg("参数错误!!");
		}
		
		
		String sql = null;
		String sql2 = null;
		if(type == null && name == null) {
			sql = "select * from im_drug limit "+s+","+c+";";
			sql2 = "select count(*) from im_drug;";
			name = null;
			type = null;
		}
		else if(type == null) {
			sql = "select * from im_drug where Drug_Name like '"+"%"+name+"%"+"' limit "+s+","+c+";";
			sql2 = "select count(*) from im_drug where Drug_Name like '"+"%"+name+"%"+"';";
			name = null;
			type = null;
		}
		else if(name == null) {
			sql = "select * from im_drug where Drug_Type = '"+type+"' limit "+s+","+c+";";
			sql2 = "select count(*) from im_drug where Drug_Type = '"+type+"';";
			type = null;
			name = null;
		}
		else {
			sql = "select * from im_drug where Drug_Type = '"+type+"' and Drug_Name like '"+"%"+name+"%"+"' limit "+s+","+c+";";
			sql2 = "select count(*) from im_drug where Drug_Name like '"+"%"+name+"%"+"';";
			name = null;
			type = null;
		}
		System.out.println(sql);
		rs = a.getData(sql);
		rs2 = a.getData(sql2);
		try {
			
			if(rs2.next()) {
				sum = rs2.getString("count(*)");
			}
			if(e > Integer.parseInt(sum))
				e = Integer.parseInt(sum);
				
			data.setCode(0);
			data.setMsg("数据库共有"+sum+"条数据，现在是第"+start+"~"+e+"条数据!!");
			code = "0";
			msg = "数据库共有"+sum+"条数据，现在是第"+start+"~"+e+"条数据!!";
			
		}
		catch(Exception e2){
			System.out.println("00");
			data.setCode(-1);
			data.setMsg("获取失败!!");
			code = "-1";
			msg = "获取失败!!";
		}
		finally {
		
		}
		
		String Drug = null;
		try {
			Drug = a.resultSetToJson(rs,code,msg);
		} catch (SQLException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (JSONException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		
		//Gson gson = new Gson();
		String json = Drug;
		// 将对象转化成json字符串
		
		try {
			response.getWriter().println(json);
			// 将j son数据传给客户端
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			response.getWriter().close(); // 关闭这个流，不然会发生错误的
		}
	}
	
	
	
}
