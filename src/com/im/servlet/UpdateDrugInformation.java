package com.im.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;

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
	DBUtils dbUtils = new DBUtils();
	private static final long serialVersionUID = 1L;
	String sql;

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
		String path = this.getServletContext().getRealPath("/");
		// 获取客户端传过来的参数
		String name = new String(request.getParameter("name").getBytes("ISO-8859-1"), "UTF-8");
		String price = new String(request.getParameter("price").getBytes("ISO-8859-1"), "UTF-8");
		String type = new String(request.getParameter("type").getBytes("ISO-8859-1"), "UTF-8");
		String describe = new String(request.getParameter("describe").getBytes("ISO-8859-1"), "UTF-8");
		String amount = request.getParameter("amount");
		String drugId = request.getParameter("drugId");
		String index = request.getParameter("index");
		String attribute = request.getParameter("attribute");//药品的属性（OTC？）
		response.setContentType("text/html;charset=utf-8");
		if (name == null || name.equals("") || price == null || price.equals("") || type == null || type.equals("")
				|| describe == null || describe.equals("") || amount == null || amount.equals("") || index == null
				|| index.equals("")) {
			System.out.println("参数为空");
			return;
		}
		// 准备删除原有的药品图片
		sql = "select Drug_Index from im_drug where Drug_Id='" + drugId + "'";
		System.out.println("deletedrug.select.sql:" + sql);
		ResultSet rs = dbUtils.getData(sql);
		try {
			if (rs.next()) {
				String pictureNameString = rs.getString("Drug_Index").trim().substring(3).replace("/", "\\");
				path = path + pictureNameString;
				System.out.println("deletedrug.picture.path:" + path);
				File file = new File(path);
				if (file.exists()) {
					file.delete();
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		// 将药品的属性进行更改
				if (attribute.equals("OTC")) {
					attribute = "true";
				} else {
					attribute = "false";
				}
				// 准备更新药品的数据库信息
		sql = "update im_drug set Drug_Name='" + name + "',Drug_Price='" + price + "', Drug_Type='" + type
				+ "', Drug_Describe='" + describe + "', Drug_Amount='" + amount + "',Drug_Index='" + index
				+ "',Drug_OTC='"+attribute+"' where Drug_Id='" + drugId + "'";
		// 请求数据库
		System.out.println("update.drug.sql:" + sql);
		if (dbUtils.updateDataToDB(sql)) {
			out.println("修改成功");
			System.out.println("update.drug.success");
		} else {
			out.println("修改失败");
			System.out.println("update.drug.fault");
		}
		out.close();
		dbUtils.closeConnect(); // 关闭数据库连接}
	}

}