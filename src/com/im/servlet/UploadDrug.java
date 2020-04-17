package com.im.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.im.db.DBUtils;
import com.im.domain.BaseBean;
import com.im.domain.UserBean;

@WebServlet("/UploadDrug")
public class UploadDrug extends HttpServlet {
	// 请求数据库
	DBUtils dbUtils = new DBUtils();
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
		response.setContentType("text/html;charset=utf-8");

		dbUtils.openConnect();
		PrintWriter out = response.getWriter();
		// 获取客户端传过来的参数
		String name = new String(request.getParameter("name").getBytes("ISO-8859-1"), "UTF-8");
		String price = new String(request.getParameter("price").getBytes("ISO-8859-1"), "UTF-8");
		String type = new String(request.getParameter("type").getBytes("ISO-8859-1"), "UTF-8");
		String describe = new String(request.getParameter("describe").getBytes("ISO-8859-1"), "UTF-8");
		String amount = request.getParameter("amount");
		String attribute = request.getParameter("attribute");
		// String picture = request.getParameter("picture");
		String index = request.getParameter("index");
		String upOrMo = request.getParameter("&upOrMo=");// 代表是上传药品还是修改药品，up为上传，mo为修改
		System.out.println("upload.picture.drug.name :" + name);

		if (attribute == null || attribute.equals("") || name == null || name.equals("") || price == null
				|| price.equals("") || type == null || type.equals("") || describe == null || describe.equals("")
				|| amount == null || amount.equals("") || index == null || index.equals("")) {
			System.out.println("参数为空");
			return;
		}

		// 将药品的属性进行更改
		if (attribute.equals("OTC")) {
			attribute = "true";
		} else {
			attribute = "false";
		}

		// 打开数据库连接
		BaseBean data = new BaseBean(); // 基类对象，回传给客户端的json对象
		UserBean userBean = new UserBean(); // user的对象

			// 获取当前系统时间，用来当做新上传药品的id
			Date day = new Date();
			String id = String.valueOf(day.getTime());
			if (!dbUtils.insertDataToDB_Drug(name, price, type, describe, amount, index, attribute, id)) {
				out.println("添加成功");
			} else {
				out.println("添加失败");
			}
	
		out.close();
		dbUtils.closeConnect(); // 关闭数据库连接}
	}
}