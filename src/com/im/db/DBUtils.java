package com.im.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mysql.cj.jdbc.result.ResultSetMetaData;


public class DBUtils {
	private Connection conn;
	private String url = "jdbc:mysql://127.0.0.1:3306/im?serverTimezone=UTC&useUnicode=true&characterEncoding=utf8"; // 指定连接数据库的URL
	private String user = "root"; // 指定连接数据库的用户名
	private String password = "111111"; // 指定连接数据库的密码
	private Statement sta;
	private ResultSet rs; // 打开数据库连接


	public void openConnect() {
		try {
			// 加载数据库驱动
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection(url, user, password);// 创建数据库连接
			if (conn != null) {
				System.out.println("数据库连接成功"); // 连接成功的提示信息
			}
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getMessage());
		}
	}


	// 获得查询user表后的数据集
	public ResultSet getUser() {
		// 创建 statement对象
		try {
			
			sta = conn.createStatement(); // 执行SQL查询语句
			rs = sta.executeQuery("select * from im_stu");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	
	// 获得查询表后的数据集
		public ResultSet getData(String a) {
			// 创建 statement对象
			try {
				System.out.println(sta);
				sta = conn.createStatement(); // 执行SQL查询语句
				rs = sta.executeQuery(a);
			} catch (SQLException e) {
				System.out.println("getData错误");
				e.printStackTrace();
			}
			if(rs == null) {
				System.out.println("getData错误");
			}
			return rs;
		}
		
	
		
	

	// 判断数据库中是否存在某个用户名及其密码,注册和登录的时候判断
	public boolean isExistInDB(String no, String pwd) {
		boolean isFlag = false; // 创建 statement对象
		try {
			System.out.println("判断用户名密码");
			sta = conn.createStatement(); // 执行SQL查询语句
			rs = sta.executeQuery("select * from im_stu");// 获得结果集
			if (rs != null) {
				while (rs.next()) { // 遍历结果集
					if (rs.getString("Stu_No").equals(no)) {
						if (rs.getString("Stu_Pwd").equals(pwd)) {
							isFlag = true;
							break;
						}
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			isFlag = false;
		}
		return isFlag;


	}

	//获取数据库中指定id的医生名字
	public String[] getDocNameAndPicture(String id) {
	String[] data=new String[2];

		try {
			sta = conn.createStatement(); // 执行SQL查询语句
			rs = sta.executeQuery("select Doc_Name , Doc_Icon from im_doc where Doc_No="+id);
			if (rs != null) {
				while (rs.next()) { // 遍历结果集
				data[0]=rs.getString("Doc_Name");
				data[1]=rs.getString("Doc_Icon");
				}
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}// 获得结果集
		return data;
	}
	
	// 判断数据库中是否存在某个用户名及其密码,注册和登录的时候判断
		public boolean isExistInDB_Doc(String no, String pwd) {
			boolean isFlag = false; // 创建 statement对象
			try {
				System.out.println("判断用户名密码");
				sta = conn.createStatement(); // 执行SQL查询语句
				rs = sta.executeQuery("select * from im_doc");// 获得结果集
				if (rs != null) {
					while (rs.next()) { // 遍历结果集
						if (rs.getString("Doc_No").equals(no)) {
							if (rs.getString("Doc_Pwd").equals(pwd)) {
								isFlag = true;
								break;
							}
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
				isFlag = false;
			}
			return isFlag;


		}

	
	public boolean insertDataToDB(String no, String name, String pwd, String sex, String birth, String height, String weight, String sno) {
		String sql = " insert into im_stu ( Stu_No , Stu_Name , Stu_Pwd , Stu_Sex , Stu_Birth , Stu_Height , Stu_Weight , Stu_Sno ) "
				+ "values ( '" + no + "','" + name + "', '" + pwd + "', '" + sex + "', '" + birth + "', '" + height + "', '" + weight + "', '" + sno + "' )";
		System.out.println(sql);
		try {
			sta = conn.createStatement();
			// 执行SQL查询语句
			return sta.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
		
	public boolean insertDataToDB_Drug(String name, String price, String type, String describe, String amount, String index,String attribute,String id) {
		String sql = " insert into im_drug ( Drug_Name , Drug_Price , Drug_Type , Drug_Describe , Drug_Amount , Drug_Index,Drug_OTC,Drug_Id) "
				+ "values ( '" + name + "','" + price + "', '" + type + "', '" + describe + "', '" + amount + "', '" + index +"','"+attribute+"','"+id+ "');";
		System.out.println(sql);
		try {
			sta = conn.createStatement();
			// 执行SQL查询语句
			return sta.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	public boolean updateDataToDB(String sql) {
		
		System.out.println(sql);
		try {
			sta = conn.createStatement();
			System.out.println("数据库更新成功");
			return sta.execute(sql);
		}
		catch (SQLException e) {
			System.out.println("数据库更新失败");
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	
	public boolean updateDataToDB_Adress(String name, String address, String phone) {
		String sql = "update im_stu set Stu_Address = '"+address+"',Stu_Phone = '"+phone+"' where Stu_No = '"+name+"';";
		System.out.println(sql);
		try {
			sta = conn.createStatement();
			System.out.println("数据库更新成功");
			return sta.execute(sql);
		}
		catch (SQLException e) {
			System.out.println("数据库更新失败");
			e.printStackTrace();
		}
		return false;
	}
	
	
	
	
	
	
	
	public boolean pwdModify(String no,String pwd, String isStu) {
		String sql = null;
		if(isStu.equals("true")) {
			sql = "update im_stu set Stu_Pwd='"+pwd+"' where Stu_No='"+no+"';";
		}
		else if(isStu.equals("false")) {
			sql = "update im_doc set Doc_Pwd='"+pwd+"' where Doc_No='"+no+"';";
		}
		System.out.print(sql);
		try {
			sta = conn.createStatement();
			System.out.println("数据库更新成功");
			return sta.execute(sql);
		}
		catch (SQLException e) {
			System.out.println("数据库更新失败");
			e.printStackTrace();
		}
		return false;
	}

	// 关闭数据库连接
	public void closeConnect() {
		try {
			if (rs != null) {
				rs.close();
			}
			if (sta != null) {
				sta.close();
			}
			if (conn != null) {
				conn.close();
			}
			System.out.println("关闭数据库连接成功");
		} catch (SQLException e) {
			System.out.println("Error: " + e.getMessage());
		}
	}
	
	
	public boolean isExistInDB_Register(String no) {
		boolean isFlag = false; // 创建 statement对象
		try {
			System.out.println("判断用户名");
			sta = conn.createStatement(); // 执行SQL查询语句
			rs = sta.executeQuery("select * from im_stu");// 获得结果集
			if (rs != null) {
				while (rs.next()) { // 遍历结果集
					if (rs.getString("Stu_No").equals(no)) {
						
							isFlag = true;
							break;
						
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			isFlag = false;
		}
		return isFlag;
	}
	
	
	public boolean isExistInDB_Drug(String name) {
		boolean isFlag = false; // 创建 statement对象
		try {
			System.out.println("判断药品名:");
			sta = conn.createStatement(); // 执行SQL查询语句
			rs = sta.executeQuery("select * from im_drug");// 获得结果集
			if (rs != null) {
				while (rs.next()) { // 遍历结果集
					if (rs.getString("Drug_name").equals(name)) {
						
							isFlag = true;
							break;
						
					}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			isFlag = false;
		}
		return isFlag;
	}
	
	
	
    public String resultSetToJson(ResultSet rs,String code,String msg) throws SQLException,JSONException
    {
       // json数组
       JSONArray array = new JSONArray();
      
       // 获取列数
       try {
       ResultSetMetaData metaData = (ResultSetMetaData) rs.getMetaData();
       
       int columnCount = metaData.getColumnCount();
      
       // 遍历ResultSet中的每条数据
       JSONObject jsonObj0 = new JSONObject();
       jsonObj0.put("Code", code);
       jsonObj0.put("Msg", msg);
       array.put(jsonObj0);
        while (rs.next()) {
        	JSONObject jsonObj = new JSONObject();
            // 遍历每一列
            for (int i = 1; i <= columnCount; i++) {
                String columnName =metaData.getColumnLabel(i);
                String value = rs.getString(columnName);
                jsonObj.put(columnName, value);            
            } 
            array.put(jsonObj); 
        }
       }
       catch(Exception ee) {
    	   System.out.println(ee.getMessage()+"122"+ee.toString());
    	   //System.out.println("122");
       }
       return array.toString();
    }
    

    
    public String hexStr2Str(String hexStr) {
		
	    String str = "0123456789ABCDEF";
	    char[] hexs = hexStr.toCharArray();
	    byte[] bytes = new byte[hexStr.length() / 2];
	    int n;
	    for (int i = 0; i < bytes.length; i++) {
	        n = str.indexOf(hexs[2 * i]) * 16;
	        n += str.indexOf(hexs[2 * i + 1]);
	        bytes[i] = (byte) (n & 0xff);
	    }
	    return new String(bytes);
	    
	}
	


}