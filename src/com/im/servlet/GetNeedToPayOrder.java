package com.im.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.im.db.DBUtils;
import com.mysql.cj.jdbc.result.ResultSetMetaData;

/**
 * Servlet implementation class GetNeedToPayOrder
 */
@WebServlet("/GetNeedToPayOrder")
public class GetNeedToPayOrder extends HttpServlet {
	 

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 DBUtils a = new DBUtils();
		 JSONArray array;
		DecimalFormat df = new DecimalFormat("##0.00");
		
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("UTF-8");	
		response.setContentType("text/html;charset=utf-8");
		a.openConnect();
		PrintWriter out=response.getWriter();
		array=new JSONArray();
		String sql=null;
	
		String type=request.getParameter("type");
		
		switch (type) {
		
		case "getOrder":{//学生获取待付款订单
			String id=request.getParameter("id");
			sql="select Drug_Name,Drug_Price,Drug_Index,DrugAmount,DrugTime from im_order,im_drug where im_order.DrugId=im_drug.Drug_Id and Type='notPost' and StuDelete='false' and im_order.StuId='"+id+"' order by DrugTime desc";
			 handleRS(a.getData(sql),array,df);	
			 out.println(array.toString());
			break;
		}
		case "historyOrder":{//学生获取历史订单
			String id=request.getParameter("id");
			 sql="select Drug_Name,Drug_Price,Drug_Index,DrugAmount,DrugTime from im_order,im_drug where im_order.DrugId=im_drug.Drug_Id and Type='received' and StuDelete='false' and im_order.StuId='"+id+"' order by DrugTime desc";
			 handleRS(a.getData(sql),array,df);	
			 out.println(array.toString());
			break;			
		}
		case "finishPay":{//学生付款款之后更新数据库
				String id=request.getParameter("id");
			String orderId=request.getParameter("orderId");
			sql="update im_order set Type='finishPay' where StuId='"+id+"' and StuDelete='false' and DrugTime='"+orderId+"'";
			System.out.println("finish"+sql);
			boolean flag=a.insertOrdrToDataBase(sql);		
			if(flag) {
				out.println("修改成功");
			}
			else {
				out.println("修改失败");
			}
			break;
		}
		case "receive":{//学生点击收货按钮进行更新操作
			String id=request.getParameter("id");
			String orderId=request.getParameter("orderId");
			sql="update im_order set Type='received' where StuId='"+id+"' and StuDelete='false' and DrugTime='"+orderId+"'";
			System.out.println("finish"+sql);
			boolean flag=a.insertOrdrToDataBase(sql);		
			if(flag) {
				out.println("收货成功");
			}
			else {
				out.println("收货失败");
			}
			
			break;
		}
		case "stuNeedReceive":{//学生获取待收货订单
			String id=request.getParameter("id");
			 sql="select Drug_Name,Drug_Price,Drug_Index,DrugAmount,DrugTime from im_order,im_drug where im_order.DrugId=im_drug.Drug_Id and Type='havePost' and StuDelete='false' and im_order.StuId='"+id+"' order by DrugTime desc";
			 System.out.println("stuNotPost sql:"+sql);
			 handleRS(a.getData(sql),array,df);	
			 out.println(array.toString());
			break;
		}
		case "stuNotPost":{//学生获取代发货订单
			String id=request.getParameter("id");
			 sql="select Drug_Name,Drug_Price,Drug_Index,DrugAmount,DrugTime from im_order,im_drug where im_order.DrugId=im_drug.Drug_Id and Type='finishPay' and StuDelete='false' and im_order.StuId='"+id+"' order by DrugTime desc";
			 System.out.println("stuNotPost sql:"+sql);
			 handleRS(a.getData(sql),array,df);	
			 out.println(array.toString());
			break;
		}
		case "notPost":{//医生查看待发货的订单
			 sql="select Stu_Name,Stu_Phone,Stu_Address,Drug_Name,DrugAmount,Drug_Index,Drug_Price,DrugTime from im_order,im_drug,im_stu where im_order.DrugId=im_drug.Drug_Id and im_order.StuId=im_stu.Stu_No and Type='finishPay' and DocDelete='false' order by DrugTime asc";
			
			 handleNotPostRS(a.getData(sql),array);
			 out.println(array.toString());
			break;
		}
		case "havePost":{//医生点击已发货按钮
			String orderId=request.getParameter("orderId");
			sql="update im_order set Type='havePost' where DocDelete='false' and DrugTime='"+orderId+"'";
			boolean flag=a.insertOrdrToDataBase(sql);
			if(flag) {
				out.println("发货成功");
			}
			else {
				out.println("发货失败");
			}
			break;
		}
		case "delete":{//医生点击删除订单按钮
			String orderId=request.getParameter("orderId");		
			boolean flag=docDeleteOrder(orderId,a);
			if(flag) {
				out.println("删除成功");
			}
			else {
				out.println("删除失败");
			}
			break;
		}
		case "stuDelete":{//学生删除历史订单		
			String orderId=request.getParameter("orderId");
			boolean flag=stuDeleteOrder(orderId,a);
			if(flag) {
				out.println("删除成功");
			}
			else {
				out.println("删除失败");
			}
			break;
			
		}
		case "getHavePost":{//获取医生已发货的订单
			 sql="select Stu_Name,Stu_Phone,Stu_Address,Drug_Name,DrugAmount,Drug_Index,Drug_Price,DrugTime from im_order,im_drug,im_stu where im_order.DrugId=im_drug.Drug_Id and im_order.StuId=im_stu.Stu_No and Type='havePost' and DocDelete='false' order by DrugTime asc";
			 handleNotPostRS(a.getData(sql),array);
			 out.println(array.toString());
			break;
		}					
		default:
			break;
		}
		
		out.close();
		a.closeConnect();
	}
	
	//医生删除订单的方法
	private boolean docDeleteOrder(String orderId, DBUtils a) {
		//首先将医生的删除订单标记为置为true
		String sql="update im_order set DocDelete='true' where DrugTime='"+orderId+"'";
		boolean flag=a.insertOrdrToDataBase(sql);
		if(flag) {
			//查询学生的删除标记为是否为true；
		sql="select StuDelete from im_order where DrugTime='"+orderId+"'";
		ResultSet rs=a.getData(sql);
		try {
			while(rs.next()) {
				//如果学生的删除标记为也置位true，则将该订单删除
				if(rs.getString("StuDelete").trim().equals("true")) {
					sql="delete from im_order where DrugTime='"+orderId+"'";
					flag=a.insertOrdrToDataBase(sql);
					if(flag) {
						return true;
					}
					else {
						return false;
					}
				}
				else {//如果学生的标记位为false(学生还未删除此订单)
					return true;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		}
		else {
			return false;
		}	
		return false;
	}
	
	//学生删除订单的方法
	private boolean stuDeleteOrder(String orderId,DBUtils a) {
		//首先将医生的删除订单标记为置为true
		String sql="update im_order set StuDelete='true' where DrugTime='"+orderId+"'";
		System.out.println("stusql"+sql);
		boolean flag=a.insertOrdrToDataBase(sql);
		if(flag) {
			//查询医生的删除标记为是否为true；
		sql="select DocDelete from im_order where DrugTime='"+orderId+"'";
		ResultSet rs=a.getData(sql);
		try {
			while(rs.next()) {
				//如果医生的删除标记为也置位true，则将该订单删除
				if(rs.getString("DocDelete").trim().equals("true")) {
					sql="delete from im_order where DrugTime='"+orderId+"'";
					flag=a.insertOrdrToDataBase(sql);
					if(flag) {
						return true;
					}
					else {
						return false;
					}
				}
				else {//如果医生的标记位为false(医生还未删除此订单)
					return true;
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		}
		else {
			return false;
		}	
		return false;
	}
	
	private void handleNotPostRS(ResultSet rs,JSONArray array) {		   
		   JSONObject orderInfo=new JSONObject();
			String time=null;
			String newTime=null;
		   try {
			while(rs.next()) {
				newTime=rs.getString("DrugTime");
				if(time==null||(!time.equals(newTime))) {//如果第一次遍历一个新的订单，则统计收货地址信息
					time=newTime;
					orderInfo=new JSONObject();
					orderInfo.put("orderTime", rs.getString("DrugTime"));
					orderInfo.put("stuName",rs.getString("Stu_Name"));
					orderInfo.put("stuPhone", rs.getString("Stu_Phone"));
					orderInfo.put("stuAddress", rs.getString("Stu_Address"));
					array.put(orderInfo);
				}		
					//每一次循环都要把药品信息添加到数组中
					orderInfo=new JSONObject();
					orderInfo.put("drugName", rs.getString("Drug_Name"));
					orderInfo.put("drugPicture", rs.getString("Drug_Index"));
					orderInfo.put("drugAmount", rs.getString("DrugAmount"));
					orderInfo.put("drugPrice", rs.getString("Drug_Price"));
					array.put(orderInfo);
				
			   }
			System.out.println("array:"+array.toString());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void handleRS(ResultSet rs,JSONArray array,DecimalFormat df ) {
		System.out.println("result0:"+array.toString());
	    ResultSetMetaData metaData;
		try {
			metaData = (ResultSetMetaData) rs.getMetaData();
			int columnCount = metaData.getColumnCount();
			String time=null;
			String newTime=null;
			double totalMoney=0.00;
			double singlePrice=0.00;
			int amount=0;
			JSONObject jsonObj = null;
		     // 遍历ResultSet中的每条数据     
			System.out.println("seq:"+1);
	        while (rs.next()) {	        
	        			newTime=rs.getString("DrugTime");
	        	     if(((time==null)||(!time.equals(newTime)))) {//如果不是同一个订单
	                	if(time!=null) {//如果不是第一次遍历结果集,则将该订单之前的订单存放到json数组中
	                		JSONObject jsonObjOrder=new JSONObject();	                
							jsonObjOrder.put("orderTime", time);						
	                		jsonObjOrder.put("orderPrice", String.valueOf(df.format(totalMoney)));
	                		System.out.println("time:"+time);
	                		System.out.println("price:"+totalMoney);
	                		array.put(jsonObjOrder);	                			               	                	                
	                	}
	                		//接下来将统计新订单的第一条信息
	                		time=newTime;
	                		amount=Integer.valueOf(rs.getString("DrugAmount"));
		                	singlePrice=Double.valueOf(rs.getString("Drug_Price"));
		                	totalMoney=amount*singlePrice;//如果是新订单的第一个药品，则将总价格置位等于号   
	                }
	                else {//如果是同一个订单，则统计同一个订单内的价格
	                	amount=Integer.valueOf(rs.getString("DrugAmount"));
	                	singlePrice=Double.valueOf(rs.getString("Drug_Price"));
	                	totalMoney+=amount*singlePrice;
	                	
	                }
	        	     
	        		jsonObj=new JSONObject();	
	            // 遍历每一列
	            for (int i = 1; i <= columnCount; i++) {	           	                  
	                String columnName =metaData.getColumnLabel(i);
	                String value = rs.getString(columnName);
	                //当获取到DrugTime这一列的时候检查是不是同一个时间	           
	                jsonObj.put(columnName, value);    	         
	            } 
	            array.put(jsonObj); 
	        }
	        System.out.println("result3:"+array.toString());
	        System.out.println("seq:"+2);
	        //当while循环结束之后，把最后一个订单时间和金额放到json数组中
	        JSONObject jsonObjOrder=new JSONObject();	                
			jsonObjOrder.put("orderTime", time);						
    		jsonObjOrder.put("orderPrice", String.valueOf(df.format(totalMoney)));
    		System.out.println("time1"+time);
    		System.out.println("price1:"+totalMoney);
    		array.put(jsonObjOrder);
    		System.out.println("result4:"+array.toString());
    	
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (JSONException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
