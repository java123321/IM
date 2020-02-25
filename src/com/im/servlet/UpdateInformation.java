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
	private static final long serialVersionUID = 1L;
    

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		DBUtils a = new DBUtils();

		
		System.out.println("request--->"+request.getRequestURL()+"===="+request.getParameterMap().toString());
		String no = request.getParameter("no");
		String name = request.getParameter("name");
		String sex = request.getParameter("sex");
		String birth = request.getParameter("birth");
		String height = request.getParameter("height");
		String weight = request.getParameter("weight");
		String state = request.getParameter("state");
		String type = request.getParameter("type");
		String isStu = request.getParameter("isStu");
		String offices = request.getParameter("offices");
		String introduce = request.getParameter("introduce");
		String title = request.getParameter("title");
		BaseBean data = new BaseBean(); // 基类对象，回传给客户端的json对象
		response.setContentType("text/html;charset=utf-8");
		try {
			name = a.hexStr2Str(name);
			sex = a.hexStr2Str(sex);
			offices = a.hexStr2Str(offices);
			introduce = a.hexStr2Str(introduce);
			title = a.hexStr2Str(title);
		}
		catch(Exception e1){
			System.out.println(e1.toString());
		}
		if(isStu.equals("true")) {
			if (sex == null || name == null || birth == null || height == null || weight == null || no == null ) {
				System.out.println("参数为空！");
				return;
			} 
			
			
			
			
			
			a.openConnect();
			// 打开数据库连接
			
			
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
			if(no == null || name == null || state == null || type == null) {
				System.out.println("参数为空！");
				return;
			}
			
		
			a.openConnect();
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



	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
