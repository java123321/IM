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
import com.im.domain.UserBean;

@WebServlet("/UpdateDrugInformation")
public class UpdateDrugInformation extends HttpServlet {

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
		// 获取客户端传过来的参数
		String name = request.getParameter("name");
		String price = request.getParameter("price");
		String type = request.getParameter("type");
		String describe = request.getParameter("describe");
		String amount = request.getParameter("amount");
		//String picture = request.getParameter("picture");
		String index = request.getParameter("index");
		response.setContentType("text/html;charset=utf-8");
		if (name == null || name.equals("") || price == null || price.equals("") || type == null || type.equals("") || describe == null || describe.equals("")
				 || amount == null || amount.equals("") || index == null || index.equals("")) {
			System.out.println("参数为空");
			return;
		} 
		String sql = "update im_drug set Drug_Name='"+name+"',Drug_Price='"+price+"', Drug_Type='"+type+"', Drug_Describe='"+describe+"', Drug_Amount='"+amount+"'where Drug_Index='"+index+"'";
		// 请求数据库
		DBUtils dbUtils = new DBUtils();
		dbUtils.openConnect();
		// 打开数据库连接
		BaseBean data = new BaseBean(); // 基类对象，回传给客户端的json对象
		UserBean userBean = new UserBean(); // user的对象
		if (!dbUtils.isExistInDB_Drug(name)) {
			
			data.setCode(-1);
			data.setData(userBean);
			data.setMsg("该药品不存在，请先添加");
			
			
			
		} else if (dbUtils.updateDataToDB(sql)) {
			
			data.setCode(0);
			data.setMsg("修改成功!!");
		}else {
			data.setCode(0);
			data.setMsg("修改失败!!");
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