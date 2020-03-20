package com.im.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.im.db.DBUtils;

/**
 * Servlet implementation class UploadOrder
 */
@WebServlet("/UploadOrder")
public class UploadOrder extends HttpServlet {
	private boolean flag = true;
	private DBUtils a = new DBUtils();
	private static final long serialVersionUID = 1L;
	private String sql;

	public UploadOrder() {
		super();
		// TODO Auto-generated constructor stub
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		String order = request.getParameter("order");
		String type = request.getParameter("type");
		a.openConnect();
		
		try {
			JSONArray jsonArray = new JSONArray(order);
			JSONObject object = jsonArray.getJSONObject(0);
			String stuId = object.getString("stuId");
			Date time = new Date();
			for (int i = 1; i < jsonArray.length(); i++) {
				object = jsonArray.getJSONObject(i);
				String drugId = object.getString("drugId");
				String drugAmount = object.getString("drugAmount");
				sql = "insert into im_order (DrugId,DrugAmount,DrugTime,StuId,Type,StuDelete,DocDelete) values ('"
						+ drugId + "','" + drugAmount + "','" + String.valueOf(time.getTime()) + "','" + stuId + "','"
						+ type + "','false','false')";
				System.out.println("orderSql is" + sql);
				a.insertOrdrToDataBase(sql);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			flag = false;
		}
		a.closeConnect();
		PrintWriter out = response.getWriter();
		if (flag) {
			out.println("订单添加成功");
		} else {
			out.println("订单添加失败");
		}
		out.close();
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
