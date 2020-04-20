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


@WebServlet("/UpdateInformation")
public class UpdateInformation extends HttpServlet {
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	doPost(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub

		DBUtils a = new DBUtils();
		System.out.println("request--->"+request.getRequestURL()+"===="+request.getParameterMap().toString());
		response.setContentType("text/html;charset=utf-8");
		String no=request.getParameter("no");
		String sex = new String (request.getParameter("sex").getBytes("ISO-8859-1"),"UTF-8");
		String name=new String (request.getParameter("name").getBytes("ISO-8859-1"),"UTF-8");
		String isStu = request.getParameter("isStu");
		
		BaseBean data = new BaseBean(); // 基类对象，回传给客户端的json对象
		a.openConnect();
		if(isStu.equals("true")) {
			String birth = request.getParameter("birth");
			String height = request.getParameter("height");
			String weight = request.getParameter("weight");
			if (sex == null || name == null || birth == null || height == null || weight == null || no == null ) {
				System.out.println("参数为空！");
				return;
			} 
			try {
				String sql = "update im_stu set Stu_Name = '"+name+"',Stu_Sex = '"+sex+"',Stu_Birth = '"+birth+"',Stu_Height = '"+height+"',Stu_Weight = '"+weight+"' where Stu_No = '"+no+"';";
				a.updateDataToDB(sql);
				data.setCode(0);
				data.setMsg("用户信息更新成功！");
			}
			catch(Exception ee) {
				ee.printStackTrace();
				data.setCode(-1);
				data.setMsg("用户信息更新失败！");
			}
		}
		else if(isStu.equals("false")) {
			String title = new String (request.getParameter("title").getBytes("ISO-8859-1"),"UTF-8");
			String introduce = new String (request.getParameter("introduce").getBytes("ISO-8859-1"),"UTF-8");
			String offices =new String (request.getParameter("offices").getBytes("ISO-8859-1"),"UTF-8");
			String type = new String (request.getParameter("type").getBytes("ISO-8859-1"),"UTF-8");
			String state =new String (request.getParameter("state").getBytes("ISO-8859-1"),"UTF-8");
			if(no == null || name == null || state == null || type == null) {
				System.out.println("参数为空！");
				return;
			}
			// 打开数据库连接
			try {
				String sql = "update im_doc set Doc_Name = '"+name+"',Doc_Sex = '"+sex+"',Doc_Offices = '"+offices+"',Doc_Title = '"+title+"',Doc_Introduce = '"+introduce+"',Doc_State = '"+state+"',Doc_Type = '"+type+"' where Doc_No = '"+no+"';";
				a.updateDataToDB(sql);
				data.setCode(0);
				data.setMsg("用户信息更新成功！");
			}
			catch(Exception ee) {
				ee.printStackTrace();
				data.setCode(-1);
				data.setMsg("用户信息更新失败！");
			}
		}
		else {
			return;
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

}
