package com.im.servlet;

import java.io.*;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.*;
import javax.servlet.http.*;

import com.google.gson.Gson;
import com.im.db.DBUtils;
import com.im.domain.BaseBean;

import javax.servlet.annotation.*;

@WebServlet("/PictureUpload")
@MultipartConfig(fileSizeThreshold = 4096)
public class PictureUpload extends HttpServlet {

	
	// 返回上传来的文件名
	private String getFilename(Part part) {
		String fname = null;
		// 返回上传的文件部分的content-disposition请求头的值
		String header = part.getHeader("content-disposition");
		System.out.println("header:" + header);
		// 返回不带路径的文件名
		fname = "." + header.substring(header.lastIndexOf(".") + 1, header.length() - 1);
		return fname;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	@SuppressWarnings("null")
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		DBUtils a = new DBUtils();

		a.openConnect();
		BaseBean data = new BaseBean(); // 基类对象，回传给客户端的json对象
		String type = request.getParameter("type");
		String id = request.getParameter("id");
		String message = "";
		String sql=null;
		// 返回Web应用程序文档根目录
		PrintWriter out = response.getWriter();
		String path = this.getServletContext().getRealPath("/");
		System.out.println("picture.type:"+type);
		
		if (type == null) {
			message = "参数为空";
			out.println("type==null");
		}else if (type.equals("Drug")) {//如果上传的药品图片	
			path = path + "DrugPicture";
		} else if (type.equals("Icon_Stu")) {//如果上传的学生头像
			path = path + "StuIcon";
//			type = "Icon/Stu";
			if (id == null || id.equals("")) {
				return;
			}
		} else if (type.equals("Icon_Doc")) {//如果上传的医生头像
			path = path + "DocIcon";
			// path = "/usr/local/tomcat/tomcat/webapps/Picture/Icon/Doc";
//			type = "Icon/Doc";
			if (id == null || id.equals("")) {
				return;
			}
		} else if (type.equals("Icon_License")) {//如果上传的医生证件
			path = path + "DocLicenseIcon";
			// path = "/usr/local/tomcat/tomcat/webapps/Picture/Icon/License";
//			type = "Icon/License";
			if (id == null || id.equals("")) {
				return;
			}
		} else {
			message = "参数错误";
			return;
		}
		Part p = request.getPart("fileName");

		// out.println("Path:"+path);
		System.out.println("the size of p is :" + p.getSize());
		if (p.getSize() > 4096 * 4096) { // 上传的文件不能超过1MB大小
			p.delete();
			message = "文件太大，不能上传！";
			data.setCode(-1);
			data.setMsg(message);
		} else {
			// 文件存储在文档根目录下member子目录中会员号子目录中
			// path = path + mnumber;
			Date date = new Date();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
			String datename = dateFormat.format(date);
			// out.println(datename);
			File f = new File(path);
			if (!f.exists()) { // 若目录不存在，则创建目录
				f.mkdirs();
			}
			String fname = Integer.toString(((int) (Math.random() * 100))) + getFilename(p); // 得到文件名
			fname = datename + fname;

			// out.println("fname:"+fname);
			try {
				String pa = path + "/" + fname;
				System.out.println("the pa is first" + pa);
				p.write(pa); // 将上传的文件写入磁盘
				System.out.println("he pa ist " + pa);
			} catch (Exception ee) {
				out.println("Exception:" + ee.toString());
			}
			System.out.println("path:" + path);

			String url;
			// out.println("message:"+message);

			try {// 该消息返回的是上传药品的图片地址
				if (type.equals("Drug")) {
					data.setCode(0);
					data.setMsg("IM/DrugPicture" + "/" + fname);
				} else if (type.equals("Icon_Stu")) {// 如果是上传的学生头像
					//首先删除数据库中原有的学生头像图片					
					sql="select Stu_Icon from im_stu where Stu_No='"+id+"'";
					ResultSet rs=a.getData(sql);	
					System.out.println("picture.upload.stuicon.before");
//					System.out.println("boolean:"+rs.next());
					System.out.println("boolean.null:"+rs==null);
					System.out.println("picture.upload.stuicon.before1");
					System.out.println("boolean:"+rs.next());
					System.out.println("picture.upload.stuicon.before2");
					rs.previous();
					System.out.println("picture.upload.stuicon.before3");
						if(rs.next()&&(rs.getString("Stu_Icon")!=null)) {//如果原来没有									
							String stuIcon=rs.getString("Stu_Icon").trim().substring(10).replace("/", "\\");
						path=path+stuIcon;
						System.out.println("deletedrug.picture.path:"+path);
						File file=new File(path);
						if(file.exists()) {
							file.delete();
						}						
						}
							System.out.println("picture.upload.stuicon.later");
						rs.close();
					
						//将新图片文件的名字保存到数据库中						
						url = "IM/StuIcon" + "/" + fname;					
						sql = "update im_stu set Stu_Icon = '" + url + "' where Stu_No = '" + id + "';";
						System.out.println("picture.upload.stuicon:"+sql);
						try {
							a.updateDataToDB(sql);
							data.setCode(0);
							data.setMsg("更新成功！ url:" + url);
							System.out.println("学生数据库更新成功！");
						} catch (Exception ee) {
							ee.printStackTrace();
							data.setCode(-1);
							data.setMsg("更新失败！");
						}
					} // 如果上传的是医生的头像
					else if (type.equals("Icon_Doc")) {
						//首先删除数据库中原有的医生头像图片					
						sql="select Doc_Icon from im_doc where Doc_No='"+id+"'";
						ResultSet rs=a.getData(sql);		
							if(rs.next()) {
								String docIcon=rs.getString("Doc_Icon").trim().substring(10).replace("/", "\\");
							path=path+docIcon;
							System.out.println("deletedrug.picture.path:"+path);
							File file=new File(path);
							if(file.exists()) {
								file.delete();
							}
							}
							rs.close();
							//将新图片文件的名字保存到数据库中	
						url = "IM/DocIcon" + "/" + fname;						
						 sql = "update im_doc set Doc_Icon = '" + url + "' where Doc_No = '" + id + "';";
						try {
							a.updateDataToDB(sql);
							data.setCode(0);
							data.setMsg("更新成功！ url:" + url);
							System.out.println("医生数据库更新成功！");
						} catch (Exception ee) {

							ee.printStackTrace();
							data.setCode(-1);
							data.setMsg("更新失败！");
						}
					} else if (type.equals("Icon_License")) {// 如果上传的是医生证书
						//首先删除数据库中原有的医生证件图片					
						sql="select Doc_License from im_doc where Doc_No='"+id+"'";
						ResultSet rs=a.getData(sql);		
							if(rs.next()) {
								String docLicense=rs.getString("Doc_License").trim().substring(17).replace("/", "\\");
							path=path+docLicense;
							System.out.println("deletedrug.picture.path:"+path);
							File file=new File(path);
							if(file.exists()) {
								file.delete();
							}
							}
							rs.close();
							//将新图片文件的名字保存到数据库中					
						url = "IM/DocLicenseIcon" + "/" + fname;
						 sql = "update im_doc set Doc_License = '" + url + "' where Doc_No = '" + id + "';";
						try {
							a.updateDataToDB(sql);
							data.setCode(0);
							data.setMsg("更新成功！ url:" + url);
							System.out.println("医生数据库更新成功！");
						} catch (Exception ee) {
							ee.printStackTrace();
							data.setCode(-1);
							data.setMsg("更新失败！");
						}
					}
					// System.out.println("type不正确！");
				
			} catch (Exception ee) {

			}
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
		
		a.closeConnect();
	}
}

