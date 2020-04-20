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


@WebServlet("/GetDocInformation")
public class GetDocInformation extends HttpServlet {

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBUtils a = new DBUtils();
		ResultSet rs = null;	
		System.out.println("request--->"+request.getRequestURL()+"===="+request.getParameterMap().toString());
		String no = request.getParameter("no");
		String state = request.getParameter("state");
		BaseBean data = new BaseBean(); // 基类对象，回传给客户端的json对象
		response.setContentType("text/html;charset=utf-8");
		String sql = null;
		if((no == null || no.equals("")) && (state == null || state.equals(""))) {
			sql = "select * from im_doc;";
		}else if(no == null || no.equals("")) {
			sql = "select * from im_doc where Doc_State = '"+state+"';";
		}else if(state == null || state.equals("")) {
			sql = "select * from im_doc where Doc_No = '"+no+"';";
		}else {
			sql = "select * from im_doc where Doc_No = '"+no+"' and Doc_State = '"+state+"';";
		}
		
			a.openConnect();
			// 打开数据库连接
			
			
			try {				
				rs = a.getData(sql);
				data.setCode(0);
				data.setMsg("获取信息成功！");
			}
			catch(Exception ee) {		
				ee.printStackTrace();
				data.setCode(-1);
				data.setMsg("获取信息失败！");
			}			
			String Doc = null;
			try {
				Doc = a.resultSetToJson( rs, String.valueOf(data.getCode()), data.getMsg());
			} catch (SQLException | JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		
		String json = Doc;
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
